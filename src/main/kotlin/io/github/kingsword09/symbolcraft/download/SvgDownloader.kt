package io.github.kingsword09.symbolcraft.download

import io.github.kingsword09.symbolcraft.model.IconConfig
import io.github.kingsword09.symbolcraft.model.MaterialSymbolsConfig
import io.github.kingsword09.symbolcraft.model.SymbolFill
import io.github.kingsword09.symbolcraft.model.SymbolWeight
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
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
 * Downloads icon SVG files from CDN with local caching, validation, and retry logic.
 *
 * Supports multiple icon libraries through the IconConfig interface.
 *
 * Features:
 * - Multiple fallback URLs for Material Symbols (user custom + built-in CDNs)
 * - Content validation (type, size, structure)
 * - 7-day cache with metadata tracking
 * - HTTPS enforcement for security
 * - Automatic retry with exponential backoff
 *
 * @property cacheDirectory Directory path for storing cached SVG files
 * @property cacheEnabled Whether to enable caching (default: true)
 * @property maxRetries Maximum number of retry attempts per URL (default: 3)
 * @property retryDelayMs Initial delay between retries in milliseconds (default: 1000ms)
 * @property logger Optional logger for status messages, if not provided uses println
 */
class SvgDownloader(
    private val cacheDirectory: String,
    private val cacheEnabled: Boolean = true,
    private val maxRetries: Int = 3,
    private val retryDelayMs: Long = 1000L,
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
     * Download an SVG file for the given icon and configuration with automatic retry logic.
     *
     * This method:
     * 1. Checks the local cache first
     * 2. Downloads from CDN if not cached (with retries on failure)
     * 3. For MaterialSymbolsConfig: tries fallback URLs (user custom + built-in CDNs)
     * 4. For other configs: uses regular retry logic with exponential backoff
     * 5. Validates content type, size, and structure
     * 6. Caches valid content for future use
     *
     * Retry Strategy for MaterialSymbolsConfig:
     * - User-provided fallback URLs are tried first (each with maxRetries)
     * - Built-in CDN URLs are tried as fallbacks (each with maxRetries)
     * - Exponential backoff within each URL
     *
     * Retry Strategy for other configs:
     * - Exponential backoff: delay doubles after each retry
     * - Configurable max retries (default: 3)
     * - Configurable initial delay (default: 1000ms)
     *
     * @param iconName Name of the icon
     * @param config Icon library configuration
     * @return SVG content as string, or null if download fails after all retries
     */
    suspend fun downloadSvg(iconName: String, config: IconConfig): String? = withContext(Dispatchers.IO) {
        val cacheKey = config.getCacheKey(iconName)

        // Check cache first
        if (cacheEnabled) {
            val cachedContent = getCachedSvg(cacheKey)
            if (cachedContent != null) {
                return@withContext cachedContent
            }
        }

        // Handle MaterialSymbolsConfig with fallback URLs
        if (config is MaterialSymbolsConfig) {
            val fallbackUrls = config.getAllFallbackUrls()
            log("📦 MaterialSymbols: $iconName - Trying ${fallbackUrls.size} URL(s)")

            for ((urlIndex, urlTemplate) in fallbackUrls.withIndex()) {
                val weightValue = when {
                    (config.weight == SymbolWeight.REGULAR || config.weight == SymbolWeight.W400) && config.fill == SymbolFill.FILLED -> ""
                    (config.weight == SymbolWeight.REGULAR || config.weight == SymbolWeight.W400) -> "default"
                    else -> "wght${config.weight.value}"
                }
                val url = urlTemplate.replace("{name}", iconName)
                    .replace("{variant}", config.variant.pathName)
                    .replace("{weight}", weightValue)
                    .replace("{fill}", config.fill.shortName)
                    .replace("{grade}", config.grade.toString())
                    .replace("{optical_size}", config.opticalSize.toString())

                val urlLabel = if (urlIndex < config.customFallbackUrls.size) {
                    "Custom URL #${urlIndex + 1}"
                } else {
                    "Built-in CDN #${urlIndex - config.customFallbackUrls.size + 1}"
                }

                var lastException: Exception? = null
                repeat(maxRetries) { attemptNumber ->
                    try {
                        val svgContent = downloadSvgInternal(url, cacheKey)
                        if (svgContent != null) {
                            if (urlIndex > 0 || attemptNumber > 0) {
                                log("✅ Success with $urlLabel after ${attemptNumber + 1} attempt(s)")
                            }
                            return@withContext svgContent
                        }
                    } catch (e: Exception) {
                        lastException = e
                        val remainingRetries = maxRetries - attemptNumber - 1

                        if (remainingRetries > 0) {
                            val delayMs = retryDelayMs * (1 shl attemptNumber)
                            log("⚠️ $urlLabel attempt ${attemptNumber + 1} failed: ${e.message}")
                            log("   Retrying in ${delayMs}ms... ($remainingRetries retries remaining)")
                            delay(delayMs)
                        }
                    }
                }

                // This URL exhausted all retries, try next URL
                if (urlIndex < fallbackUrls.size - 1) {
                    log("⚠️ $urlLabel failed after $maxRetries attempts, trying next URL...")
                } else {
                    log("❌ All ${fallbackUrls.size} URL(s) exhausted for $iconName: ${lastException?.message}")
                }
            }

            return@withContext null
        }

        // Regular download for other icon configs
        val url = config.buildUrl(iconName)
        var lastException: Exception? = null
        repeat(maxRetries) { attemptNumber ->
            try {
                val svgContent = downloadSvgInternal(url, cacheKey)
                if (svgContent != null) {
                    if (attemptNumber > 0) {
                        log("✅ Successfully downloaded after ${attemptNumber + 1} attempt(s): $url")
                    }
                    return@withContext svgContent
                }
            } catch (e: Exception) {
                lastException = e
                val remainingRetries = maxRetries - attemptNumber - 1

                if (remainingRetries > 0) {
                    val delayMs = retryDelayMs * (1 shl attemptNumber)
                    log("⚠️ Attempt ${attemptNumber + 1} failed for $url: ${e.message}")
                    log("   Retrying in ${delayMs}ms... ($remainingRetries retries remaining)")
                    delay(delayMs)
                }
            }
        }

        log("Error downloading SVG from $url after $maxRetries attempts: ${lastException?.message}")
        return@withContext null
    }

    /**
     * Internal method to perform a single download attempt.
     */
    private suspend fun downloadSvgInternal(url: String, cacheKey: String): String? {
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

            return svgContent
        } else {
            throw IOException("Failed to download from $url: HTTP ${response.status.value} ${response.status.description}")
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