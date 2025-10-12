package io.github.kingsword09.symbolcraft.download

import io.github.kingsword09.symbolcraft.model.IconConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * Downloads icon SVG files from CDN with local caching and validation.
 *
 * Supports multiple icon libraries through the IconConfig interface.
 *
 * Features:
 * - Configurable CDN base URL with fallback support
 * - Content validation (type, size, structure)
 * - 7-day cache with metadata tracking
 * - HTTPS enforcement for security
 *
 * @property cacheDirectory Directory path for storing cached SVG files
 * @property cacheEnabled Whether to enable caching (default: true)
 * @property cdnBaseUrl Base URL for the CDN serving icons (default: esm.sh)
 * @property logger Optional logger for status messages, if not provided uses println
 */
class SvgDownloader(
    private val cacheDirectory: String,
    private val cacheEnabled: Boolean = true,
    private val cdnBaseUrl: String = DEFAULT_CDN_BASE_URL,
    private val logger: ((String) -> Unit)? = null
) {
    companion object {
        /** Default timeout for HTTP requests in milliseconds */
        private const val REQUEST_TIMEOUT_MS = 30_000L

        /** Cache validity period in days */
        private const val CACHE_MAX_AGE_DAYS = 7

        /** Cache validity period in milliseconds */
        private const val CACHE_MAX_AGE_MS = CACHE_MAX_AGE_DAYS * 24 * 60 * 60 * 1000L

        /** Maximum allowed SVG file size (10MB) to prevent DoS attacks */
        private const val MAX_SVG_SIZE = 10 * 1024 * 1024

        /** Default CDN base URL for Material Symbols */
        const val DEFAULT_CDN_BASE_URL = "https://esm.sh"

        /** Maximum number of concurrent downloads */
        const val MAX_CONNECTIONS_COUNT = 50

        /** Maximum connections per route */
        const val MAX_CONNECTIONS_PER_ROUTE = 20
    }

    private val httpClient = HttpClient(CIO) {
        engine {
            requestTimeout = REQUEST_TIMEOUT_MS
            maxConnectionsCount = MAX_CONNECTIONS_COUNT
            endpoint {
                maxConnectionsPerRoute = MAX_CONNECTIONS_PER_ROUTE
            }
        }
    }

    /**
     * Log a message using the provided logger or fall back to println
     */
    private fun log(message: String) {
        logger?.invoke(message) ?: println(message)
    }
    
    private val cachePath = Path(cacheDirectory)
    
    init {
        if (cacheEnabled) {
            cachePath.createDirectories()
        }
    }
    
    /**
     * Download an SVG file for the given icon and configuration.
     *
     * This method:
     * 1. Checks the local cache first
     * 2. Downloads from CDN if not cached
     * 3. Validates content type, size, and structure
     * 4. Caches valid content for future use
     *
     * @param iconName Name of the icon
     * @param config Icon library configuration
     * @return SVG content as string, or null if download fails
     */
    suspend fun downloadSvg(iconName: String, config: IconConfig): String? = withContext(Dispatchers.IO) {
        val url = config.buildUrl(iconName, cdnBaseUrl)
        val cacheKey = config.getCacheKey(iconName)

        // Check cache first
        if (cacheEnabled) {
            val cachedContent = getCachedSvg(cacheKey)
            if (cachedContent != null) {
                return@withContext cachedContent
            }
        }

        // Download from CDN
        try {
            log("Downloading SVG from $url")

            // Validate HTTPS is used
            if (!url.startsWith("https://")) {
                throw IllegalStateException("Only HTTPS URLs are allowed for security. Got: $url")
            }

            val response = httpClient.get(url)

            if (response.status.isSuccess()) {
                // Validate content type
                val contentType = response.contentType()
                if (contentType?.match(ContentType.Text.Xml) != true &&
                    contentType?.match(ContentType.Image.SVG) != true) {
                    throw IllegalStateException("Invalid content type: $contentType for URL: $url")
                }

                // Validate content size to prevent DoS
                val contentLength = response.contentLength()
                if (contentLength != null && contentLength > MAX_SVG_SIZE) {
                    throw IllegalStateException("SVG too large: $contentLength bytes (max: $MAX_SVG_SIZE) from URL: $url")
                }

                val svgContent = if (contentLength == null) {
                    // Stream read with a limit if content length is unknown
                    val channel = response.body<ByteReadChannel>()
                    val packet = channel.readRemaining(MAX_SVG_SIZE.toLong() + 1)
                    try {
                        if (packet.remaining > MAX_SVG_SIZE || !channel.isClosedForRead) {
                            throw IllegalStateException("SVG response exceeds max size of $MAX_SVG_SIZE bytes from URL: $url")
                        }
                        packet.readText()
                    } finally {
                        packet.release()
                    }
                } else {
                    response.bodyAsText()
                }

                // Validate basic SVG structure
                if (!svgContent.contains("<svg") || !svgContent.contains("</svg>")) {
                    throw IllegalStateException("Invalid SVG structure (missing svg tags) from URL: $url")
                }

                // Cache the validated content
                if (cacheEnabled && svgContent.isNotBlank()) {
                    cacheSvg(cacheKey, svgContent, url)
                }

                return@withContext svgContent
            } else {
                log("Failed to download from $url: HTTP ${response.status.value} ${response.status.description}")
                return@withContext null
            }
        } catch (e: Exception) {
            log("Error downloading SVG from $url: ${e.message}")
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
                    val maxAge = CACHE_MAX_AGE_MS // 7 days
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
    
    /**
     * Cache SVG content with metadata for future use.
     *
     * @param cacheKey Unique identifier for this cached file
     * @param content SVG content to cache
     * @param url Source URL for tracking
     */
    private fun cacheSvg(cacheKey: String, content: String, url: String) {
        try {
            val cacheFile = cachePath / "$cacheKey.svg"
            val metaFile = cachePath / "$cacheKey.meta"

            cacheFile.writeText(content)
            metaFile.writeText("${System.currentTimeMillis()}\n$url\n${content.hashCode()}")
        } catch (e: Exception) {
            log("Failed to cache SVG for key $cacheKey: ${e.message}")
        }
    }
    
    fun cleanup() {
        httpClient.close()
    }
    
    /**
     * Check if an SVG is cached and valid
     */
    fun isCached(cacheKey: String): Boolean {
        if (!cacheEnabled) return false

        val cacheFile = cachePath / "$cacheKey.svg"
        val metaFile = cachePath / "$cacheKey.meta"

        if (cacheFile.exists() && metaFile.exists()) {
            try {
                val meta = metaFile.readLines()
                if (meta.size >= 2) {
                    val timestamp = meta[0].toLong()

                    // Check if cache is still valid (7 days)
                    val maxAge = 7 * 24 * 60 * 60 * 1000L // 7 days
                    return System.currentTimeMillis() - timestamp < maxAge
                }
            } catch (e: Exception) {
                // Cache corrupted
                return false
            }
        }

        return false
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