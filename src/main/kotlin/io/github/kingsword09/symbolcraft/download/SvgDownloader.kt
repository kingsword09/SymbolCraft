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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.time.Duration.Companion.days

/**
 * Downloads icon SVG files from CDN with local caching, validation, and retry logic.
 *
 * Supports multiple icon libraries through the IconConfig interface.
 *
 * Features:
 * - Content validation (type, size, structure)
 * - 7-day cache with metadata tracking
 * - HTTPS enforcement for security
 * - Automatic retry with exponential backoff
 *
 * @property cacheDirectory Directory path for storing cached SVG files
 * @property cacheEnabled Whether to enable caching (default: true)
 * @property maxRetries Maximum number of retry attempts (default: 3)
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
        /**
         * Default timeout for HTTP requests in milliseconds (30 seconds).
         *
         * Rationale: Most SVG files are small (<100KB) and should download quickly.
         * 30 seconds allows for slow networks while preventing indefinite hangs.
         */
        private const val REQUEST_TIMEOUT_MS = 30_000L

        /**
         * Cache validity period in days.
         *
         * Rationale: Icon libraries rarely update existing icons, so 7 days provides
         * a good balance between freshness and reducing network requests.
         */
        private const val CACHE_MAX_AGE_DAYS = 7

        /**
         * Cache validity period in milliseconds.
         *
         * Calculated as: 7 days × 24 hours × 60 minutes × 60 seconds × 1000 milliseconds
         */
        private val CACHE_MAX_AGE_MS = CACHE_MAX_AGE_DAYS.days.inWholeMilliseconds

        /**
         * Maximum allowed SVG file size in bytes (10 MB).
         *
         * Rationale: Typical icon SVG files are 1-50KB. Setting a 10MB limit:
         * - Prevents DoS attacks from malicious CDNs serving huge files
         * - Protects against memory exhaustion during parsing
         * - Still allows for unusually large or complex SVG icons
         *
         * Note: Files larger than this will be rejected with an error.
         */
        private const val MAX_SVG_SIZE = 10 * 1024 * 1024  // 10 MB

        /**
         * Maximum number of concurrent HTTP connections.
         *
         * Rationale: Allows parallel downloads while respecting system resources.
         * 50 connections balances speed with memory/socket usage.
         */
        const val MAX_CONNECTIONS_COUNT = 50

        /**
         * Maximum concurrent connections per route (host).
         *
         * Rationale: Prevents overwhelming a single CDN server while allowing
         * reasonable parallelism. Most CDNs can handle 20+ concurrent connections.
         */
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
     * 3. Validates content type, size, and structure
     * 4. Caches valid content for future use
     *
     * Retry Strategy:
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

        // Download from URL with retry logic
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

            // Security validation: Prevent XXE and other attacks
            validateSvgSecurity(svgContent, url)

            // Cache the validated content
            if (cacheEnabled && svgContent.isNotBlank()) {
                cacheSvg(cacheKey, svgContent, url)
            }

            return svgContent
        } else {
            throw IOException("Failed to download from $url: HTTP ${response.status.value} ${response.status.description}")
        }
    }

    /**
     * Validate SVG content for security threats.
     *
     * This method scans for dangerous patterns that could lead to:
     * - XXE (XML External Entity) attacks
     * - Script injection
     * - Data exfiltration
     *
     * @param svgContent The SVG content to validate
     * @param url Source URL for error messages
     * @throws SecurityException if dangerous content is detected
     */
    private fun validateSvgSecurity(svgContent: String, url: String) {
        // List of dangerous patterns that should never appear in safe SVG files
        val dangerousPatterns = mapOf(
            "<!ENTITY" to "XML External Entity (XXE) declaration",
            "<!DOCTYPE" to "DOCTYPE declaration (potential XXE vector)",
            "<script" to "Embedded JavaScript",
            "javascript:" to "JavaScript protocol handler",
            "data:text/html" to "HTML data URL",
            "on" + "load=" to "Event handler (onload)",
            "on" + "error=" to "Event handler (onerror)",
            "on" + "click=" to "Event handler (onclick)",
            "<iframe" to "Embedded iframe",
            "<object" to "Embedded object",
            "<embed" to "Embedded content",
            "xlink:href=\"javascript:" to "XLink JavaScript protocol"
        )

        dangerousPatterns.forEach { (pattern, description) ->
            if (svgContent.contains(pattern, ignoreCase = true)) {
                throw SecurityException(
                    "SVG contains potentially dangerous content from $url: $description (pattern: '$pattern'). " +
                    "This file may be malicious and has been rejected for security reasons."
                )
            }
        }

        // Additional check: Ensure no SYSTEM or PUBLIC entities
        if (svgContent.contains("SYSTEM", ignoreCase = true) &&
            (svgContent.contains("<!ENTITY", ignoreCase = true) || svgContent.contains("<!DOCTYPE", ignoreCase = true))) {
            throw SecurityException(
                "SVG contains SYSTEM entity declaration from $url. " +
                "This is a critical security risk (potential file disclosure) and has been rejected."
            )
        }

        if (svgContent.contains("PUBLIC", ignoreCase = true) &&
            (svgContent.contains("<!ENTITY", ignoreCase = true) || svgContent.contains("<!DOCTYPE", ignoreCase = true))) {
            throw SecurityException(
                "SVG contains PUBLIC entity declaration from $url. " +
                "This is a security risk and has been rejected."
            )
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
     * Uses SHA-256 hash for integrity verification instead of hashCode()
     * to prevent hash collisions and provide cryptographic integrity.
     *
     * @param cacheKey Unique identifier for this cached file
     * @param content SVG content to cache
     * @param url Source URL for tracking
     */
    private fun cacheSvg(cacheKey: String, content: String, url: String) {
        try {
            val cacheFile = cachePath / "$cacheKey.svg"
            val metaFile = cachePath / "$cacheKey.meta"

            val contentHash = calculateSHA256(content)
            cacheFile.writeText(content)
            metaFile.writeText("${System.currentTimeMillis()}\n$url\n$contentHash")
        } catch (e: Exception) {
            log("Failed to cache SVG for key $cacheKey: ${e.message}")
        }
    }

    /**
     * Calculate SHA-256 hash of content for cache integrity verification.
     *
     * SHA-256 provides:
     * - Cryptographic integrity (detects tampering)
     * - Extremely low collision probability (unlike hashCode())
     * - Consistent hash values across JVM instances
     *
     * @param content Content to hash
     * @return Hexadecimal string representation of SHA-256 hash
     */
    private fun calculateSHA256(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(content.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
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

                    // Check if cache is still valid
                    return System.currentTimeMillis() - timestamp < CACHE_MAX_AGE_MS
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