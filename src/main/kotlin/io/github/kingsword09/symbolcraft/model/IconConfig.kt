package io.github.kingsword09.symbolcraft.model

import kotlinx.serialization.Serializable

/**
 * Base interface for all icon library configurations.
 *
 * Users can implement this interface to support custom icon libraries.
 *
 * Example:
 * ```kotlin
 * @Serializable
 * data class MyCustomIconConfig(
 *     val style: String = "default"
 * ) : IconConfig {
 *     override val libraryId = "my-custom-library"
 *
 *     override fun buildUrl(iconName: String, cdnBaseUrl: String): String {
 *         return "$cdnBaseUrl/my-library/$iconName.svg"
 *     }
 *
 *     override fun getCacheKey(iconName: String): String {
 *         return "${iconName}_${libraryId}_${style}"
 *     }
 *
 *     override fun getSignature(): String = style
 * }
 * ```
 */
interface IconConfig {
    /**
     * Unique identifier for the icon library.
     * Must be unique across all icon libraries to avoid cache conflicts.
     *
     * Recommended format: "library-name" (e.g., "material-symbols", "font-awesome")
     */
    val libraryId: String

    /**
     * Build the CDN URL for downloading the icon SVG file.
     *
     * @param iconName Name of the icon (e.g., "home", "search")
     * @return Full URL to the SVG file
     */
    fun buildUrl(iconName: String): String

    /**
     * Generate a unique cache key for this icon and configuration combination.
     *
     * The cache key MUST be unique across all icons and configurations to avoid cache conflicts.
     * It's recommended to include: iconName, libraryId, and all style parameters.
     *
     * @param iconName Name of the icon
     * @return Unique cache key string
     */
    fun getCacheKey(iconName: String): String

    /**
     * Generate a signature string used for file naming.
     * This appears in the generated Kotlin file names.
     *
     * Should be short and descriptive (e.g., "W400Outlined", "Fill", "24px")
     *
     * @return Signature string for file naming
     */
    fun getSignature(): String
}

/**
 * Configuration for Material Symbols icon library.
 *
 * Uses Google Fonts CDN as the source for Material Symbols icons.
 * For custom CDN or backup URLs, use `externalIcon` instead.
 *
 * @property weight Stroke weight (100-700)
 * @property variant Visual variant (outlined, rounded, sharp)
 * @property fill Fill mode (filled or unfilled)
 * @property grade Fine-tuning parameter for weight adjustment
 * @property opticalSize Optical size optimization parameter
 */
@Serializable
data class MaterialSymbolsConfig(
    val weight: SymbolWeight = SymbolWeight.W400,
    val variant: SymbolVariant = SymbolVariant.OUTLINED,
    val fill: SymbolFill = SymbolFill.UNFILLED,
    val grade: Int = 0,
    val opticalSize: Int = 24
) : IconConfig {
    override val libraryId = "material-symbols"

    override fun buildUrl(iconName: String): String {
        val weightValue = when {
            (weight == SymbolWeight.REGULAR || weight == SymbolWeight.W400) && fill == SymbolFill.FILLED -> ""
            (weight == SymbolWeight.REGULAR || weight == SymbolWeight.W400) -> "default"
            else -> "wght${weight.value}"
        }

        // Google Fonts official CDN
        return "https://fonts.gstatic.com/s/i/short-term/release/materialsymbols${variant.pathName}/$iconName/$weightValue${fill.shortName}/${opticalSize}px.svg"
    }

    override fun getCacheKey(iconName: String): String {
        return "${iconName}_${libraryId}_${weight.value}_${variant.pathName}_${fill.name.lowercase()}"
    }

    override fun getSignature(): String = buildString {
        append("W").append(weight.value)
        append(variant.shortName)
        append(fill.shortName)
        if (grade != 0) append("G").append(grade)
    }
}

/**
 * Configuration for external icon libraries with URL template support.
 *
 * Supports flexible URL patterns with placeholder replacement.
 *
 * Available placeholders:
 * - {name}: Icon name
 * - {key}: Any custom style parameter key
 *
 * Example:
 * ```kotlin
 * ExternalIconConfig(
 *     libraryName = "bootstrap-icons",
 *     urlTemplate = "https://esm.sh/bootstrap-icons/{style}/{name}.svg",
 *     styleParams = mapOf("style" to "fill")
 * )
 * ```
 *
 * @property libraryName Name of the external library (will be prefixed with "external-")
 * @property urlTemplate URL pattern with placeholders (must be full URL)
 * @property styleParams Map of style parameters for placeholder replacement
 */
@Serializable
data class ExternalIconConfig(
    val libraryName: String,
    val urlTemplate: String,
    val styleParams: Map<String, String> = emptyMap()
) : IconConfig {
    override val libraryId = "external-$libraryName"

    override fun buildUrl(iconName: String): String {
        var url = urlTemplate.replace("{name}", iconName)

        styleParams.forEach { (key, value) ->
            url = url.replace("{$key}", value)
        }

        return url
    }

    override fun getCacheKey(iconName: String): String {
        val paramsString = styleParams.entries
            .sortedBy { it.key }
            .joinToString("_") { "${it.key}=${it.value}" }
        return "${iconName}_${libraryId}_${paramsString.hashCode()}"
    }

    override fun getSignature(): String {
        return styleParams.values.joinToString("") { it.replaceFirstChar { c -> c.titlecase() } }
            .ifEmpty { libraryName.replaceFirstChar { it.titlecase() } }
    }
}

// Material Symbols enums

@Serializable
enum class SymbolVariant(val shortName: String, val pathName: String) {
    OUTLINED("Outlined", "outlined"),
    ROUNDED("Rounded", "rounded"),
    SHARP("Sharp", "sharp")
}

@Serializable
enum class SymbolFill(val shortName: String) {
    UNFILLED(""),
    FILLED("fill1")
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

    companion object {
        // Convenience aliases
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

    override fun toString(): String = value.toString()
}

// Predefined Material Symbols configurations

object MaterialSymbolsPresets {
    // Common weight variants
    val W400 = MaterialSymbolsConfig(weight = SymbolWeight.W400)
    val W500 = MaterialSymbolsConfig(weight = SymbolWeight.W500)
    val W700 = MaterialSymbolsConfig(weight = SymbolWeight.W700)

    // Filled variants
    val W400Filled = MaterialSymbolsConfig(weight = SymbolWeight.W400, fill = SymbolFill.FILLED)
    val W500Filled = MaterialSymbolsConfig(weight = SymbolWeight.W500, fill = SymbolFill.FILLED)

    // Style variants
    val W400Rounded = MaterialSymbolsConfig(weight = SymbolWeight.W400, variant = SymbolVariant.ROUNDED)
    val W400Sharp = MaterialSymbolsConfig(weight = SymbolWeight.W400, variant = SymbolVariant.SHARP)

    // Aliases
    val Regular = W400
    val Medium = W500
    val Bold = W700
    val RegularFilled = W400Filled
    val MediumFilled = W500Filled
    val Rounded = W400Rounded
    val Sharp = W400Sharp
}
