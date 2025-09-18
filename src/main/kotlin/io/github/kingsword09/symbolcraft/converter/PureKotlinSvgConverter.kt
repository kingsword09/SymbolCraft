package io.github.kingsword09.symbolcraft.converter

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * 纯 Kotlin SVG 到 Compose 转换器
 * 不依赖外部工具，直接解析 SVG 并生成 Compose ImageVector 代码
 */
class PureKotlinSvgConverter {
    
    data class ConversionResult(
        val fileName: String,
        val success: Boolean,
        val message: String = ""
    )
    
    /**
     * 转换整个目录中的 SVG 文件
     */
    suspend fun convertDirectory(
        inputDir: File,
        outputDir: File,
        packageName: String,
        theme: String? = null,
        optimize: Boolean = false
    ): List<ConversionResult> = coroutineScope {
        val results = mutableListOf<ConversionResult>()
        
        val svgFiles = inputDir.listFiles { file -> file.extension == "svg" } ?: emptyArray()
        
        if (svgFiles.isEmpty()) {
            println("未找到 SVG 文件在目录: ${inputDir.absolutePath}")
            return@coroutineScope emptyList()
        }
        
        println("找到 ${svgFiles.size} 个 SVG 文件，开始转换...")
        
        val jobs = svgFiles.map { svgFile ->
            async(Dispatchers.IO) {
                convertSingleFile(svgFile, outputDir, packageName)
            }
        }
        
        results.addAll(jobs.awaitAll())
        
        return@coroutineScope results
    }
    
    /**
     * 转换单个 SVG 文件
     */
    private suspend fun convertSingleFile(
        svgFile: File,
        outputDir: File,
        packageName: String
    ): ConversionResult = withContext(Dispatchers.IO) {
        try {
            val iconName = svgFile.nameWithoutExtension
                .split("_", "-")
                .joinToString("") { it.replaceFirstChar { char -> char.titlecase() } }
            
            val outputFileName = "$iconName.kt"
            val packagePath = packageName.replace('.', '/')
            val outputFile = File(outputDir, "$packagePath/$outputFileName")
            outputFile.parentFile.mkdirs()
            
            // 解析 SVG
            val svgData = parseSvg(svgFile)
            
            // 生成 Compose 代码
            val composeCode = generateComposeCode(packageName, iconName, svgData)
            
            outputFile.writeText(composeCode)
            
            println("✅ 成功转换: ${svgFile.name} -> $outputFileName")
            ConversionResult(
                fileName = outputFileName,
                success = true,
                message = "成功转换"
            )
        } catch (e: Exception) {
            println("❌ 转换失败: ${svgFile.name} - ${e.message}")
            e.printStackTrace()
            
            ConversionResult(
                fileName = svgFile.name,
                success = false,
                message = "转换失败: ${e.message}"
            )
        }
    }
    
    /**
     * 解析 SVG 文件
     */
    private fun parseSvg(svgFile: File): SvgData {
        val docFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = docFactory.newDocumentBuilder()
        val doc = docBuilder.parse(svgFile)
        
        val svg = doc.documentElement
        
        // 获取 viewBox
        val viewBox = svg.getAttribute("viewBox")
        val viewBoxValues = if (viewBox.isNotEmpty()) {
            viewBox.split(" ").map { it.toFloatOrNull() ?: 0f }
        } else {
            listOf(0f, 0f, 24f, 24f)
        }
        
        // 获取宽高
        val width = svg.getAttribute("width").toFloatOrNull() ?: 24f
        val height = svg.getAttribute("height").toFloatOrNull() ?: 24f
        
        // 获取所有 path 元素
        val pathNodes = svg.getElementsByTagName("path")
        val paths = mutableListOf<PathData>()
        
        for (i in 0 until pathNodes.length) {
            val pathElement = pathNodes.item(i) as Element
            val pathStr = pathElement.getAttribute("d")
            val fill = pathElement.getAttribute("fill").takeIf { it.isNotEmpty() }
            val stroke = pathElement.getAttribute("stroke").takeIf { it.isNotEmpty() }
            val strokeWidth = pathElement.getAttribute("stroke-width").toFloatOrNull()
            
            if (pathStr.isNotEmpty()) {
                paths.add(PathData(pathStr, fill, stroke, strokeWidth))
            }
        }
        
        return SvgData(
            width = width,
            height = height,
            viewBoxWidth = if (viewBoxValues.size >= 3) viewBoxValues[2] else width,
            viewBoxHeight = if (viewBoxValues.size >= 4) viewBoxValues[3] else height,
            paths = paths
        )
    }
    
    /**
     * 生成 Compose ImageVector 代码
     */
    private fun generateComposeCode(packageName: String, iconName: String, svgData: SvgData): String {
        val pathCode = svgData.paths.joinToString("\n            ") { pathData ->
            val pathCommands = convertSvgPathToCompose(pathData.path)
            """
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
                $pathCommands
            }""".trimIndent()
        }
        
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
 * Generated from SVG by SymbolCraft
 */
val $iconName: ImageVector
    get() {
        if (_$iconName != null) {
            return _$iconName!!
        }
        _$iconName = ImageVector.Builder(
            name = "$iconName",
            defaultWidth = ${svgData.width}f.dp,
            defaultHeight = ${svgData.height}f.dp,
            viewportWidth = ${svgData.viewBoxWidth}f,
            viewportHeight = ${svgData.viewBoxHeight}f
        ).apply {
            $pathCode
        }.build()
        return _$iconName!!
    }

private var _$iconName: ImageVector? = null
        """.trimIndent()
    }
    
    /**
     * 转换 SVG 路径命令为 Compose 路径命令
     */
    private fun convertSvgPathToCompose(svgPath: String): String {
        val commands = mutableListOf<String>()
        val tokens = tokenizePath(svgPath)
        var i = 0
        var lastCommand = ""  // 记录上一个命令，用于处理隐式命令
        
        while (i < tokens.size) {
            val cmd = tokens[i]
            
            when {
                cmd in listOf("M", "m", "L", "l", "H", "h", "V", "v", "C", "c", "S", "s", "Q", "q", "T", "t", "A", "a", "Z", "z") -> {
                    i++
                    when (cmd) {
                        "M", "m" -> {
                            // 可能有多个坐标对，第一个是 moveTo，后续的是隐式的 lineTo
                            var first = true
                            while (i < tokens.size && tokens[i].toFloatOrNull() != null) {
                                val x = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                if (first) {
                                    commands.add(if (cmd == "M") "moveTo(${x}f, ${y}f)" else "moveToRelative(${x}f, ${y}f)")
                                    first = false
                                    lastCommand = cmd
                                } else {
                                    // 后续坐标对是隐式的 lineTo
                                    commands.add(if (cmd == "M") "lineTo(${x}f, ${y}f)" else "lineToRelative(${x}f, ${y}f)")
                                }
                            }
                        }
                        "L", "l" -> {
                            while (i < tokens.size && tokens[i].toFloatOrNull() != null) {
                                val x = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                commands.add(if (cmd == "L") "lineTo(${x}f, ${y}f)" else "lineToRelative(${x}f, ${y}f)")
                            }
                            lastCommand = cmd
                        }
                        "H", "h" -> {
                            while (i < tokens.size && tokens[i].toFloatOrNull() != null) {
                                val x = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                commands.add(if (cmd == "H") "horizontalLineTo(${x}f)" else "horizontalLineToRelative(${x}f)")
                            }
                            lastCommand = cmd
                        }
                        "V", "v" -> {
                            while (i < tokens.size && tokens[i].toFloatOrNull() != null) {
                                val y = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                commands.add(if (cmd == "V") "verticalLineTo(${y}f)" else "verticalLineToRelative(${y}f)")
                            }
                            lastCommand = cmd
                        }
                        "C", "c" -> {
                            while (i < tokens.size && tokens[i].toFloatOrNull() != null) {
                                val x1 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y1 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val x2 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y2 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val x3 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y3 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                commands.add(if (cmd == "C") 
                                    "curveTo(${x1}f, ${y1}f, ${x2}f, ${y2}f, ${x3}f, ${y3}f)" 
                                else 
                                    "curveToRelative(${x1}f, ${y1}f, ${x2}f, ${y2}f, ${x3}f, ${y3}f)")
                            }
                            lastCommand = cmd
                        }
                        "S", "s" -> {
                            // Smooth cubic Bezier curve
                            while (i < tokens.size && tokens[i].toFloatOrNull() != null) {
                                val x2 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y2 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val x = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                commands.add(if (cmd == "S")
                                    "reflectiveCurveTo(${x2}f, ${y2}f, ${x}f, ${y}f)"
                                else
                                    "reflectiveCurveToRelative(${x2}f, ${y2}f, ${x}f, ${y}f)")
                            }
                            lastCommand = cmd
                        }
                        "Q", "q" -> {
                            while (i < tokens.size && tokens[i].toFloatOrNull() != null) {
                                val x1 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y1 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val x2 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y2 = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                commands.add(if (cmd == "Q")
                                    "quadTo(${x1}f, ${y1}f, ${x2}f, ${y2}f)"
                                else
                                    "quadToRelative(${x1}f, ${y1}f, ${x2}f, ${y2}f)")
                            }
                            lastCommand = cmd
                        }
                        "T", "t" -> {
                            // Smooth quadratic Bezier curve
                            while (i < tokens.size && tokens[i].toFloatOrNull() != null) {
                                val x = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                commands.add(if (cmd == "T")
                                    "reflectiveQuadTo(${x}f, ${y}f)"
                                else
                                    "reflectiveQuadToRelative(${x}f, ${y}f)")
                            }
                            lastCommand = cmd
                        }
                        "A", "a" -> {
                            while (i < tokens.size && tokens[i].toFloatOrNull() != null) {
                                val rx = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val ry = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val rotation = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val largeArc = tokens.getOrNull(i++)?.toFloatOrNull()?.toInt() == 1
                                val sweep = tokens.getOrNull(i++)?.toFloatOrNull()?.toInt() == 1
                                val x = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                val y = tokens.getOrNull(i++)?.toFloatOrNull() ?: break
                                commands.add(if (cmd == "A")
                                    "arcTo(${rx}f, ${ry}f, ${rotation}f, $largeArc, $sweep, ${x}f, ${y}f)"
                                else
                                    "arcToRelative(${rx}f, ${ry}f, ${rotation}f, $largeArc, $sweep, ${x}f, ${y}f)")
                            }
                            lastCommand = cmd
                        }
                        "Z", "z" -> {
                            commands.add("close()")
                            lastCommand = cmd
                        }
                    }
                }
                else -> {
                    // 不是命令字母，跳过无效的token
                    i++
                }
            }
        }
        
        return commands.joinToString("\n                ")
    }
    
    /**
     * 将 SVG 路径字符串分词
     */
    private fun tokenizePath(path: String): List<String> {
        val tokens = mutableListOf<String>()
        val current = StringBuilder()
        
        for (char in path) {
            when {
                char.isLetter() -> {
                    if (current.isNotEmpty()) {
                        tokens.add(current.toString())
                        current.clear()
                    }
                    tokens.add(char.toString())
                }
                char == ' ' || char == ',' -> {
                    if (current.isNotEmpty()) {
                        tokens.add(current.toString())
                        current.clear()
                    }
                }
                char == '-' -> {
                    if (current.isNotEmpty()) {
                        tokens.add(current.toString())
                        current.clear()
                    }
                    current.append(char)
                }
                char == '.' || char.isDigit() -> {
                    current.append(char)
                }
            }
        }
        
        if (current.isNotEmpty()) {
            tokens.add(current.toString())
        }
        
        return tokens
    }
    
    data class SvgData(
        val width: Float,
        val height: Float,
        val viewBoxWidth: Float,
        val viewBoxHeight: Float,
        val paths: List<PathData>
    )
    
    data class PathData(
        val path: String,
        val fill: String?,
        val stroke: String?,
        val strokeWidth: Float?
    )
}
