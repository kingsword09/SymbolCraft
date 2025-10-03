package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.converter.Svg2ComposeConverter
import io.github.kingsword09.symbolcraft.download.SvgDownloader
import kotlinx.coroutines.*
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

@CacheableTask
abstract class GenerateSymbolsTask : DefaultTask() {

    @get:Internal
    abstract val extension: Property<MaterialSymbolsExtension>

    @get:Input
    val symbolsConfigHash: String
        get() = extension.get().getConfigHash()

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val assetsDir: DirectoryProperty

    @get:Input
    abstract val cacheDirectory: Property<String>

    @get:Input
    abstract val gradleUserHomeDir: Property<String>

    @get:Input
    abstract val projectBuildDir: Property<String>

    @TaskAction
    fun generate() = runBlocking {
        val ext = extension.get()
        val config = ext.getSymbolsConfig()
        val packageName = ext.packageName.get()
        val cacheDirPath = cacheDirectory.get()
        val projectBuildDirPath = projectBuildDir.get()

        // Resolve cache directory: support both absolute and relative paths
        val cacheBaseDir = PathUtils.resolveCacheDirectory(cacheDirPath, projectBuildDirPath)
        val tempDir = File(cacheBaseDir, "temp-svgs")
        val svgCacheDir = File(cacheBaseDir, "svg-cache")
        val outputDirFile = outputDir.get().asFile

        logger.lifecycle("🎨 Generating Material Symbols...")
        logger.lifecycle("📊 Symbols to generate: ${config.values.sumOf { it.size }} icons")
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
            cacheEnabled = ext.cacheEnabled.get()
        )

        try {
            // Clean and prepare temp directory
            if (tempDir.exists()) tempDir.deleteRecursively()
            tempDir.mkdirs()

            // Download SVGs with progress tracking
            val downloadStats = downloadSvgsParallel(downloader, config, tempDir)
            logDownloadStats(downloadStats)

            // Convert SVGs to Compose code
            convertSvgsToCompose(tempDir, outputDirFile, packageName, downloadStats.successCount, ext.generatePreview.get())

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
        val symbolsDir = File(outputDir, "$packagePath/materialsymbols")
        val mainSymbolsFile = File(outputDir, "$packagePath/__MaterialSymbols.kt")

        var cleanedCount = 0

        // Clean individual icon files
        if (symbolsDir.exists()) {
            symbolsDir.listFiles()?.forEach { file ->
                if (file.isFile && file.extension == "kt") {
                    logger.debug("🧹 Cleaning old generated file: ${file.name}")
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
    private fun cleanUnusedCache(cacheDir: File, config: Map<String, List<io.github.kingsword09.symbolcraft.model.SymbolStyle>>) {
        if (!cacheDir.exists()) return

        // Build set of required cache keys
        val requiredCacheKeys = config.flatMap { (iconName, styles) ->
            styles.map { style -> style.getCacheKey(iconName) }
        }.toSet()

        var cleanedCount = 0

        // Clean unused SVG and meta files
        cacheDir.listFiles()?.forEach { file ->
            if (file.isFile) {
                val cacheKey = file.nameWithoutExtension
                if (cacheKey !in requiredCacheKeys) {
                    logger.debug("🧹 Cleaning unused cache file: ${file.name}")
                    file.delete()
                    cleanedCount++
                }
            }
        }

        if (cleanedCount > 0) {
            logger.lifecycle("🧹 Cleaned $cleanedCount unused cache files")
        }
    }

    private suspend fun downloadSvgsParallel(
        downloader: SvgDownloader,
        config: Map<String, List<io.github.kingsword09.symbolcraft.model.SymbolStyle>>,
        tempDir: File
    ): DownloadStats = coroutineScope {
        val totalIcons = config.values.sumOf { it.size }
        val completed = AtomicInteger(0)
        val failed = AtomicInteger(0)
        val cached = AtomicInteger(0)

        logger.lifecycle("⬇️ Downloading SVG files...")

        // Create download jobs for parallel execution
        val downloadJobs = config.flatMap { (iconName, styles) ->
            styles.map { style ->
                async(Dispatchers.IO) {
                    try {
                        val cacheKey = style.getCacheKey(iconName)
                        val wasCached = downloader.isCached(cacheKey)

                        val svgContent = downloader.downloadSvg(iconName, style)

                        if (svgContent != null && svgContent.isNotBlank()) {
                            val fileName = "${iconName.replaceFirstChar { it.titlecase() }}${style.signature}.svg"
                            val tempFile = File(tempDir, fileName)
                            tempFile.writeText(svgContent)

                            completed.incrementAndGet()
                            if (wasCached) cached.incrementAndGet()

                            // Progress logging
                            val progress = completed.get() + failed.get()
                            if (progress % 5 == 0 || progress == totalIcons) {
                                logger.lifecycle("   Progress: $progress/$totalIcons")
                            }

                            DownloadResult.Success(iconName, style, fileName)
                        } else {
                            failed.incrementAndGet()
                            val errorMsg = if (svgContent == null) "Download returned null" else "Empty SVG content"
                            logger.warn("   ⚠️ Failed to download: $iconName-${style.signature} ($errorMsg)")
                            DownloadResult.Failed(iconName, style, errorMsg)
                        }
                    } catch (e: Exception) {
                        failed.incrementAndGet()
                        val detailedError = when {
                            e.message?.contains("timeout", ignoreCase = true) == true -> "Timeout - network too slow"
                            e.message?.contains("404", ignoreCase = true) == true -> "Icon not found in Material Symbols"
                            e.message?.contains("connection", ignoreCase = true) == true -> "Network connection failed"
                            else -> e.message ?: "Unknown error"
                        }
                        logger.warn("   ❌ Error downloading $iconName-${style.signature}: $detailedError")
                        DownloadResult.Failed(iconName, style, detailedError)
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

    private fun convertSvgsToCompose(
        tempDir: File,
        outputDir: File,
        packageName: String,
        iconCount: Int,
        generatePreview: Boolean = true,
    ) {
        logger.lifecycle("🔄 Converting SVGs to Compose ImageVectors...")

        val converter = Svg2ComposeConverter()

        try {
            converter.convertDirectory(
                inputDirectory = tempDir,
                outputDirectory = outputDir,
                packageName = packageName,
                generatePreview = generatePreview,
                accessorName = "MaterialSymbols",
                allAssetsPropertyName = "AllIcons"
            )
            logger.lifecycle("✅ Successfully converted $iconCount icons")
        } catch (e: Exception) {
            logger.error("❌ SVG conversion failed with DevSrSouza library: ${e.message}")
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
}

// Data classes for download tracking
data class DownloadStats(
    val totalCount: Int,
    val successCount: Int,
    val failedCount: Int,
    val cachedCount: Int,
    val results: List<DownloadResult>
)

sealed class DownloadResult {
    data class Success(
        val iconName: String,
        val style: io.github.kingsword09.symbolcraft.model.SymbolStyle,
        val fileName: String
    ) : DownloadResult()

    data class Failed(
        val iconName: String,
        val style: io.github.kingsword09.symbolcraft.model.SymbolStyle,
        val error: String
    ) : DownloadResult()
}
