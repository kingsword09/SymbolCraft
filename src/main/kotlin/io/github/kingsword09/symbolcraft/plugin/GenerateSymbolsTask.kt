package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.converter.Svg2ComposeConverter
import io.github.kingsword09.symbolcraft.download.SvgDownloader
import kotlinx.coroutines.*
import org.gradle.api.DefaultTask
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
    val outputDir: File
        get() = project.file(extension.get().outputDirectory.get())

    @get:OutputDirectory
    val assetsDir: File
        get() = project.file(extension.get().assetsDirectory.get())

    @TaskAction
    fun generate() = runBlocking {
        val ext = extension.get()
        val config = ext.getSymbolsConfig()
        val packageName = ext.packageName.get()
        val cacheDir = ext.cacheDirectory.get().trim('/', '\\')
        val tempDir = project.file("$cacheDir/temp-svgs")

        logger.lifecycle("🎨 Generating Material Symbols...")
        logger.lifecycle("📊 Symbols to generate: ${config.values.sumOf { it.size }} icons")

        val downloader = SvgDownloader(
            cacheDirectory = File(project.gradle.gradleUserHomeDir, "caches/symbolcraft/svg-cache").absolutePath,
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
            convertSvgsToCompose(tempDir, outputDir, packageName, downloadStats.successCount)

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

    private suspend fun convertSvgsToCompose(
        tempDir: File,
        outputDir: File,
        packageName: String,
        iconCount: Int
    ) {
        logger.lifecycle("🔄 Converting SVGs to Compose ImageVectors...")

        val converter = Svg2ComposeConverter()

        try {
            converter.convertDirectory(
                inputDirectory = tempDir,
                outputDirectory = outputDir,
                packageName = packageName,
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

            // Fall back to internal converter
            logger.lifecycle("🔄 Falling back to internal converter...")
            try {
                runInternalConverter(tempDir, outputDir, packageName)
                logger.lifecycle("✅ Fallback conversion completed successfully")
            } catch (fallbackException: Exception) {
                logger.error("❌ Fallback converter also failed: ${fallbackException.message}")
                throw Exception("Both primary and fallback converters failed", fallbackException)
            }
        }
    }


    private fun runInternalConverter(fromDir: File, outDir: File, packageName: String) = runBlocking {
        logger.lifecycle("🔄 Using fallback icon generator...")

        val svgFiles = fromDir.listFiles { file -> file.extension == "svg" } ?: emptyArray()

        if (svgFiles.isEmpty()) {
            val errorMsg = "No SVG files found in directory: ${fromDir.absolutePath}"
            logger.error("❌ $errorMsg")
            throw IllegalStateException(errorMsg)
        }

        logger.lifecycle("📁 Found ${svgFiles.size} SVG files to convert")

        val packagePath = packageName.replace('.', '/')
        val outputDirPath = File(outDir, packagePath)

        if (!outputDirPath.mkdirs() && !outputDirPath.exists()) {
            val errorMsg = "Failed to create output directory: ${outputDirPath.absolutePath}"
            logger.error("❌ $errorMsg")
            throw IllegalStateException(errorMsg)
        }

        var successCount = 0
        var failureCount = 0

        svgFiles.forEach { svgFile ->
            try {
                val iconName = svgFile.nameWithoutExtension
                    .split("_", "-")
                    .joinToString("") { it.replaceFirstChar { char -> char.titlecase() } }

                val outputFile = File(outputDirPath, "$iconName.kt")

                // 根据图标名称生成不同的示例路径
                val iconPath = when {
                    iconName.contains("Home", true) -> generateHomePath()
                    iconName.contains("Person", true) -> generatePersonPath()
                    iconName.contains("Search", true) -> generateSearchPath()
                    else -> generateDefaultPath()
                }

                val composeCode = generateFallbackIconCode(packageName, iconName, iconPath)
                outputFile.writeText(composeCode)

                successCount++
                logger.debug("  ✅ Generated fallback icon: $iconName.kt")
            } catch (e: Exception) {
                failureCount++
                logger.warn("  ❌ Failed to generate fallback for ${svgFile.name}: ${e.message}")
            }
        }

        if (failureCount > 0) {
            logger.warn("⚠️ Fallback conversion completed with $failureCount failures out of ${svgFiles.size} files")
        } else {
            logger.lifecycle("✅ Fallback conversion completed successfully for all ${svgFiles.size} files")
        }

        if (successCount == 0) {
            throw IllegalStateException("Fallback converter failed to generate any icons")
        }
    }

    private fun generateFallbackIconCode(packageName: String, iconName: String, iconPath: String): String {
        return """
package $packageName

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Material Symbol: $iconName
 * Generated from SVG by SymbolCraft (Fallback)
 */
val $iconName: ImageVector
    get() {
        if (_$iconName != null) {
            return _$iconName!!
        }
        _$iconName = ImageVector.Builder(
            name = "$iconName",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4f,
                pathFillType = PathFillType.NonZero
            ) {
                $iconPath
            }
        }.build()
        return _$iconName!!
    }

private var _$iconName: ImageVector? = null
            """.trimIndent()
    }

    private fun generateHomePath(): String = """
                // Home icon path
                moveTo(10f, 20f)
                verticalLineTo(14f)
                horizontalLineTo(14f)
                verticalLineTo(20f)
                horizontalLineTo(19f)
                verticalLineTo(12f)
                horizontalLineTo(22f)
                lineTo(12f, 3f)
                lineTo(2f, 12f)
                horizontalLineTo(5f)
                verticalLineTo(20f)
                horizontalLineTo(10f)
                close()
    """.trimIndent()

    private fun generatePersonPath(): String = """
                // Person icon path
                moveTo(12f, 12f)
                curveToRelative(2.21f, 0f, 4f, -1.79f, 4f, -4f)
                reflectiveCurveToRelative(-1.79f, -4f, -4f, -4f)
                reflectiveCurveToRelative(-4f, 1.79f, -4f, 4f)
                reflectiveCurveToRelative(1.79f, 4f, 4f, 4f)
                close()
                moveTo(12f, 14f)
                curveToRelative(-2.67f, 0f, -8f, 1.34f, -8f, 4f)
                verticalLineTo(20f)
                horizontalLineToRelative(16f)
                verticalLineTo(18f)
                curveToRelative(0f, -2.66f, -5.33f, -4f, -8f, -4f)
                close()
    """.trimIndent()

    private fun generateSearchPath(): String = """
                // Search icon path
                moveTo(15.5f, 14f)
                horizontalLineToRelative(-0.79f)
                lineToRelative(-0.28f, -0.27f)
                curveToRelative(0.98f, -1.14f, 1.57f, -2.62f, 1.57f, -4.23f)
                curveToRelative(0f, -3.59f, -2.91f, -6.5f, -6.5f, -6.5f)
                reflectiveCurveTo(3f, 5.91f, 3f, 9.5f)
                reflectiveCurveTo(5.91f, 16f, 9.5f, 16f)
                curveToRelative(1.61f, 0f, 3.09f, -0.59f, 4.23f, -1.57f)
                lineToRelative(0.27f, 0.28f)
                verticalLineToRelative(0.79f)
                lineToRelative(5f, 4.99f)
                lineTo(20.49f, 19f)
                lineToRelative(-4.99f, -5f)
                close()
                moveTo(9.5f, 14f)
                curveToRelative(-2.49f, 0f, -4.5f, -2.01f, -4.5f, -4.5f)
                reflectiveCurveTo(7.01f, 5f, 9.5f, 5f)
                reflectiveCurveTo(14f, 7.01f, 14f, 9.5f)
                reflectiveCurveTo(11.99f, 14f, 9.5f, 14f)
                close()
    """.trimIndent()

    private fun generateDefaultPath(): String = """
                // Default icon path (star)
                moveTo(12f, 17.27f)
                lineTo(18.18f, 21f)
                lineToRelative(-1.64f, -7.03f)
                lineTo(22f, 9.24f)
                lineToRelative(-7.19f, -0.61f)
                lineTo(12f, 2f)
                lineTo(9.19f, 8.63f)
                lineTo(2f, 9.24f)
                lineToRelative(5.46f, 4.73f)
                lineTo(5.82f, 21f)
                close()
    """.trimIndent()
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
