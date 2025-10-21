package io.github.kingsword09.symbolcraft.tasks.internal

import io.github.kingsword09.symbolcraft.model.IconConfig

/**
 * Aggregated metrics from the parallel download stage. Exposed so higher-level components (tasks,
 * tests) can make assertions about success/failure ratios without digging into logs.
 */
internal data class DownloadStats(
    val totalCount: Int,
    val successCount: Int,
    val failedCount: Int,
    val cachedCount: Int,
    val results: List<DownloadResult>,
)

/**
 * Result wrapper representing either a successful or failed SVG download.
 *
 * Keeping the full [IconConfig] around allows callers to link failures back to the configuration
 * DSL entry that triggered the request.
 */
internal sealed class DownloadResult {
    data class Success(val iconName: String, val config: IconConfig, val fileName: String) :
        DownloadResult()

    data class Failed(val iconName: String, val config: IconConfig, val error: String) :
        DownloadResult()
}
