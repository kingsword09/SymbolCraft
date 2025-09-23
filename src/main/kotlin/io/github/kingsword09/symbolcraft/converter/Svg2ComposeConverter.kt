package io.github.kingsword09.symbolcraft.converter

import io.github.kingsword09.svg2compose.Svg2Compose
import io.github.kingsword09.svg2compose.VectorType
import java.io.File

/**
 * SVG to Compose converter using the DevSrSouza/svg-to-compose library
 * 
 * This converter properly parses SVG files and generates Compose ImageVector code
 * It supports both SVG and Android Vector Drawable formats
 */
class Svg2ComposeConverter {
    
    /**
     * Convert SVG files from a directory to Compose code
     * 
     * @param inputDirectory Directory containing SVG files
     * @param outputDirectory Directory where generated Kotlin files will be saved
     * @param packageName Package name for generated files
     * @param accessorName Name of the object that will contain all icons
     * @param allAssetsPropertyName Name of the property that contains all icons
     */
    fun convertDirectory(
        inputDirectory: File,
        outputDirectory: File,
        packageName: String,
        generatePreview: Boolean = true,
        accessorName: String = "GeneratedIcons",
        allAssetsPropertyName: String = "AllIcons"
    ) {
        if (!inputDirectory.exists() || !inputDirectory.isDirectory) {
            throw IllegalArgumentException("Input directory does not exist or is not a directory: ${inputDirectory.absolutePath}")
        }
        
        // Create output directory if it doesn't exist
        outputDirectory.mkdirs()
        
        // Use Svg2Compose to convert all SVG files
        Svg2Compose.parse(
            applicationIconPackage = packageName,
            accessorName = accessorName,
            outputSourceDirectory = outputDirectory,
            vectorsDirectory = inputDirectory,
            type = VectorType.SVG,
            allAssetsPropertyName = allAssetsPropertyName,
            iconNameTransformer = { name, _ ->
                // Transform icon names to match our naming convention
                // e.g., "home_400_rounded_unfilled.svg" -> "HomeW400Rounded"
                transformIconName(name)
            },
            generatePreview = generatePreview
        )

        // Post-process generated files to ensure deterministic output
        makeOutputDeterministic(outputDirectory, packageName)
    }
    
    /**
     * Convert a single SVG file to Compose code
     * 
     * @param svgFile The SVG file to convert
     * @param outputFile The output Kotlin file
     * @param packageName Package name for the generated file
     * @param iconName Name of the generated icon
     */
    fun convertSingleFile(
        svgFile: File,
        outputFile: File,
        packageName: String,
        iconName: String
    ) {
        if (!svgFile.exists() || !svgFile.isFile) {
            throw IllegalArgumentException("SVG file does not exist: ${svgFile.absolutePath}")
        }
        
        // Create a temporary directory for single file conversion
        val tempDir = File(System.getProperty("java.io.tmpdir"), "svg2compose_temp_${System.currentTimeMillis()}")
        tempDir.mkdirs()
        
        try {
            // Copy the SVG to temp directory
            val tempSvgFile = File(tempDir, "${iconName}.svg")
            svgFile.copyTo(tempSvgFile)
            
            // Convert using directory method
            val outputDir = outputFile.parentFile
            outputDir.mkdirs()
            
            Svg2Compose.parse(
                applicationIconPackage = packageName,
                accessorName = iconName,
                outputSourceDirectory = outputDir,
                vectorsDirectory = tempDir,
                type = VectorType.SVG,
                allAssetsPropertyName = "AllIcons" // Set a default name
            )
            
            // Rename the generated file if needed
            val generatedFile = File(outputDir, "$packageName/$iconName.kt")
            if (generatedFile.exists() && generatedFile.absolutePath != outputFile.absolutePath) {
                generatedFile.renameTo(outputFile)
            }
        } finally {
            // Clean up temp directory
            tempDir.deleteRecursively()
        }
    }
    
    /**
     * Transform icon file names to match our naming convention
     * 
     * Examples:
     * - "home_400_rounded_unfilled" -> "HomeW400Rounded"
     * - "search_500_outlined_filled" -> "SearchW500OutlinedFill"
     * - "person_400_outlined_unfilled" -> "PersonW400Outlined"
     */
    fun transformIconName(fileName: String): String {
        // Remove .svg extension if present
        val nameWithoutExt = fileName.removeSuffix(".svg")
        
        // Parse the file name format: {icon}_{weight}_{variant}_{fill}
        val parts = nameWithoutExt.split("_")
        if (parts.size < 4) {
            // If the format doesn't match, just capitalize the name
            return nameWithoutExt.split("_", "-")
                .joinToString("") { it.replaceFirstChar { char -> char.titlecase() } }
        }
        
        val iconName = parts[0]
        val weight = parts[1]
        val variant = parts[2]
        val fill = parts[3]
        
        // Build the transformed name
        val transformedName = buildString {
            // Capitalize icon name
            append(iconName.replaceFirstChar { it.titlecase() })
            
            // Add weight with W prefix
            append("W")
            append(weight)
            
            // Capitalize variant
            append(variant.replaceFirstChar { it.titlecase() })
            
            // Add Fill suffix only if filled
            if (fill == "filled") {
                append("Fill")
            }
        }
        
        return transformedName
    }
    
    /**
     * Check if the converter can process the given file
     */
    fun canProcess(file: File): Boolean {
        val extension = file.extension.lowercase()
        return extension == "svg" || extension == "xml"
    }

    /**
     * Post-process generated files to ensure deterministic output
     * This removes timestamps and other non-deterministic content
     */
    private fun makeOutputDeterministic(outputDirectory: File, packageName: String) {
        val packagePath = packageName.replace('.', '/')
        val generatedDir = File(outputDirectory, packagePath)

        if (!generatedDir.exists()) return

        generatedDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { file ->
                val content = file.readText()
                val deterministicContent = makeDeterministic(content)
                if (content != deterministicContent) {
                    file.writeText(deterministicContent)
                }
            }
    }

    /**
     * Remove non-deterministic elements from generated code
     */
    private fun makeDeterministic(content: String): String {
        return content
            // Remove timestamp comments (common patterns)
            .replace(Regex("//.*Generated on.*\\d{4}-\\d{2}-\\d{2}.*"), "// Generated by SymbolCraft")
            .replace(Regex("//.*Created on.*\\d{4}-\\d{2}-\\d{2}.*"), "// Generated by SymbolCraft")
            .replace(Regex("//.*Date:.*\\d{4}-\\d{2}-\\d{2}.*"), "// Generated by SymbolCraft")
            .replace(Regex("//.*\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}.*"), "// Generated by SymbolCraft")
            // Remove specific svg-to-compose timestamp patterns
            .replace(Regex("//\\s*Generated by svg-to-compose.*"), "// Generated by SymbolCraft")
            .replace(Regex("//\\s*Converted from.*\\d{4}-\\d{2}-\\d{2}.*"), "// Generated by SymbolCraft")
            // Normalize floating point precision (keep 2 decimal places max)
            .replace(Regex("(\\d+\\.\\d{3,})f")) { matchResult ->
                val number = matchResult.groupValues[1].toDouble()
                String.format("%.2f", number) + "f"
            }
            // Sort imports for consistency
            .let { sortImportsIfNeeded(it) }
    }

    /**
     * Sort imports for deterministic order
     */
    private fun sortImportsIfNeeded(content: String): String {
        val lines = content.lines()
        val packageLineIndex = lines.indexOfFirst { it.startsWith("package ") }
        if (packageLineIndex == -1) return content

        val importsStartIndex = lines.indexOfFirst { it.startsWith("import ") && it.indexOf("import ") >= packageLineIndex }
        if (importsStartIndex == -1) return content

        val importsEndIndex = lines.indexOfLast { it.startsWith("import ") }
        if (importsEndIndex == -1) return content

        val beforeImports = lines.subList(0, importsStartIndex)
        val imports = lines.subList(importsStartIndex, importsEndIndex + 1).sorted()
        val afterImports = lines.subList(importsEndIndex + 1, lines.size)

        return (beforeImports + imports + afterImports).joinToString("\n")
    }
}
