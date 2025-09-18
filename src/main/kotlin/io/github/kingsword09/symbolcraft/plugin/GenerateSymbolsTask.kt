package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.download.SvgDownloader
import io.github.kingsword09.symbolcraft.converter.RafaelSvgToComposeConverter
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
        val converter = RafaelSvgToComposeConverter()
        
        val results = converter.convertDirectory(
            inputDir = fromDir,
            outputDir = outDir,
            packageName = packageName,
            theme = null, // 主题可以后续添加
            optimize = false // 暂时禁用优化以避免额外依赖
        )
        
        if (results.isNotEmpty()) {
            logger.lifecycle("转换结果:")
            results.forEach { result ->
                if (result.success) {
                    logger.lifecycle("  ✅ ${result.fileName} - ${result.message}")
                } else {
                    logger.warn("  ❌ ${result.message}")
                }
            }
            
            val successCount = results.count { it.success }
            val failedCount = results.count { !it.success }
            logger.lifecycle("转换完成: 成功 $successCount 个，失败 $failedCount 个")
        } else {
            logger.warn("未能转换任何 SVG 文件")
        }
    }
}
