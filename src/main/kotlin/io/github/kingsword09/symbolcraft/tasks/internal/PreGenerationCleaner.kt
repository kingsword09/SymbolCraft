package io.github.kingsword09.symbolcraft.tasks.internal

import io.github.kingsword09.symbolcraft.model.IconConfig
import io.github.kingsword09.symbolcraft.utils.PathUtils
import java.io.File
import org.gradle.api.logging.Logger

/**
 * Performs filesystem hygiene before generation begins.
 *
 * Responsibilities include:
 * * removing previously generated Kotlin files so the next pass produces deterministic output
 * * pruning stale cache entries when we know the cache is isolated to the current project
 * * emitting detailed logs so contributors understand *why* files disappear
 */
internal class PreGenerationCleaner(private val logger: Logger) {

    fun clean(context: GenerationContext) {
        cleanOldGeneratedFiles(context.outputDir, context.packageName)

        val cacheEnabled = context.extension.cacheEnabled.get()
        if (cacheEnabled && shouldCleanCache(context.cacheBaseDir, context.projectBuildDir)) {
            cleanUnusedCache(context.svgCacheDir, context.config)
        }
    }

    /**
     * Determines whether it is safe to delete cache entries.
     *
     * Shared caches (outside the consumer build directory) are intentionally preserved because
     * multiple projects or IDE syncs may reuse them concurrently; nuking shared directories would
     * introduce subtle race conditions.
     */
    private fun shouldCleanCache(cacheBaseDir: File, projectBuildDir: String): Boolean {
        val isInsideBuildDir = PathUtils.isCacheInsideBuildDir(cacheBaseDir, File(projectBuildDir))

        if (!isInsideBuildDir) {
            logger.lifecycle(
                "ℹ️  Cache cleanup skipped: Using shared cache outside build directory"
            )
            logger.lifecycle("   Cache location: ${cacheBaseDir.canonicalFile.absolutePath}")
            logger.lifecycle("   Shared caches are preserved to avoid conflicts across projects")
        }

        return isInsideBuildDir
    }

    /**
     * Removes Kotlin files emitted by previous runs so we never end up with orphaned classes after
     * configuration changes (for instance when a user drops an icon from their DSL block).
     */
    private fun cleanOldGeneratedFiles(outputDir: File, packageName: String) {
        val packagePath = packageName.replace('.', '/')
        val iconsBaseDir = File(outputDir, "$packagePath/icons")
        val mainSymbolsFile = File(outputDir, "$packagePath/__Icons.kt")

        var cleanedCount = 0

        if (iconsBaseDir.exists()) {
            iconsBaseDir.walkTopDown().forEach { file ->
                if (file.isFile && file.extension == "kt") {
                    logger.debug(
                        "🧹 Cleaning old generated file: ${file.relativeTo(iconsBaseDir).path}"
                    )
                    file.delete()
                    cleanedCount++
                }
            }
        }

        if (mainSymbolsFile.exists()) {
            logger.debug("🧹 Cleaning main symbols file")
            mainSymbolsFile.delete()
            cleanedCount++
        }

        if (cleanedCount > 0) {
            logger.lifecycle("🧹 Cleaned $cleanedCount old generated files")
        }
    }

    /**
     * Deletes SVG cache files that no longer map to any requested icon signature.
     *
     * Cache files are named after `IconConfig#getCacheKey`, so once a configuration entry
     * disappears, we can deterministically identify the orphaned payload and drop it.
     */
    private fun cleanUnusedCache(cacheDir: File, config: Map<String, List<IconConfig>>) {
        if (!cacheDir.exists()) return

        val requiredCacheKeys =
            config
                .flatMap { (iconName, iconConfigs) ->
                    iconConfigs.map { iconConfig -> iconConfig.getCacheKey(iconName) }
                }
                .toSet()

        var cleanedCount = 0

        cacheDir.listFiles()?.forEach { file ->
            if (file.isFile) {
                val cacheKey = file.nameWithoutExtension
                if (cacheKey !in requiredCacheKeys) {
                    logger.debug("🧹 Cleaning unused cache file: ${file.name}")
                    if (file.delete()) {
                        cleanedCount++
                    } else {
                        logger.warn(
                            "   ⚠️ Failed to delete unused cache file: ${file.absolutePath}"
                        )
                    }
                }
            }
        }

        if (cleanedCount > 0) {
            logger.lifecycle("🧹 Cleaned $cleanedCount unused cache files")
        }
    }
}
