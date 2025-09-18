package io.github.kingsword09.symbolcraft.converter

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

/**
 * Converts SVG files to Jetpack Compose ImageVector Kotlin code
 */
class SvgToComposeConverter {
    
    data class ConversionResult(
        val fileName: String,
        val content: String
    )
    
    /**
     * Convert a single SVG file to Compose code
     */
    fun convertSvg(
        svgFile: File,
        packageName: String,
        iconName: String
    ): ConversionResult {
        val svgContent = svgFile.readText()
        val svgData = parseSvg(svgContent)
        val composeCode = generateComposeCode(svgData, packageName, iconName)
        
        return ConversionResult(
            fileName = "${iconName}.kt",
            content = composeCode
        )
    }
    
    /**
     * Convert all SVG files in a directory to Compose code
     */
    fun convertDirectory(
        inputDir: File,
        outputDir: File,
        packageName: String
    ): List<ConversionResult> {
        val results = mutableListOf<ConversionResult>()
        
        inputDir.listFiles { file -> file.extension == "svg" }?.forEach { svgFile ->
            val iconName = svgFile.nameWithoutExtension
                .split("_", "-")
                .joinToString("") { it.replaceFirstChar { char -> char.titlecase() } }
            
            val result = convertSvg(svgFile, packageName, iconName)
            
            // Write to output directory
            val packagePath = packageName.replace('.', '/')
            val outputFile = File(outputDir, "$packagePath/${result.fileName}")
            outputFile.parentFile.mkdirs()
            outputFile.writeText(result.content)
            
            results.add(result)
        }
        
        return results
    }
    
    private data class SvgData(
        val width: Float,
        val height: Float,
        val viewportWidth: Float,
        val viewportHeight: Float,
        val paths: List<PathData>
    )
    
    private data class PathData(
        val pathData: String,
        val fillColor: String? = null,
        val strokeColor: String? = null,
        val strokeWidth: Float? = null,
        val fillAlpha: Float = 1f,
        val strokeAlpha: Float = 1f
    )
    
    private fun parseSvg(svgContent: String): SvgData {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(svgContent.byteInputStream())
        
        val svgElement = document.documentElement
        
        // Parse dimensions
        val width = parseDimension(svgElement.getAttribute("width")) ?: 24f
        val height = parseDimension(svgElement.getAttribute("height")) ?: 24f
        
        // Parse viewBox
        val viewBox = svgElement.getAttribute("viewBox")
        val (viewportWidth, viewportHeight) = if (viewBox.isNotEmpty()) {
            val parts = viewBox.split(" ")
            if (parts.size >= 4) {
                parts[2].toFloatOrNull() to parts[3].toFloatOrNull()
            } else {
                width to height
            }
        } else {
            width to height
        }
        
        // Parse paths
        val paths = mutableListOf<PathData>()
        parsePaths(svgElement, paths)
        
        return SvgData(
            width = width,
            height = height,
            viewportWidth = viewportWidth ?: width,
            viewportHeight = viewportHeight ?: height,
            paths = paths
        )
    }
    
    private fun parsePaths(element: Element, paths: MutableList<PathData>) {
        val pathNodes = element.getElementsByTagName("path")
        for (i in 0 until pathNodes.length) {
            val pathElement = pathNodes.item(i) as? Element ?: continue
            
            val d = pathElement.getAttribute("d")
            if (d.isNotEmpty()) {
                val fill = pathElement.getAttribute("fill").takeIf { it.isNotEmpty() && it != "none" }
                val stroke = pathElement.getAttribute("stroke").takeIf { it.isNotEmpty() && it != "none" }
                val strokeWidth = pathElement.getAttribute("stroke-width").toFloatOrNull()
                val fillOpacity = pathElement.getAttribute("fill-opacity").toFloatOrNull() ?: 1f
                val strokeOpacity = pathElement.getAttribute("stroke-opacity").toFloatOrNull() ?: 1f
                
                paths.add(PathData(
                    pathData = d,
                    fillColor = fill ?: "#000000",
                    strokeColor = stroke,
                    strokeWidth = strokeWidth,
                    fillAlpha = fillOpacity,
                    strokeAlpha = strokeOpacity
                ))
            }
        }
        
        // Also check for nested g elements
        val gNodes = element.getElementsByTagName("g")
        for (i in 0 until gNodes.length) {
            val gElement = gNodes.item(i) as? Element ?: continue
            parsePaths(gElement, paths)
        }
    }
    
    private fun parseDimension(value: String): Float? {
        return value.replace(Regex("[^0-9.]"), "").toFloatOrNull()
    }
    
    private fun generateComposeCode(
        svgData: SvgData,
        packageName: String,
        iconName: String
    ): String {
        val vectorName = iconName
        val objectName = iconName.replaceFirstChar { it.lowercase() }
        
        return buildString {
            appendLine("package $packageName")
            appendLine()
            appendLine("import androidx.compose.ui.graphics.Color")
            appendLine("import androidx.compose.ui.graphics.PathFillType")
            appendLine("import androidx.compose.ui.graphics.SolidColor")
            appendLine("import androidx.compose.ui.graphics.StrokeCap")
            appendLine("import androidx.compose.ui.graphics.StrokeJoin")
            appendLine("import androidx.compose.ui.graphics.vector.ImageVector")
            appendLine("import androidx.compose.ui.graphics.vector.path")
            appendLine("import androidx.compose.ui.unit.dp")
            appendLine()
            appendLine("val $vectorName: ImageVector")
            appendLine("    get() {")
            appendLine("        if (_$objectName != null) {")
            appendLine("            return _$objectName!!")
            appendLine("        }")
            appendLine("        _$objectName = ImageVector.Builder(")
            appendLine("            name = \"$iconName\",")
            appendLine("            defaultWidth = ${svgData.width}.dp,")
            appendLine("            defaultHeight = ${svgData.height}.dp,")
            appendLine("            viewportWidth = ${svgData.viewportWidth}f,")
            appendLine("            viewportHeight = ${svgData.viewportHeight}f")
            appendLine("        ).apply {")
            
            svgData.paths.forEach { path ->
                appendLine("            path(")
                if (path.fillColor != null) {
                    appendLine("                fill = SolidColor(Color(0xFF${path.fillColor.removePrefix("#")})),")
                } else {
                    appendLine("                fill = null,")
                }
                if (path.fillAlpha < 1f) {
                    appendLine("                fillAlpha = ${path.fillAlpha}f,")
                }
                if (path.strokeColor != null) {
                    appendLine("                stroke = SolidColor(Color(0xFF${path.strokeColor.removePrefix("#")})),")
                } else {
                    appendLine("                stroke = null,")
                }
                if (path.strokeAlpha < 1f) {
                    appendLine("                strokeAlpha = ${path.strokeAlpha}f,")
                }
                if (path.strokeWidth != null) {
                    appendLine("                strokeLineWidth = ${path.strokeWidth}f,")
                }
                appendLine("                strokeLineCap = StrokeCap.Butt,")
                appendLine("                strokeLineJoin = StrokeJoin.Miter,")
                appendLine("                strokeLineMiter = 4.0f,")
                appendLine("                pathFillType = PathFillType.NonZero")
                appendLine("            ) {")
                
                // Convert SVG path to Compose path commands
                appendLine(convertPathData(path.pathData))
                
                appendLine("            }")
            }
            
            appendLine("        }.build()")
            appendLine("        return _$objectName!!")
            appendLine("    }")
            appendLine()
            appendLine("private var _$objectName: ImageVector? = null")
        }
    }
    
    private fun convertPathData(pathData: String): String {
        val commands = mutableListOf<String>()
        var i = 0
        val data = pathData.trim()
        
        while (i < data.length) {
            when (val cmd = data[i]) {
                'M', 'm' -> {
                    val (x, y, newIndex) = parseCoordinatePair(data, i + 1)
                    commands.add("                moveTo(${x}f, ${y}f)")
                    i = newIndex
                }
                'L', 'l' -> {
                    val (x, y, newIndex) = parseCoordinatePair(data, i + 1)
                    commands.add("                lineTo(${x}f, ${y}f)")
                    i = newIndex
                }
                'H', 'h' -> {
                    val (x, newIndex) = parseNumber(data, i + 1)
                    commands.add("                horizontalLineTo(${x}f)")
                    i = newIndex
                }
                'V', 'v' -> {
                    val (y, newIndex) = parseNumber(data, i + 1)
                    commands.add("                verticalLineTo(${y}f)")
                    i = newIndex
                }
                'C', 'c' -> {
                    val (x1, y1, i1) = parseCoordinatePair(data, i + 1)
                    val (x2, y2, i2) = parseCoordinatePair(data, i1)
                    val (x3, y3, i3) = parseCoordinatePair(data, i2)
                    commands.add("                curveTo(${x1}f, ${y1}f, ${x2}f, ${y2}f, ${x3}f, ${y3}f)")
                    i = i3
                }
                'S', 's' -> {
                    val (x2, y2, i1) = parseCoordinatePair(data, i + 1)
                    val (x3, y3, i2) = parseCoordinatePair(data, i1)
                    commands.add("                reflectiveCurveTo(${x2}f, ${y2}f, ${x3}f, ${y3}f)")
                    i = i2
                }
                'Q', 'q' -> {
                    val (x1, y1, i1) = parseCoordinatePair(data, i + 1)
                    val (x2, y2, i2) = parseCoordinatePair(data, i1)
                    commands.add("                quadTo(${x1}f, ${y1}f, ${x2}f, ${y2}f)")
                    i = i2
                }
                'T', 't' -> {
                    val (x, y, newIndex) = parseCoordinatePair(data, i + 1)
                    commands.add("                reflectiveQuadTo(${x}f, ${y}f)")
                    i = newIndex
                }
                'A', 'a' -> {
                    val (rx, ry, i1) = parseCoordinatePair(data, i + 1)
                    val (rotation, i2) = parseNumber(data, i1)
                    val (largeArc, i3) = parseNumber(data, i2)
                    val (sweep, i4) = parseNumber(data, i3)
                    val (x, y, i5) = parseCoordinatePair(data, i4)
                    
                    val isLargeArc = largeArc.toInt() != 0
                    val isSweep = sweep.toInt() != 0
                    
                    if (cmd == 'A') {
                        commands.add("                arcTo(${rx}f, ${ry}f, ${rotation}f, $isLargeArc, $isSweep, ${x}f, ${y}f)")
                    } else {
                        commands.add("                arcToRelative(${rx}f, ${ry}f, ${rotation}f, $isLargeArc, $isSweep, ${x}f, ${y}f)")
                    }
                    i = i5
                }
                'Z', 'z' -> {
                    commands.add("                close()")
                    i++
                }
                ' ', ',' -> i++
                else -> i++
            }
        }
        
        return commands.joinToString("\n")
    }
    
    private fun parseNumber(data: String, startIndex: Int): Pair<Float, Int> {
        var i = startIndex
        while (i < data.length && (data[i] == ' ' || data[i] == ',')) i++
        
        val start = i
        if (i < data.length && (data[i] == '-' || data[i] == '+')) i++
        
        while (i < data.length && (data[i].isDigit() || data[i] == '.')) i++
        
        val number = if (start < i) data.substring(start, i).toFloatOrNull() ?: 0f else 0f
        return number to i
    }
    
    private fun parseCoordinatePair(data: String, startIndex: Int): Triple<Float, Float, Int> {
        val (x, i1) = parseNumber(data, startIndex)
        val (y, i2) = parseNumber(data, i1)
        return Triple(x, y, i2)
    }
}
