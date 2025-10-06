package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.model.SymbolFill
import io.github.kingsword09.symbolcraft.model.SymbolVariant
import io.github.kingsword09.symbolcraft.model.SymbolStyle
import io.github.kingsword09.symbolcraft.model.SymbolWeight
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty

abstract class MaterialSymbolsExtension {
    abstract val cacheEnabled: Property<Boolean>
    abstract val cacheDirectory: Property<String>
    abstract val outputDirectory: Property<String>
    abstract val packageName: Property<String>
    // Preview generation configuration
    abstract val generatePreview: Property<Boolean>

    private val symbolsConfig = mutableMapOf<String, MutableList<SymbolStyle>>()

    init {
        cacheEnabled.convention(true)
        cacheDirectory.convention("material-symbols-cache")
        // Default to the main source set, inside the configured package
        // svg-to-compose will place files under this directory following the package path
        outputDirectory.convention("src/main/kotlin")
        packageName.convention("io.github.kingsword09.symbolcraft.symbols")
        generatePreview.convention(false)
    }

    fun symbol(name: String, configure: SymbolConfigBuilder.() -> Unit) {
        val builder = SymbolConfigBuilder()
        builder.configure()
        symbolsConfig[name] = builder.styles
    }

    fun symbols(vararg names: String, configure: SymbolConfigBuilder.() -> Unit) {
        names.forEach { name -> symbol(name, configure) }
    }

    fun getSymbolsConfig(): Map<String, List<SymbolStyle>> = symbolsConfig.toMap()

    fun getConfigHash(): String {
        // Create a more comprehensive hash that includes all configuration
        val configString = buildString {
            // Add version identifier to invalidate cache when we change the model
            append("version:1.1|")

            append("symbols:")
            symbolsConfig.toSortedMap().forEach { (name, styles) ->
                append("$name-[")
                styles.sortedBy { "${it.weight.value}-${it.variant}-${it.fill}" }.forEach { style ->
                    append("${style.weight.value}:${style.variant.pathName}:${style.fill.name.lowercase()},")
                }
                append("]")
            }
            append("|package:").append(packageName.orNull)
            append("|outputDir:").append(outputDirectory.orNull)
            append("|preview:").append(generatePreview.orNull)
        }
        return configString.hashCode().toString()
    }
}

class SymbolConfigBuilder {
    val styles = mutableListOf<SymbolStyle>()

    // Primary method: using SymbolWeight enum
    fun style(
        weight: SymbolWeight = SymbolWeight.W400,
        variant: SymbolVariant = SymbolVariant.OUTLINED,
        fill: SymbolFill = SymbolFill.UNFILLED,
        grade: Int = 0,
        opticalSize: Int = 24
    ) {
        styles.add(SymbolStyle(weight, variant, fill, grade, opticalSize))
    }

    // Overload method: using Int weight value (more intuitive)
    fun style(
        weight: Int,
        variant: SymbolVariant = SymbolVariant.OUTLINED,
        fill: SymbolFill = SymbolFill.UNFILLED,
        grade: Int = 0,
        opticalSize: Int = 24
    ) {
        val symbolWeight = SymbolWeight.fromValue(weight)
        styles.add(SymbolStyle(symbolWeight, variant, fill, grade, opticalSize))
    }

    // Convenient methods for adding multiple weight variants using SymbolWeight
    fun weights(vararg weights: SymbolWeight, variant: SymbolVariant = SymbolVariant.OUTLINED, fill: SymbolFill = SymbolFill.UNFILLED) {
        weights.forEach { weight ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    // Overload: using Int weight values
    fun weights(vararg weights: Int, variant: SymbolVariant = SymbolVariant.OUTLINED, fill: SymbolFill = SymbolFill.UNFILLED) {
        weights.forEach { weight ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    // Common Material Design weight combinations
    fun standardWeights(variant: SymbolVariant = SymbolVariant.OUTLINED, fill: SymbolFill = SymbolFill.UNFILLED) {
        weights(SymbolWeight.W400, SymbolWeight.W500, SymbolWeight.W700, variant = variant, fill = fill)
    }

    // All variants for a specific weight using SymbolWeight
    fun allVariants(weight: SymbolWeight = SymbolWeight.W400, fill: SymbolFill = SymbolFill.UNFILLED) {
        SymbolVariant.entries.forEach { variant ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    // Overload: using Int weight value
    fun allVariants(weight: Int, fill: SymbolFill = SymbolFill.UNFILLED) {
        SymbolVariant.entries.forEach { variant ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    // Both filled and unfilled for a specific style using SymbolWeight
    fun bothFills(weight: SymbolWeight = SymbolWeight.W400, variant: SymbolVariant = SymbolVariant.OUTLINED) {
        style(weight = weight, variant = variant, fill = SymbolFill.UNFILLED)
        style(weight = weight, variant = variant, fill = SymbolFill.FILLED)
    }

    // Overload: using Int weight value
    fun bothFills(weight: Int, variant: SymbolVariant = SymbolVariant.OUTLINED) {
        style(weight = weight, variant = variant, fill = SymbolFill.UNFILLED)
        style(weight = weight, variant = variant, fill = SymbolFill.FILLED)
    }
}
