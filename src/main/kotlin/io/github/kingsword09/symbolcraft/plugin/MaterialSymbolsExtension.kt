package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.model.SymbolFill
import io.github.kingsword09.symbolcraft.model.SymbolVariant
import io.github.kingsword09.symbolcraft.model.SymbolStyle
import io.github.kingsword09.symbolcraft.model.SymbolWeight
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty

/**
 * DSL entry point exposed as `materialSymbols { ... }` in a consuming build script.
 *
 * The extension collects icon requests and paths that drive [GenerateSymbolsTask].
 *
 * @property cacheEnabled enables reuse of downloaded SVG assets between builds.
 * @property cacheDirectory directory that hosts cached SVG payloads (relative to `build/` by default).
 * @property outputDirectory Kotlin source folder where generated code will be written.
 * @property packageName root package used for generated Kotlin types.
 * @property generatePreview toggles Compose preview function generation for each icon.
 */
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

    /**
     * Registers a single Material Symbol by name and describes the variants to generate.
     *
     * @param name icon identifier as published by the Material Symbols catalog (e.g. `"home"`).
     * @param configure block that configures the weights, variants and fill combinations to emit.
     */
    fun symbol(name: String, configure: SymbolConfigBuilder.() -> Unit) {
        val builder = SymbolConfigBuilder()
        builder.configure()
        symbolsConfig[name] = builder.styles
    }

    /**
     * Convenience overload for configuring multiple icons with the same style block.
     *
     * @param names one or more Material Symbol identifiers.
     * @param configure block that is executed for each icon name.
     */
    fun symbols(vararg names: String, configure: SymbolConfigBuilder.() -> Unit) {
        names.forEach { name -> symbol(name, configure) }
    }

    /**
     * Returns an immutable snapshot of all icon requests declared via the DSL.
     */
    fun getSymbolsConfig(): Map<String, List<SymbolStyle>> = symbolsConfig.toMap()

    /**
     * Computes a deterministic hash for the current configuration.
     *
     * The hash is used to decide whether `generateMaterialSymbols` can reuse cached outputs.
     */
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

/**
 * Builder used inside the DSL to describe the variants for a given symbol.
 */
class SymbolConfigBuilder {
    val styles = mutableListOf<SymbolStyle>()

    // Primary method: using SymbolWeight enum
    /**
     * Adds a new style entry using the strongly typed [SymbolWeight] enum.
     *
     * @param weight Material weight classification (default W400).
     * @param variant visual variant (outlined, rounded, sharp).
     * @param fill fill mode (filled or unfilled).
     * @param grade parameter forwarded to Material Symbols for finer adjustments.
     * @param opticalSize optical size requested from the Material Symbols catalog.
     */
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
    /**
     * Adds a new style entry using a raw integer weight as published by Google (e.g. 400).
     */
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
    /**
     * Adds multiple weight variants for the same variant/fill combination.
     */
    fun weights(vararg weights: SymbolWeight, variant: SymbolVariant = SymbolVariant.OUTLINED, fill: SymbolFill = SymbolFill.UNFILLED) {
        weights.forEach { weight ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    // Overload: using Int weight values
    /**
     * Adds multiple weights expressed as integers while keeping the same variant/fill.
     */
    fun weights(vararg weights: Int, variant: SymbolVariant = SymbolVariant.OUTLINED, fill: SymbolFill = SymbolFill.UNFILLED) {
        weights.forEach { weight ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    // Common Material Design weight combinations
    /**
     * Adds the standard Material Design weight trio (400/500/700).
     */
    fun standardWeights(variant: SymbolVariant = SymbolVariant.OUTLINED, fill: SymbolFill = SymbolFill.UNFILLED) {
        weights(SymbolWeight.W400, SymbolWeight.W500, SymbolWeight.W700, variant = variant, fill = fill)
    }

    // All variants for a specific weight using SymbolWeight
    /**
     * Generates all visual variants (outlined, rounded, sharp) for the supplied weight.
     */
    fun allVariants(weight: SymbolWeight = SymbolWeight.W400, fill: SymbolFill = SymbolFill.UNFILLED) {
        SymbolVariant.entries.forEach { variant ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    // Overload: using Int weight value
    /**
     * Equivalent to [allVariants] but accepts the weight as an integer.
     */
    fun allVariants(weight: Int, fill: SymbolFill = SymbolFill.UNFILLED) {
        SymbolVariant.entries.forEach { variant ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    // Both filled and unfilled for a specific style using SymbolWeight
    /**
     * Enqueues both filled and unfilled versions for the supplied weight/variant pair.
     *
     * Example:
     * ```
     * bothFills(weight = SymbolWeight.W400, variant = SymbolVariant.ROUNDED)
     * // Generates rounded W400 icons for both fill modes
     * ```
     */
    fun bothFills(weight: SymbolWeight = SymbolWeight.W400, variant: SymbolVariant = SymbolVariant.OUTLINED) {
        style(weight = weight, variant = variant, fill = SymbolFill.UNFILLED)
        style(weight = weight, variant = variant, fill = SymbolFill.FILLED)
    }

    // Overload: using Int weight value
    /**
     * Adds filled and unfilled variants when the weight is provided as an integer.
     *
     * Example:
     * ```
     * bothFills(weight = 400, variant = SymbolVariant.ROUNDED)
     * // Generates rounded W400 icons for both fill modes
     * ```
     */
    fun bothFills(weight: Int, variant: SymbolVariant = SymbolVariant.OUTLINED) {
        style(weight = weight, variant = variant, fill = SymbolFill.UNFILLED)
        style(weight = weight, variant = variant, fill = SymbolFill.FILLED)
    }
}
