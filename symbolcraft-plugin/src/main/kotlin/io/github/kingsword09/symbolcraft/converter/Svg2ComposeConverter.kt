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
     * @param generatePreview Whether to generate Compose preview functions
     * @param accessorName Name of the object that will contain all icons
     * @param allAssetsPropertyName Name of the property that contains all icons
     * @param librarySubdir Optional subdirectory name for organizing icons by library (e.g., "materialsymbols", "bootstrap-icons")
     * @param nameTransformer Custom naming transformer for icon class names. If null, uses Material Symbols transformer.
     */
    fun convertDirectory(
        inputDirectory: File,
        outputDirectory: File,
        packageName: String,
        generatePreview: Boolean = true,
        accessorName: String = "GeneratedIcons",
        allAssetsPropertyName: String = "AllIcons",
        librarySubdir: String? = null,
        nameTransformer: IconNameTransformer? = null
    ) {
        if (!inputDirectory.exists() || !inputDirectory.isDirectory) {
            throw IllegalArgumentException("Input directory does not exist or is not a directory: ${inputDirectory.absolutePath}")
        }

        // Create output directory if it doesn't exist
        outputDirectory.mkdirs()

        // Adjust package name to include library subdirectory if specified
        val effectivePackage = if (librarySubdir != null) {
            "$packageName.icons.$librarySubdir"
        } else {
            packageName
        }

        // Use Svg2Compose to convert all SVG files
        val transformer = nameTransformer ?: NameTransformerFactory.pascalCase()
        
        Svg2Compose.parse(
            applicationIconPackage = effectivePackage,
            accessorName = accessorName,
            outputSourceDirectory = outputDirectory,
            vectorsDirectory = inputDirectory,
            type = VectorType.SVG,
            allAssetsPropertyName = allAssetsPropertyName,
            iconNameTransformer = { name, _ ->
                // Use the provided or default name transformer
                transformer.transform(name)
            },
            generatePreview = generatePreview
        )

        // Post-process generated files to ensure deterministic output
        makeOutputDeterministic(outputDirectory, packageName, librarySubdir)
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
     * Check if the converter can process the given file
     */
    fun canProcess(file: File): Boolean {
        val extension = file.extension.lowercase()
        return extension == "svg" || extension == "xml"
    }

    /**
     * Post-process generated files to ensure deterministic output
     * This removes timestamps and other non-deterministic content
     *
     * @param outputDirectory Base output directory
     * @param packageName Package name for generated files
     * @param librarySubdir Optional subdirectory for library-specific icons
     */
    private fun makeOutputDeterministic(outputDirectory: File, packageName: String, librarySubdir: String? = null) {
        val packagePath = packageName.replace('.', '/')
        val baseDir = File(outputDirectory, packagePath)

        // If librarySubdir is specified, process that directory; otherwise process the base directory
        val targetDir = if (librarySubdir != null) {
            File(baseDir, "icons/$librarySubdir")
        } else {
            baseDir
        }

        if (!targetDir.exists()) return

        targetDir.walkTopDown()
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
