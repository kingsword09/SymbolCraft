package io.github.kingsword09.symbolcraft.utils

import java.io.File

/**
 * Utility functions for path resolution and cache management
 */
internal object PathUtils {

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
    fun resolveCacheDirectory(cacheDirPath: String, projectBuildDir: String): File {
        val cacheFile = File(cacheDirPath)

        return if (cacheFile.isAbsolute) {
            // Absolute path: use as-is (supports /absolute, C:\absolute, \\UNC\paths)
            cacheFile
        } else {
            // Relative path: resolve relative to project build directory
          File(projectBuildDir, cacheDirPath)
        }
    }

    /**
     * Determine if a cache directory is inside the project build directory
     *
     * @param cacheDir The cache directory to check
     * @param buildDir The project build directory
     * @return true if cache is inside build directory, false otherwise
     */
    fun isCacheInsideBuildDir(cacheDir: File, buildDir: File): Boolean {
        return try {
            val canonicalCacheDir = cacheDir.canonicalFile
            val canonicalBuildDir = buildDir.canonicalFile
            canonicalCacheDir.startsWith(canonicalBuildDir)
        } catch (e: Exception) {
            // If unable to determine, assume it's outside (be conservative)
            false
        }
    }
}