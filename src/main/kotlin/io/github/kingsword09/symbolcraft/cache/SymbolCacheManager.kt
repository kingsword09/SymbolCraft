package io.github.kingsword09.symbolcraft.cache

import java.io.File

/**
 * Simple cache manager for SymbolCraft.
 * Responsible for handling cache directory lifecycle and basic stats.
 */
class SymbolCacheManager(
    private val cacheDir: File,
    private val enabled: Boolean = true
) {
    init {
        if (enabled) cacheDir.mkdirs()
    }

    fun isEnabled(): Boolean = enabled

    fun directory(): File = cacheDir

    fun clear(): Boolean {
        if (!cacheDir.exists()) return true
        return cacheDir.deleteRecursively().also { cacheDir.mkdirs() }
    }

    fun sizeBytes(): Long = if (!cacheDir.exists()) 0 else cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }

    fun fileCount(): Int = if (!cacheDir.exists()) 0 else cacheDir.walkTopDown().count { it.isFile }
}

