package io.github.kingsword09.symbolcraft.download

import io.github.kingsword09.symbolcraft.model.SymbolStyle
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.path.*

/**
 * Downloads Material Symbols SVG files from esm.sh CDN with local caching
 */
class SvgDownloader(
    private val cacheDirectory: String,
    private val cacheEnabled: Boolean = true
) {
    private val httpClient = HttpClient(CIO) {
        engine {
            requestTimeout = 30_000
        }
    }
    
    private val cachePath = Path(cacheDirectory)
    
    init {
        if (cacheEnabled) {
            cachePath.createDirectories()
        }
    }
    
    suspend fun downloadSvg(iconName: String, style: SymbolStyle): String? = withContext(Dispatchers.IO) {
        val url = style.buildEsmUrl(iconName)
        val cacheKey = style.getCacheKey(iconName)
        
        // Check cache first
        if (cacheEnabled) {
            val cachedContent = getCachedSvg(cacheKey)
            if (cachedContent != null) {
                return@withContext cachedContent
            }
        }
        
        // Download from esm.sh
        try {
            println("Downloading SVG: $url")
            val response = httpClient.get(url)
            
            if (response.status.isSuccess()) {
                val svgContent = response.bodyAsText()
                
                // Cache the content
                if (cacheEnabled && svgContent.isNotBlank()) {
                    cacheSvg(cacheKey, svgContent, url)
                }
                
                return@withContext svgContent
            } else {
                println("Failed to download $url: ${response.status}")
                return@withContext null
            }
        } catch (e: Exception) {
            println("Error downloading SVG from $url: ${e.message}")
            return@withContext null
        }
    }
    
    private fun getCachedSvg(cacheKey: String): String? {
        val cacheFile = cachePath / "$cacheKey.svg"
        val metaFile = cachePath / "$cacheKey.meta"
        
        if (cacheFile.exists() && metaFile.exists()) {
            try {
                val meta = metaFile.readLines()
                if (meta.size >= 2) {
                    val timestamp = meta[0].toLong()
                    
                    // Check if cache is still valid (7 days)
                    val maxAge = 7 * 24 * 60 * 60 * 1000L // 7 days
                    if (System.currentTimeMillis() - timestamp < maxAge) {
                        return cacheFile.readText()
                    }
                }
            } catch (e: Exception) {
                // Cache corrupted, will re-download
            }
        }
        
        return null
    }
    
    private fun cacheSvg(cacheKey: String, content: String, url: String) {
        try {
            val cacheFile = cachePath / "$cacheKey.svg"
            val metaFile = cachePath / "$cacheKey.meta"
            
            cacheFile.writeText(content)
            metaFile.writeText("${System.currentTimeMillis()}\n$url\n${content.hashCode()}")
        } catch (e: Exception) {
            println("Failed to cache SVG: ${e.message}")
        }
    }
    
    fun cleanup() {
        httpClient.close()
    }
    
    /**
     * Get cache statistics
     */
    fun getCacheStats(): CacheStats {
        if (!cacheEnabled || !cachePath.exists()) {
            return CacheStats(0, 0)
        }
        
        val svgFiles = cachePath.listDirectoryEntries("*.svg")
        val totalSize = svgFiles.sumOf { it.fileSize() }
        
        return CacheStats(svgFiles.size, totalSize)
    }
    
    data class CacheStats(
        val fileCount: Int,
        val totalSizeBytes: Long
    ) {
        val totalSizeKB: Double get() = totalSizeBytes / 1024.0
        val totalSizeMB: Double get() = totalSizeKB / 1024.0
    }
}