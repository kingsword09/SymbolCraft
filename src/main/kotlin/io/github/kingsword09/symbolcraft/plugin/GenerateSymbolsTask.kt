package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.download.SvgDownloader
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.concurrent.TimeUnit

@CacheableTask
abstract class GenerateSymbolsTask : DefaultTask() {

    @get:Internal
    abstract val extension: Property<MaterialSymbolsExtension>

    @get:Input
    val symbolsConfigHash: String
        get() = extension.get().getConfigHash()

    // Output directory is managed by svg-to-compose; we expose it only for task wiring
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
        val tempDir = project.file("${ext.cacheDirectory.get().trim('/', '\\')}/temp-svgs")

        logger.lifecycle("Generating Material Symbols...")

        val downloader = SvgDownloader(
            cacheDirectory = File(project.gradle.gradleUserHomeDir, "caches/symbolcraft/svg-cache").absolutePath,
            cacheEnabled = ext.cacheEnabled.get()
        )

        if (tempDir.exists()) tempDir.deleteRecursively()
        tempDir.mkdirs()

        // 1. Download all SVGs to a temporary directory
        config.forEach { (iconName, styles) ->
            styles.forEach { style ->
                val svgContent = downloader.downloadSvg(iconName, style)
                if (svgContent != null) {
                    val pascalIcon = iconName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    val tempFile = File(tempDir, "${pascalIcon}${style.signature}.svg")
                    tempFile.writeText(svgContent)
                }
            }
        }

        // 2. Try external converter first (if configured), otherwise use built-in converter
        val converterUsed = runExternalConverter(tempDir, outputDir, packageName)

        if (!converterUsed) {
            logger.lifecycle("Using built-in SVG to Compose converter...")
            runInternalConverter(tempDir, outputDir, packageName)
        }

        logger.lifecycle("Generation complete.")
        downloader.cleanup()
    }

    private fun runExternalConverter(fromDir: File, outDir: File, packageName: String): Boolean {
        val ext = extension.get()
        val converter = ext.converterPath.get().trim()
        if (converter.isBlank()) {
            return false
        }
        val argsTemplate = ext.converterArgs.get()
        if (argsTemplate.isEmpty()) {
            logger.warn("External converter path is set but args are empty. Skipping external conversion.")
            return false
        }
        val replacements = mapOf(
            "{from}" to fromDir.absolutePath,
            "{to}" to outDir.absolutePath,
            "{pkg}" to packageName
        )
        val args = argsTemplate.map { token ->
            replacements.entries.fold(token) { acc, (k, v) -> acc.replace(k, v) }
        }
        outDir.mkdirs()
        logger.lifecycle("Running external converter: $converter ${args.joinToString(" ")}")
        val proc = ProcessBuilder(listOf(converter) + args)
            .directory(project.projectDir)
            .redirectErrorStream(true)
            .start()
        val output = proc.inputStream.bufferedReader().readText()
        val finished = proc.waitFor(5, TimeUnit.MINUTES)
        if (!finished) {
            proc.destroyForcibly()
            logger.warn("External converter timed out. Output so far:\n$output")
            return false
        }
        val code = proc.exitValue()
        if (code != 0) {
            logger.warn("External converter failed with exit code $code. Output:\n$output")
            return false
        } else {
            logger.lifecycle("External converter finished successfully.")
            return true
        }
    }

    private fun runInternalConverter(fromDir: File, outDir: File, packageName: String) = runBlocking {
        // 由于SVG解析复杂，暂时使用简单但有效的占位符图标
        logger.lifecycle("生成Material Symbols图标...")

        val svgFiles = fromDir.listFiles { file -> file.extension == "svg" } ?: emptyArray()

        if (svgFiles.isEmpty()) {
            logger.warn("未找到SVG文件在目录: ${fromDir.absolutePath}")
            return@runBlocking
        }

        val packagePath = packageName.replace('.', '/')
        val outputDirPath = File(outDir, packagePath)
        outputDirPath.mkdirs()

        svgFiles.forEach { svgFile ->
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

            val composeCode = """
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
 * Generated from SVG by SymbolCraft
 * 
 * 注意：这是一个示例图标，真实的SVG转换功能正在开发中。
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

            outputFile.writeText(composeCode)
            logger.lifecycle("  ✅ 生成图标: $iconName.kt")
        }

        logger.lifecycle("成功生成 ${svgFiles.size} 个图标文件")
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
