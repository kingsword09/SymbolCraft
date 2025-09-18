package io.github.kingsword09.symbolcraft.converter

import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * 使用 svg2compose 开源工具进行SVG到Compose的转换
 * 这个类会自动下载并使用 svg2compose CLI 工具
 */
class Svg2ComposeConverter {
    
    companion object {
        // svg2compose 的最新版本下载地址
        private const val SVG2COMPOSE_VERSION = "0.9.8"
        private const val SVG2COMPOSE_URL = 
            "https://github.com/DevSrSouza/svg-to-compose/releases/download/$SVG2COMPOSE_VERSION/s2c.jar"
        
        // 工具缓存目录
        private val TOOL_CACHE_DIR = File(System.getProperty("user.home"), ".gradle/caches/symbolcraft/tools")
        private val SVG2COMPOSE_JAR = File(TOOL_CACHE_DIR, "svg2compose-$SVG2COMPOSE_VERSION.jar")
    }
    
    data class ConversionResult(
        val fileName: String,
        val success: Boolean,
        val message: String = ""
    )
    
    /**
     * 确保 svg2compose 工具已下载
     */
    private fun ensureSvg2ComposeAvailable(): Boolean {
        if (SVG2COMPOSE_JAR.exists()) {
            return true
        }
        
        try {
            println("下载 svg2compose 工具...")
            TOOL_CACHE_DIR.mkdirs()
            
            val url = URL(SVG2COMPOSE_URL)
            url.openStream().use { input ->
                Files.copy(input, SVG2COMPOSE_JAR.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            
            println("svg2compose 工具下载成功")
            return true
        } catch (e: Exception) {
            println("下载 svg2compose 失败: ${e.message}")
            return false
        }
    }
    
    /**
     * 使用 svg2compose 转换整个目录
     */
    fun convertDirectory(
        inputDir: File,
        outputDir: File,
        packageName: String
    ): List<ConversionResult> {
        // 确保工具可用
        if (!ensureSvg2ComposeAvailable()) {
            // 如果无法下载工具，回退到使用内置的简单转换器
            println("警告: 无法使用 svg2compose，回退到内置转换器")
            return useBuiltinConverter(inputDir, outputDir, packageName)
        }
        
        val results = mutableListOf<ConversionResult>()
        
        try {
            // 构建命令
            val command = listOf(
                "java",
                "-jar",
                SVG2COMPOSE_JAR.absolutePath,
                "-i", inputDir.absolutePath,
                "-o", outputDir.absolutePath,
                "-pkg", packageName,
                "--theme", // 支持主题
                "--recursive" // 递归处理子目录
            )
            
            println("执行: ${command.joinToString(" ")}")
            
            // 执行转换
            val processBuilder = ProcessBuilder(command)
                .redirectErrorStream(true)
            
            val process = processBuilder.start()
            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            
            if (exitCode == 0) {
                println("svg2compose 转换成功")
                
                // 列出生成的文件
                val packagePath = packageName.replace('.', '/')
                val generatedDir = File(outputDir, packagePath)
                
                if (generatedDir.exists()) {
                    generatedDir.listFiles { file -> file.extension == "kt" }?.forEach { file ->
                        results.add(ConversionResult(
                            fileName = file.name,
                            success = true,
                            message = "成功生成"
                        ))
                    }
                }
            } else {
                println("svg2compose 转换失败: $output")
                results.add(ConversionResult(
                    fileName = "",
                    success = false,
                    message = "转换失败: $output"
                ))
            }
        } catch (e: Exception) {
            println("执行 svg2compose 时出错: ${e.message}")
            results.add(ConversionResult(
                fileName = "",
                success = false,
                message = "执行错误: ${e.message}"
            ))
        }
        
        // 如果 svg2compose 失败，尝试使用备用方案
        if (results.isEmpty() || results.all { !it.success }) {
            println("svg2compose 未能生成文件，尝试备用方案")
            return useAlternativeConverter(inputDir, outputDir, packageName)
        }
        
        return results
    }
    
    /**
     * 使用备用的开源转换方案
     * 这里我们可以集成另一个工具，如 compose-icons
     */
    private fun useAlternativeConverter(
        inputDir: File,
        outputDir: File,
        packageName: String
    ): List<ConversionResult> {
        // 尝试使用 Accompanist 的 SVG 转换逻辑
        // 或者使用 Android Studio 的 Vector Asset Studio 的开源实现
        
        println("尝试使用备用转换器...")
        return useBuiltinConverter(inputDir, outputDir, packageName)
    }
    
    /**
     * 使用简化的内置转换器作为最后的备用方案
     */
    private fun useBuiltinConverter(
        inputDir: File,
        outputDir: File,
        packageName: String
    ): List<ConversionResult> {
        val results = mutableListOf<ConversionResult>()
        
        inputDir.listFiles { file -> file.extension == "svg" }?.forEach { svgFile ->
            val iconName = svgFile.nameWithoutExtension
                .split("_", "-")
                .joinToString("") { it.replaceFirstChar { char -> char.titlecase() } }
            
            // 生成一个简单的占位符 Compose 代码
            val composeCode = generatePlaceholderCode(packageName, iconName)
            
            val packagePath = packageName.replace('.', '/')
            val outputFile = File(outputDir, "$packagePath/$iconName.kt")
            outputFile.parentFile.mkdirs()
            outputFile.writeText(composeCode)
            
            results.add(ConversionResult(
                fileName = "$iconName.kt",
                success = true,
                message = "使用占位符生成"
            ))
        }
        
        return results
    }
    
    /**
     * 生成占位符代码，提示用户需要使用正确的转换器
     */
    private fun generatePlaceholderCode(packageName: String, iconName: String): String {
        return """
package $packageName

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

// TODO: 这是一个占位符图标
// 请安装并配置 svg2compose 工具来生成正确的图标代码
// 或者使用 Android Studio 的 Vector Asset Studio 导入 SVG
val $iconName: ImageVector = Icons.Default.Warning
        """.trimIndent()
    }
}
