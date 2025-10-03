package io.github.kingsword09.symbolcraft.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class CleanSymbolsCacheTask : DefaultTask() {
    @get:Internal
    abstract val extension: Property<MaterialSymbolsExtension>

    @get:Internal
    abstract val projectBuildDir: Property<String>

    @TaskAction
    fun clean() {
        val cacheDirPath = extension.get().cacheDirectory.get()
        val projectBuildDirPath = projectBuildDir.get()

        // Resolve cache directory: support both absolute and relative paths
        val cacheBaseDir = resolveCacheDirectory(cacheDirPath, projectBuildDirPath)

        logger.lifecycle("🧹 Cleaning Material Symbols cache...")
        logger.lifecycle("📂 Cache location: ${cacheBaseDir.absolutePath}")

        if (cacheBaseDir.exists()) {
            val svgCacheDir = File(cacheBaseDir, "svg-cache")
            val tempSvgDir = File(cacheBaseDir, "temp-svgs")

            var deletedCount = 0

            // Clean SVG cache
            if (svgCacheDir.exists()) {
                val cacheFiles = svgCacheDir.listFiles()
                deletedCount += cacheFiles?.size ?: 0
                svgCacheDir.deleteRecursively()
                logger.lifecycle("   🧹 Cleaned SVG cache: ${cacheFiles?.size ?: 0} files")
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

    /**
     * Resolve cache directory path, supporting both absolute and relative paths
     *
     * @param cacheDirPath The cache directory path from configuration
     * @param projectBuildDir The project build directory
     * @return Resolved File pointing to the cache base directory
     *
     * Examples:
     * - "material-symbols-cache" -> <projectBuildDir>/material-symbols-cache
     * - "/var/tmp/symbols" -> /var/tmp/symbols (absolute path preserved)
     * - "C:\cache\symbols" -> C:\cache\symbols (Windows absolute path preserved)
     * - "\\server\share\cache" -> \\server\share\cache (UNC path preserved)
     */
    private fun resolveCacheDirectory(cacheDirPath: String, projectBuildDir: String): File {
        val cacheFile = File(cacheDirPath)

        return if (cacheFile.isAbsolute) {
            // Absolute path: use as-is (supports /absolute, C:\absolute, \\UNC\paths)
            cacheFile
        } else {
            // Relative path: resolve relative to project build directory
            File(projectBuildDir, cacheDirPath)
        }
    }
}
