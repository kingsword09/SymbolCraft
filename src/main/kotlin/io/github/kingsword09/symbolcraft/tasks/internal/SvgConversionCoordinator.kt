package io.github.kingsword09.symbolcraft.tasks.internal

import io.github.kingsword09.symbolcraft.converter.NameTransformerFactory
import io.github.kingsword09.symbolcraft.converter.Svg2ComposeConverter
import org.gradle.api.logging.Logger

/**
 * Converts downloaded SVG directories into Compose-ready Kotlin sources per icon library.
 *
 * This class is intentionally decoupled from Gradle types to keep the conversion logic portable;
 * the only bridge to Gradle is the injected [Logger].
 */
internal class SvgConversionCoordinator(
    private val logger: Logger,
    private val converter: Svg2ComposeConverter = Svg2ComposeConverter(),
) {

    /**
     * Converts each library subdirectory produced by the download phase.
     *
     * @param context shared generation context holding output directories and DSL configuration
     * @param iconsByLibrary mapping of library identifier → icon names, primarily for logging
     */
    fun convert(context: GenerationContext, iconsByLibrary: Map<String, Set<String>>) {
        logger.lifecycle("🔄 Converting SVGs to Compose ImageVectors...")

        var totalConverted = 0

        iconsByLibrary.forEach { (libraryId, _) ->
            val libraryTempDir = context.tempDir.resolve(libraryId)
            if (!libraryTempDir.exists() || libraryTempDir.listFiles()?.isEmpty() != false) {
                logger.warn("⚠️ No SVG files found for library: $libraryId")
                return@forEach
            }

            val librarySubdir =
                when {
                    libraryId == "material-symbols" -> "materialsymbols"
                    libraryId.startsWith("external-") -> libraryId.removePrefix("external-")
                    else -> libraryId
                }

            val ext = context.extension
            val nameTransformer =
                if (ext.namingConfig.transformer.isPresent) {
                    ext.namingConfig.transformer.get()
                } else {
                    NameTransformerFactory.fromConvention(
                        convention = ext.namingConfig.namingConvention.get(),
                        suffix = ext.namingConfig.suffix.get(),
                        prefix = ext.namingConfig.prefix.get(),
                        removePrefix = ext.namingConfig.removePrefix.get(),
                        removeSuffix = ext.namingConfig.removeSuffix.get(),
                    )
                }

            logger.lifecycle("   📚 Converting library: $libraryId → icons/$librarySubdir/")
            if (ext.namingConfig.transformer.isPresent) {
                logger.debug("   🔄 Using custom transformer")
            } else {
                logger.debug(
                    "   🔄 Using transformer: ${ext.namingConfig.namingConvention.get()} (suffix='${ext.namingConfig.suffix.get()}', prefix='${ext.namingConfig.prefix.get()}')"
                )
            }

            try {
                converter.convertDirectory(
                    inputDirectory = libraryTempDir,
                    outputDirectory = context.outputDir,
                    packageName = context.packageName,
                    generatePreview = ext.generatePreview.get(),
                    previewAnnotationClass = ext.previewAnnotationClass.get(),
                    accessorName = "Icons",
                    allAssetsPropertyName = "AllIcons",
                    librarySubdir = librarySubdir,
                    nameTransformer = nameTransformer,
                )
                val iconCount = libraryTempDir.listFiles()?.size ?: 0
                totalConverted += iconCount
                logger.lifecycle("      ✅ Converted $iconCount icons")
            } catch (e: Exception) {
                logger.error("❌ SVG conversion failed for library $libraryId: ${e.message}")
                logger.error("   Stack trace: ${e.stackTraceToString()}")
                when {
                    e.message?.contains("directory", ignoreCase = true) == true -> {
                        logger.error(
                            "   💡 Directory issue: Check input/output directories exist and are writable"
                        )
                    }
                    e.message?.contains("package", ignoreCase = true) == true -> {
                        logger.error(
                            "   💡 Package issue: Check packageName is valid Kotlin package identifier"
                        )
                    }
                    e.message?.contains("SVG", ignoreCase = true) == true -> {
                        logger.error(
                            "   💡 SVG parsing issue: Some downloaded SVG files may be malformed"
                        )
                    }
                    else -> {
                        logger.error("   💡 Unexpected conversion error: ${e.javaClass.simpleName}")
                    }
                }
            }
        }

        logger.lifecycle("✅ Successfully converted $totalConverted icons total")
    }
}
