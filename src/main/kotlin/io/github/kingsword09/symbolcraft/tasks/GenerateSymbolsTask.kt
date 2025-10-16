package io.github.kingsword09.symbolcraft.tasks

import io.github.kingsword09.symbolcraft.converter.*
import io.github.kingsword09.symbolcraft.download.SvgDownloader
import io.github.kingsword09.symbolcraft.model.IconConfig
import io.github.kingsword09.symbolcraft.utils.PathUtils
import io.github.kingsword09.symbolcraft.plugin.SymbolCraftExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/**
 * Gradle task responsible for downloading icons from multiple libraries and converting them into Compose APIs.
 *
 * The task is cacheable and honours [io.github.kingsword09.symbolcraft.plugin.SymbolCraftExtension] settings supplied via the plugin DSL.
 *
 * @property extension lazily provides the extension backing the current project configuration.
 * @property outputDir destination directory for the generated Kotlin sources.
 * @property cacheDirectory path used for storing SVG payloads between executions.
 * @property gradleUserHomeDir exposed for cache resolution when Gradle configuration cache is enabled.
 * @property projectBuildDir points at the consuming project's `build` directory for relative cache resolution.
 */
@CacheableTask
abstract class GenerateSymbolsTask : DefaultTask() {

    @get:Internal
    abstract val extension: Property<SymbolCraftExtension>

    @get:Input
    val symbolsConfigHash: String
        get() = extension.get().getConfigHash()

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val cacheDirectory: Property<String>

    @get:Input
    abstract val gradleUserHomeDir: Property<String>

    @get:Input
    abstract val projectBuildDir: Property<String>

    /**
     * Downloads the requested icons and regenerates the Kotlin sources.
     *
     * The implementation uses structured concurrency to download SVGs in parallel and
     * cleans stale cache entries when safe to do so.
     */
    @TaskAction
    fun generate() = runBlocking {
        val ext = extension.get()
        val config = ext.getIconsConfig()
        val packageName = ext.packageName.get()
        val cacheDirPath = cacheDirectory.get()
        val projectBuildDirPath = projectBuildDir.get()

        // Resolve cache directory: support both absolute and relative paths
        val cacheBaseDir = PathUtils.resolveCacheDirectory(cacheDirPath, projectBuildDirPath)
        val tempDir = File(cacheBaseDir, "temp-svgs")
        val svgCacheDir = File(cacheBaseDir, "svg-cache")
        val outputDirFile = outputDir.get().asFile

        logger.lifecycle("🎨 Generating icons...")
        logger.lifecycle("📊 Icons to generate: ${config.values.sumOf { it.size }} total")
        logger.debug("📂 Cache directory: ${cacheBaseDir.absolutePath}")

        // Clean old generated files to ensure fresh generation
        cleanOldGeneratedFiles(outputDirFile, packageName)

        // Clean unused cache files before generating
        // Skip if using shared cache (absolute path or outside build directory) to avoid conflicts
        if (ext.cacheEnabled.get() && shouldCleanCache(cacheBaseDir, projectBuildDirPath)) {
            cleanUnusedCache(svgCacheDir, config)
        }

        val downloader = SvgDownloader(
            cacheDirectory = svgCacheDir.absolutePath,
            cacheEnabled = ext.cacheEnabled.get(),
            cdnBaseUrl = ext.cdnBaseUrl.get(),
            maxRetries = ext.maxRetries.get(),
            retryDelayMs = ext.retryDelayMs.get(),
            logger = { message -> logger.debug(message) }
        )

        try {
            // Clean and prepare temp directory
            if (tempDir.exists()) tempDir.deleteRecursively()
            tempDir.mkdirs()

            // Group icons by library
            val iconsByLibrary = groupIconsByLibrary(config)
            logger.debug("📚 Libraries found: ${iconsByLibrary.keys.joinToString()}")

            // Download SVGs with progress tracking (all libraries together)
            val downloadStats = downloadSvgsParallel(downloader, config, tempDir)
            logDownloadStats(downloadStats)

            // Convert SVGs to Compose code, organized by library
            convertSvgsToComposeByLibrary(tempDir, outputDirFile, packageName, iconsByLibrary, ext)

            // Log cache statistics
            val cacheStats = downloader.getCacheStats()
            logger.lifecycle("📦 SVG Cache: ${cacheStats.fileCount} files, ${String.format("%.2f", cacheStats.totalSizeMB)} MB")

        } catch (e: Exception) {
            logger.error("❌ Generation failed: ${e.message}")
            logger.error("   Stack trace: ${e.stackTraceToString()}")

            // Provide helpful error guidance
            when {
                e.message?.contains("network", ignoreCase = true) == true -> {
                    logger.error("   💡 Network issue detected. Check internet connection and try again.")
                }
                e.message?.contains("cache", ignoreCase = true) == true -> {
                    logger.error("   💡 Cache issue detected. Try running with --rerun-tasks or clearing cache.")
                }
                e.message?.contains("SVG", ignoreCase = true) == true -> {
                    logger.error("   💡 SVG processing issue. Check if the requested icons exist in Material Symbols.")
                }
                else -> {
                    logger.error("   💡 Unexpected error. Please check configuration and try again.")
                }
            }

            throw e
        } finally {
            try {
                downloader.cleanup()
            } catch (cleanupException: Exception) {
                logger.warn("⚠️ Warning: Failed to cleanup downloader: ${cleanupException.message}")
            }
        }
    }

    /**
     * Determine if cache cleanup should be performed
     *
     * Cache cleanup is SKIPPED if:
     * - Cache is located outside the project build directory (shared cache)
     * - This prevents conflicts when multiple projects/modules share the same cache
     *
     * Cache cleanup is PERFORMED if:
     * - Cache is inside the project build directory (project-local cache)
     *
     * @param cacheBaseDir The resolved cache directory
     * @param projectBuildDir The project build directory path
     * @return true if cleanup should be performed, false if it should be skipped
     */
    private fun shouldCleanCache(cacheBaseDir: File, projectBuildDir: String): Boolean {
        val buildDir = File(projectBuildDir)
        val isInsideBuildDir = PathUtils.isCacheInsideBuildDir(cacheBaseDir, buildDir)

        if (!isInsideBuildDir) {
            logger.lifecycle("ℹ️  Cache cleanup skipped: Using shared cache outside build directory")
            logger.lifecycle("   Cache location: ${cacheBaseDir.canonicalFile.absolutePath}")
            logger.lifecycle("   Shared caches are preserved to avoid conflicts across projects")
        }

        return isInsideBuildDir
    }

    /**
     * Clean old generated files to ensure fresh generation
     */
    private fun cleanOldGeneratedFiles(outputDir: File, packageName: String) {
        val packagePath = packageName.replace('.', '/')
        val iconsBaseDir = File(outputDir, "$packagePath/icons")
        val mainSymbolsFile = File(outputDir, "$packagePath/__Icons.kt")

        var cleanedCount = 0

        // Clean all library subdirectories and icon files
        if (iconsBaseDir.exists()) {
            iconsBaseDir.walkTopDown().forEach { file ->
                if (file.isFile && file.extension == "kt") {
                    logger.debug("🧹 Cleaning old generated file: ${file.relativeTo(iconsBaseDir).path}")
                    file.delete()
                    cleanedCount++
                }
            }
        }

        // Clean main symbols file to force regeneration
        if (mainSymbolsFile.exists()) {
            logger.debug("🧹 Cleaning main symbols file")
            mainSymbolsFile.delete()
            cleanedCount++
        }

        if (cleanedCount > 0) {
            logger.lifecycle("🧹 Cleaned $cleanedCount old generated files")
        }
    }

    /**
     * Clean unused SVG cache files that are no longer in the configuration
     */
    private fun cleanUnusedCache(cacheDir: File, config: Map<String, List<IconConfig>>) {
        if (!cacheDir.exists()) return

        // Build set of required cache keys
        val requiredCacheKeys = config.flatMap { (iconName, configs) ->
            configs.map { iconConfig -> iconConfig.getCacheKey(iconName) }
        }.toSet()

        var cleanedCount = 0

        // Clean unused SVG and meta files
        cacheDir.listFiles()?.forEach { file ->
            if (file.isFile) {
                val cacheKey = file.nameWithoutExtension
                if (cacheKey !in requiredCacheKeys) {
                    logger.debug("🧹 Cleaning unused cache file: ${file.name}")
                    if (file.delete()) {
                        cleanedCount++
                    } else {
                        logger.warn("   ⚠️ Failed to delete unused cache file: ${file.absolutePath}")
                    }
                }
            }
        }

        if (cleanedCount > 0) {
            logger.lifecycle("🧹 Cleaned $cleanedCount unused cache files")
        }
    }

    /**
     * Group icons by their library ID for organized output
     */
    private fun groupIconsByLibrary(config: Map<String, List<IconConfig>>): Map<String, Set<String>> {
        val libraryMap = mutableMapOf<String, MutableSet<String>>()

        config.forEach { (iconName, iconConfigs) ->
            iconConfigs.forEach { iconConfig ->
                libraryMap.getOrPut(iconConfig.libraryId) { mutableSetOf() }.add(iconName)
            }
        }

        return libraryMap
    }

    private suspend fun downloadSvgsParallel(
        downloader: SvgDownloader,
        config: Map<String, List<IconConfig>>,
        tempDir: File
    ): DownloadStats = coroutineScope {
        val totalIcons = config.values.sumOf { it.size }
        val completed = AtomicInteger(0)
        val failed = AtomicInteger(0)
        val cached = AtomicInteger(0)

        logger.lifecycle("⬇️ Downloading SVG files...")

        // Create download jobs for parallel execution
        val downloadJobs = config.flatMap { (iconName, iconConfigs) ->
            iconConfigs.map { iconConfig ->
                async(Dispatchers.IO) {
                    try {
                        val cacheKey = iconConfig.getCacheKey(iconName)
                        val wasCached = downloader.isCached(cacheKey)

                        val svgContent = downloader.downloadSvg(iconName, iconConfig)

                        if (svgContent != null && svgContent.isNotBlank()) {
                            // Include library ID in the directory structure
                            val librarySubdir = File(tempDir, iconConfig.libraryId)
                            librarySubdir.mkdirs()

                            val fileName = "${iconName.replaceFirstChar { it.titlecase() }}${iconConfig.getSignature()}.svg"
                            val tempFile = File(librarySubdir, fileName)
                            tempFile.writeText(svgContent)

                            completed.incrementAndGet()
                            if (wasCached) cached.incrementAndGet()

                            // Progress logging
                            val progress = completed.get() + failed.get()
                            if (progress % 5 == 0 || progress == totalIcons) {
                                logger.lifecycle("   Progress: $progress/$totalIcons")
                            }

                            DownloadResult.Success(iconName, iconConfig, fileName)
                        } else {
                            failed.incrementAndGet()
                            val errorMsg = if (svgContent == null) "Download returned null" else "Empty SVG content"
                            logger.warn("   ⚠️ Failed to download: $iconName-${iconConfig.getSignature()} ($errorMsg)")
                            DownloadResult.Failed(iconName, iconConfig, errorMsg)
                        }
                    } catch (e: Exception) {
                        failed.incrementAndGet()
                        val detailedError = when {
                            e.message?.contains("timeout", ignoreCase = true) == true -> "Timeout - network too slow"
                            e.message?.contains("404", ignoreCase = true) == true -> "Icon not found"
                            e.message?.contains("connection", ignoreCase = true) == true -> "Network connection failed"
                            else -> e.message ?: "Unknown error"
                        }
                        logger.warn("   ❌ Error downloading $iconName-${iconConfig.getSignature()}: $detailedError")
                        DownloadResult.Failed(iconName, iconConfig, detailedError)
                    }
                }
            }
        }

        // Wait for all downloads to complete
        val results = downloadJobs.awaitAll()

        DownloadStats(
            totalCount = totalIcons,
            successCount = completed.get(),
            failedCount = failed.get(),
            cachedCount = cached.get(),
            results = results.filterIsInstance<DownloadResult>()
        )
    }

    private fun logDownloadStats(stats: DownloadStats) {
        logger.lifecycle("✅ Download completed:")
        logger.lifecycle("   📁 Total: ${stats.totalCount}")
        logger.lifecycle("   ✅ Success: ${stats.successCount}")
        logger.lifecycle("   ❌ Failed: ${stats.failedCount}")
        logger.lifecycle("   💾 From cache: ${stats.cachedCount}")

        if (stats.failedCount > 0) {
            logger.warn("⚠️ Some icons failed to download. Generated code may use fallback implementations.")
        }
    }

    private fun convertSvgsToComposeByLibrary(
        tempDir: File,
        outputDir: File,
        packageName: String,
        iconsByLibrary: Map<String, Set<String>>,
        ext: SymbolCraftExtension
    ) {
        logger.lifecycle("🔄 Converting SVGs to Compose ImageVectors...")

        val converter = Svg2ComposeConverter()
        var totalConverted = 0

        iconsByLibrary.forEach { (libraryId, iconNames) ->
            val libraryTempDir = File(tempDir, libraryId)
            if (!libraryTempDir.exists() || libraryTempDir.listFiles()?.isEmpty() != false) {
                logger.warn("⚠️ No SVG files found for library: $libraryId")
                return@forEach
            }

            // Determine subdirectory name for this library
            val librarySubdir = when (libraryId) {
                "material-symbols" -> "materialsymbols" // Keep backward compatibility
                else -> libraryId.removePrefix("external-") // e.g., "bootstrap-icons", "heroicons"
            }

            // Create name transformer from extension configuration
            val nameTransformer = if (ext.namingConfig.transformer.isPresent) {
                // Use custom transformer
                LambdaNameTransformer(ext.namingConfig.transformer.get())
            } else {
                // Use convention-based transformer
                NameTransformerFactory.fromConvention(
                    convention = ext.namingConfig.namingConvention.get(),
                    suffix = ext.namingConfig.suffix.get(),
                    prefix = ext.namingConfig.prefix.get(),
                    removePrefix = ext.namingConfig.removePrefix.get(),
                    removeSuffix = ext.namingConfig.removeSuffix.get()
                )
            }

            logger.lifecycle("   📚 Converting library: $libraryId → icons/$librarySubdir/")
            if (ext.namingConfig.transformer.isPresent) {
                logger.debug("   🔄 Using custom transformer")
            } else {
                logger.debug("   🔄 Using transformer: ${ext.namingConfig.namingConvention.get()} (suffix='${ext.namingConfig.suffix.get()}', prefix='${ext.namingConfig.prefix.get()}')")
            }

            try {
                converter.convertDirectory(
                    inputDirectory = libraryTempDir,
                    outputDirectory = outputDir,
                    packageName = packageName,
                    generatePreview = ext.generatePreview.get(),
                    accessorName = "Icons",
                    allAssetsPropertyName = "AllIcons",
                    librarySubdir = librarySubdir,
                    nameTransformer = nameTransformer
                )
                val iconCount = libraryTempDir.listFiles()?.size ?: 0
                totalConverted += iconCount
                logger.lifecycle("      ✅ Converted $iconCount icons")
            } catch (e: Exception) {
                logger.error("❌ SVG conversion failed for library $libraryId: ${e.message}")
                logger.error("   Stack trace: ${e.stackTraceToString()}")

                // Provide specific guidance based on error type
                when {
                    e.message?.contains("directory", ignoreCase = true) == true -> {
                        logger.error("   💡 Directory issue: Check input/output directories exist and are writable")
                    }
                    e.message?.contains("package", ignoreCase = true) == true -> {
                        logger.error("   💡 Package issue: Check packageName is valid Kotlin package identifier")
                    }
                    e.message?.contains("SVG", ignoreCase = true) == true -> {
                        logger.error("   💡 SVG parsing issue: Some downloaded SVG files may be malformed")
                    }
                    else -> {
                        logger.error("   💡 Unexpected conversion error: ${e.javaClass.simpleName}")
                    }
                }
            }
        }

        logger.lifecycle("✅ Successfully converted $totalConverted icons total")
    }
}

/** Data aggregated from the parallel download stage for logging and diagnostics. */
data class DownloadStats(
    val totalCount: Int,
    val successCount: Int,
    val failedCount: Int,
    val cachedCount: Int,
    val results: List<DownloadResult>
)

/** Result wrapper representing either a successful or failed SVG download. */
sealed class DownloadResult {
    data class Success(
        val iconName: String,
        val config: IconConfig,
        val fileName: String
    ) : DownloadResult()

    data class Failed(
        val iconName: String,
        val config: IconConfig,
        val error: String
    ) : DownloadResult()
}
