package io.github.kingsword09.symbolcraft.model

import kotlinx.serialization.Serializable

@Serializable
data class SymbolStyle(
    val weight: Int = 400,
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
     * Generate esm.sh URL for downloading SVG from @material-symbols packages
     * Example: https://esm.sh/@material-symbols/svg-700/outlined/face.svg
     */
    fun buildEsmUrl(iconName: String): String {
        val suffix = if (fill == SymbolFill.FILLED) "-fill" else ""
        return "https://esm.sh/@material-symbols/svg-$weight/${variant.pathName}/$iconName$suffix.svg"
    }
    
    /**
     * Generate cache key for this icon and style combination
     */
    fun getCacheKey(iconName: String): String {
        return "${iconName}_${weight}_${variant.pathName}_${fill.name.lowercase()}"
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

object SymbolStyles {
    // Outlined styles
    val Light = SymbolStyle(weight = 300, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
    val Regular = SymbolStyle(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
    val Medium = SymbolStyle(weight = 500, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
    val Bold = SymbolStyle(weight = 700, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
    
    // Filled styles
    val LightFilled = SymbolStyle(weight = 300, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)
    val RegularFilled = SymbolStyle(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)
    val MediumFilled = SymbolStyle(weight = 500, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)
    val BoldFilled = SymbolStyle(weight = 700, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)
    
    // Rounded styles
    val RoundedRegular = SymbolStyle(weight = 400, variant = SymbolVariant.ROUNDED, fill = SymbolFill.UNFILLED)
    val RoundedFilled = SymbolStyle(weight = 400, variant = SymbolVariant.ROUNDED, fill = SymbolFill.FILLED)
    
    // Sharp styles
    val SharpRegular = SymbolStyle(weight = 400, variant = SymbolVariant.SHARP, fill = SymbolFill.UNFILLED)
    val SharpFilled = SymbolStyle(weight = 400, variant = SymbolVariant.SHARP, fill = SymbolFill.FILLED)
}

