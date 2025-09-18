package io.github.kingsword09.symbolcraft.converter

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * 使用 rafaeltonholo/svg-to-compose 库进行 SVG 到 Compose 的转换
 * 这是一个成熟且经过验证的解决方案，支持复杂的 SVG 路径和变换
 */
class RafaelSvgToComposeConverter {
    
    companion object {
        // s2c CLI 工具的版本和下载地址
        private const val S2C_VERSION = "2.1.2"
        
        // 根据操作系统选择正确的二进制文件
        private fun getS2cBinaryUrl(): String {
            val os = System.getProperty("os.name").lowercase()
            val arch = System.getProperty("os.arch").lowercase()
            
            // 基于项目实际的发布文件名格式
            return when {
                os.contains("mac") && (arch.contains("aarch64") || arch.contains("arm64")) -> 
                    "https://github.com/rafaeltonholo/svg-to-compose/releases/download/$S2C_VERSION/s2c-aarch64-apple-darwin.zip"
                os.contains("mac") -> 
                    "https://github.com/rafaeltonholo/svg-to-compose/releases/download/$S2C_VERSION/s2c-x86_64-apple-darwin.zip"
                os.contains("linux") -> 
                    "https://github.com/rafaeltonholo/svg-to-compose/releases/download/$S2C_VERSION/s2c-x86_64-unknown-linux-gnu.zip"
                os.contains("win") -> 
                    "https://github.com/rafaeltonholo/svg-to-compose/releases/download/$S2C_VERSION/s2c-x86_64-pc-windows-mingw.zip"
                else -> throw UnsupportedOperationException("不支持的操作系统: $os")
            }
        }
        
        private fun getS2cBinaryName(): String {
            val os = System.getProperty("os.name").lowercase()
            return if (os.contains("win")) "s2c.exe" else "s2c"
        }
        
        // 工具缓存目录
        private val TOOL_CACHE_DIR = File(System.getProperty("user.home"), ".gradle/caches/symbolcraft/tools")
        private val S2C_BINARY = File(TOOL_CACHE_DIR, getS2cBinaryName())
    }
    
    data class ConversionResult(
        val fileName: String,
        val success: Boolean,
        val message: String = ""
    )
    
    /**
     * 确保 s2c 工具已下载并可用
     */
    private suspend fun ensureS2cAvailable(): Boolean = withContext(Dispatchers.IO) {
        if (S2C_BINARY.exists() && S2C_BINARY.canExecute()) {
            println("s2c 工具已存在: ${S2C_BINARY.absolutePath}")
            return@withContext true
        }
        
        try {
            println("下载 s2c 工具...")
            TOOL_CACHE_DIR.mkdirs()
            
            val zipFile = File(TOOL_CACHE_DIR, "s2c.zip")
            val url = URL(getS2cBinaryUrl())
            
            // 下载 ZIP 文件
            url.openStream().use { input ->
                Files.copy(input, zipFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            
            // 解压 ZIP 文件
            println("解压 s2c 工具...")
            val unzipCommand = if (System.getProperty("os.name").lowercase().contains("win")) {
                listOf("powershell", "-command", "Expand-Archive", "-Path", zipFile.absolutePath, "-DestinationPath", TOOL_CACHE_DIR.absolutePath, "-Force")
            } else {
                listOf("unzip", "-o", zipFile.absolutePath, "-d", TOOL_CACHE_DIR.absolutePath)
            }
            
            val process = ProcessBuilder(unzipCommand).start()
            process.waitFor()
            
            // 查找解压后的可执行文件
            val extractedFile = TOOL_CACHE_DIR.listFiles()?.find { file ->
                file.name.startsWith("s2c") && (file.name.endsWith(".exe") || !file.name.contains("."))
            }
            
            if (extractedFile != null && extractedFile != S2C_BINARY) {
                // 重命名为标准名称
                extractedFile.renameTo(S2C_BINARY)
            }
            
            // 设置可执行权限（Unix-like 系统）
            if (!System.getProperty("os.name").lowercase().contains("win")) {
                S2C_BINARY.setExecutable(true)
            }
            
            // 清理 ZIP 文件
            zipFile.delete()
            
            if (S2C_BINARY.exists()) {
                println("s2c 工具下载成功: ${S2C_BINARY.absolutePath}")
                return@withContext true
            } else {
                println("s2c 工具解压失败，未找到可执行文件")
                return@withContext false
            }
        } catch (e: Exception) {
            println("下载 s2c 工具失败: ${e.message}")
            e.printStackTrace()
            return@withContext false
        }
    }
    
    /**
     * 使用 s2c 转换整个目录
     */
    suspend fun convertDirectory(
        inputDir: File,
        outputDir: File,
        packageName: String,
        theme: String? = null,
        optimize: Boolean = false // 暂时禁用优化以避免依赖问题
    ): List<ConversionResult> = coroutineScope {
        // 确保工具可用
        if (!ensureS2cAvailable()) {
            println("警告: 无法使用 s2c，生成简单的占位符代码")
            return@coroutineScope generateFallbackCode(inputDir, outputDir, packageName)
        }
        
        val results = mutableListOf<ConversionResult>()
        
        // 为每个 SVG 文件创建单独的转换任务
        val svgFiles = inputDir.listFiles { file -> file.extension == "svg" } ?: emptyArray()
        
        if (svgFiles.isEmpty()) {
            println("未找到 SVG 文件在目录: ${inputDir.absolutePath}")
            return@coroutineScope emptyList()
        }
        
        println("找到 ${svgFiles.size} 个 SVG 文件，开始转换...")
        
        // 并行处理每个 SVG 文件
        val jobs = svgFiles.map { svgFile ->
            async(Dispatchers.IO) {
                convertSingleFile(svgFile, outputDir, packageName, theme, optimize)
            }
        }
        
        // 等待所有转换完成
        results.addAll(jobs.awaitAll())
        
        return@coroutineScope results
    }
    
    /**
     * 转换单个 SVG 文件
     */
    private suspend fun convertSingleFile(
        svgFile: File,
        outputDir: File,
        packageName: String,
        theme: String?,
        optimize: Boolean
    ): ConversionResult = withContext(Dispatchers.IO) {
        try {
            // 生成输出文件名
            val iconName = svgFile.nameWithoutExtension
                .split("_", "-")
                .joinToString("") { it.replaceFirstChar { char -> char.titlecase() } }
            
            val outputFileName = "$iconName.kt"
            val packagePath = packageName.replace('.', '/')
            val outputFile = File(outputDir, "$packagePath/$outputFileName")
            
            // 确保输出目录存在
            outputFile.parentFile.mkdirs()
            
            // 构建命令
            val command = mutableListOf(
                S2C_BINARY.absolutePath,
                "-o", outputFile.absolutePath,
                "-p", packageName
            )
            
            // 添加主题参数（如果提供）
            if (!theme.isNullOrBlank()) {
                command.addAll(listOf("-t", theme))
            }
            
            // 添加优化参数
            command.addAll(listOf("--optimize", optimize.toString()))
            
            // 添加输入文件
            command.add(svgFile.absolutePath)
            
            println("执行: ${command.joinToString(" ")}")
            
            // 执行转换
            val processBuilder = ProcessBuilder(command)
                .redirectErrorStream(true)
            
            val process = processBuilder.start()
            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            
            if (exitCode == 0) {
                println("✅ 成功转换: ${svgFile.name} -> $outputFileName")
                ConversionResult(
                    fileName = outputFileName,
                    success = true,
                    message = "成功转换"
                )
            } else {
                println("❌ 转换失败: ${svgFile.name}")
                println("错误输出: $output")
                ConversionResult(
                    fileName = outputFileName,
                    success = false,
                    message = "转换失败: $output"
                )
            }
        } catch (e: Exception) {
            println("❌ 转换异常: ${svgFile.name} - ${e.message}")
            ConversionResult(
                fileName = svgFile.name,
                success = false,
                message = "转换异常: ${e.message}"
            )
        }
    }
    
    /**
     * 生成备用代码（当 s2c 不可用时）
     */
    private fun generateFallbackCode(
        inputDir: File,
        outputDir: File,
        packageName: String
    ): List<ConversionResult> {
        val results = mutableListOf<ConversionResult>()
        
        inputDir.listFiles { file -> file.extension == "svg" }?.forEach { svgFile ->
            val iconName = svgFile.nameWithoutExtension
                .split("_", "-")
                .joinToString("") { it.replaceFirstChar { char -> char.titlecase() } }
            
            // 生成简单但功能完整的 Compose 代码
            val composeCode = generateSimpleComposeCode(packageName, iconName)
            
            val packagePath = packageName.replace('.', '/')
            val outputFile = File(outputDir, "$packagePath/$iconName.kt")
            outputFile.parentFile.mkdirs()
            outputFile.writeText(composeCode)
            
            results.add(ConversionResult(
                fileName = "$iconName.kt",
                success = true,
                message = "使用备用方案生成"
            ))
        }
        
        return results
    }
    
    /**
     * 生成简单的 Compose 代码作为备用方案
     */
    private fun generateSimpleComposeCode(packageName: String, iconName: String): String {
        return """
package $packageName

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
 * 
 * 注意：这是一个临时占位符图标。
 * 要获得正确的图标，请确保 s2c 工具正确安装。
 * 
 * 安装方法：
 * 1. 访问 https://github.com/rafaeltonholo/svg-to-compose
 * 2. 下载对应平台的二进制文件
 * 3. 或通过项目自动下载功能获取
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
            // 警告图标路径（作为占位符）
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
                moveTo(1f, 21f)
                horizontalLineToRelative(22f)
                lineTo(12f, 2f)
                lineTo(1f, 21f)
                close()
                moveTo(13f, 18f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                close()
                moveTo(13f, 14f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(4f)
                close()
            }
        }.build()
        return _$iconName!!
    }

private var _$iconName: ImageVector? = null
        """.trimIndent()
    }
}
