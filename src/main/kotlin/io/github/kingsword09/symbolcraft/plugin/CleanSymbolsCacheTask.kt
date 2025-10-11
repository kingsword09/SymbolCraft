package io.github.kingsword09.symbolcraft.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Task that deletes cached SVG assets created by [GenerateSymbolsTask].
 *
 * Exposed to consumers as `cleanSymbolsCache`.
 */
abstract class CleanSymbolsCacheTask : DefaultTask() {
    @get:Internal
    abstract val extension: Property<MaterialSymbolsExtension>

    @get:Internal
    abstract val projectBuildDir: Property<String>

    /**
     * Deletes the configured cache directory.
     */
    @TaskAction
    fun clean() {
        val cacheDirPath = extension.get().cacheDirectory.get()
        val projectBuildDirPath = projectBuildDir.get()

        // Resolve cache directory: support both absolute and relative paths
        val cacheBaseDir = PathUtils.resolveCacheDirectory(cacheDirPath, projectBuildDirPath)

        logger.lifecycle("🧹 Cleaning Material Symbols cache...")
        logger.lifecycle("📂 Cache location: ${cacheBaseDir.absolutePath}")

        if (cacheBaseDir.exists()) {
            val svgCacheDir = File(cacheBaseDir, "svg-cache")
            val tempSvgDir = File(cacheBaseDir, "temp-svgs")

            var deletedCount = 0

            // Clean SVG cache
            if (svgCacheDir.exists()) {
                val fileCount = svgCacheDir.listFiles()?.size ?: 0
                if (svgCacheDir.deleteRecursively()) {
                    deletedCount += fileCount
                    logger.lifecycle("   🧹 Cleaned SVG cache: $fileCount files")
                } else {
                    logger.warn("   ⚠️ Failed to clean SVG cache directory: ${svgCacheDir.absolutePath}")
                }
            }

            // Clean temp SVGs
            if (tempSvgDir.exists()) {
                val tempFiles = tempSvgDir.listFiles()
                deletedCount += tempFiles?.size ?: 0
                tempSvgDir.deleteRecursively()
                logger.lifecycle("   🧹 Cleaned temp SVGs: ${tempFiles?.size ?: 0} files")
            }

            // Clean the cache directory itself if empty
            if (cacheBaseDir.listFiles()?.isEmpty() == true) {
                cacheBaseDir.delete()
                logger.lifecycle("   🧹 Removed empty cache directory")
            }

            logger.lifecycle("✅ Total cache cleaned: $deletedCount files")
        } else {
            logger.lifecycle("ℹ️  No cache to clean (directory does not exist)")
        }
    }
}
