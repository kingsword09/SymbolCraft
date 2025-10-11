package io.github.kingsword09.symbolcraft.model

import kotlinx.serialization.Serializable

@Serializable
data class SymbolStyle(
    val weight: SymbolWeight = SymbolWeight.REGULAR,
    val variant: SymbolVariant = SymbolVariant.OUTLINED,
    val fill: SymbolFill = SymbolFill.UNFILLED,
    val grade: Int = 0,  // Keep for future use
    val opticalSize: Int = 24  // Keep for future use
) {
    val signature: String
        get() = buildString {
            append("W").append(weight)
            append(variant.shortName)
            append(fill.shortName)
            if (grade != 0) append("G").append(grade)
        }

    /**
     * Generate CDN URL for downloading SVG from @material-symbols packages.
     *
     * Default format (esm.sh): https://esm.sh/@material-symbols/svg-400/outlined/face.svg
     * Custom CDN format: {cdnBaseUrl}/@material-symbols/svg-{weight}/{variant}/{name}{suffix}.svg
     *
     * @param iconName Name of the icon (e.g., "home", "search")
     * @param cdnBaseUrl Base URL for the CDN (default: "https://esm.sh")
     * @return Full URL to the SVG file
     */
    fun buildUrl(iconName: String, cdnBaseUrl: String = "https://esm.sh"): String {
        val suffix = if (fill == SymbolFill.FILLED) "-fill" else ""
        return "$cdnBaseUrl/@material-symbols/svg-${weight.value}/${variant.pathName}/$iconName$suffix.svg"
    }

    /**
     * Generate esm.sh URL for downloading SVG from @material-symbols packages.
     *
     * @deprecated Use [buildUrl] instead for better flexibility with CDN configuration
     * Example: https://esm.sh/@material-symbols/svg-400/outlined/face.svg
     */
    @Deprecated(
        message = "Use buildUrl(iconName, cdnBaseUrl) for configurable CDN support",
        replaceWith = ReplaceWith("buildUrl(iconName, \"https://esm.sh\")"),
        level = DeprecationLevel.WARNING
    )
    fun buildEsmUrl(iconName: String): String {
        return buildUrl(iconName, "https://esm.sh")
    }

    /**
     * Generate cache key for this icon and style combination
     */
    fun getCacheKey(iconName: String): String {
        return "${iconName}_${weight.value}_${variant.pathName}_${fill.name.lowercase()}"
    }
}

@Serializable
enum class SymbolVariant(val shortName: String, val pathName: String) {
    OUTLINED("Outlined", "outlined"),
    ROUNDED("Rounded", "rounded"), 
    SHARP("Sharp", "sharp")
}

@Serializable
enum class SymbolFill(val shortName: String) {
    UNFILLED(""), 
    FILLED("Fill")
}

@Serializable
enum class SymbolWeight(val value: Int) {
    /**
     * weight = 100 - Thinnest stroke weight
     */
    W100(100),

    /**
     * weight = 200 - Extra light stroke weight
     */
    W200(200),

    /**
     * weight = 300 - Light stroke weight
     */
    W300(300),

    /**
     * weight = 400 - Regular/Normal stroke weight (default)
     */
    W400(400),

    /**
     * weight = 500 - Medium stroke weight
     */
    W500(500),

    /**
     * weight = 600 - Semi-bold stroke weight
     */
    W600(600),

    /**
     * weight = 700 - Bold stroke weight
     */
    W700(700);

    // Backward compatibility aliases
    companion object {
        val THIN = W100
        val EXTRA_LIGHT = W200
        val LIGHT = W300
        val REGULAR = W400
        val MEDIUM = W500
        val SEMI_BOLD = W600
        val BOLD = W700

        /**
         * Get SymbolWeight enum from numeric value
         */
        fun fromValue(value: Int): SymbolWeight {
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unsupported weight: $value. Supported weights: ${entries.map { it.value }}")
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}

object SymbolStyles {
    // Most commonly used styles with clear weight indication
    val W400 = SymbolStyle(weight = SymbolWeight.W400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
    val W500 = SymbolStyle(weight = SymbolWeight.W500, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
    val W700 = SymbolStyle(weight = SymbolWeight.W700, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)

    val W400Filled = SymbolStyle(weight = SymbolWeight.W400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)
    val W500Filled = SymbolStyle(weight = SymbolWeight.W500, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)

    val W400Rounded = SymbolStyle(weight = SymbolWeight.W400, variant = SymbolVariant.ROUNDED, fill = SymbolFill.UNFILLED)
    val W400Sharp = SymbolStyle(weight = SymbolWeight.W400, variant = SymbolVariant.SHARP, fill = SymbolFill.UNFILLED)

    // Backward compatibility aliases
    val Regular = W400
    val Medium = W500
    val Bold = W700
    val RegularFilled = W400Filled
    val MediumFilled = W500Filled
    val Rounded = W400Rounded
    val Sharp = W400Sharp
}

