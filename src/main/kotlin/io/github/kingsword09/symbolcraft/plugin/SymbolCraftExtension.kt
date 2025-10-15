package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.model.*
import org.gradle.api.provider.Property

/**
 * DSL entry point exposed as `symbolCraft { ... }` in a consuming build script.
 *
 * The extension collects icon requests from multiple icon libraries and paths that drive [io.github.kingsword09.symbolcraft.tasks.GenerateSymbolsTask].
 *
 * @property cacheEnabled enables reuse of downloaded SVG assets between builds.
 * @property cacheDirectory directory that hosts cached SVG payloads (relative to `build/` by default).
 * @property outputDirectory Kotlin source folder where generated code will be written.
 * @property packageName root package used for generated Kotlin types.
 * @property generatePreview toggles Compose preview function generation for each icon.
 * @property cdnBaseUrl base URL for the CDN serving icons (default: https://esm.sh).
 */
abstract class SymbolCraftExtension {
    abstract val cacheEnabled: Property<Boolean>
    abstract val cacheDirectory: Property<String>
    abstract val outputDirectory: Property<String>
    abstract val packageName: Property<String>
    abstract val generatePreview: Property<Boolean>
    abstract val cdnBaseUrl: Property<String>

    private val iconsConfig = mutableMapOf<String, MutableList<IconConfig>>()

    init {
        cacheEnabled.convention(true)
        cacheDirectory.convention("symbolcraft-cache")
        outputDirectory.convention("src/main/kotlin")
        packageName.convention("io.github.kingsword09.symbolcraft.symbols")
        generatePreview.convention(false)
        cdnBaseUrl.convention("https://esm.sh")
    }

    /**
     * Generic method to add an icon with any IconConfig implementation.
     *
     * This method supports both built-in and user-defined icon libraries.
     *
     * Example:
     * ```kotlin
     * iconConfig("home", MaterialSymbolsConfig(weight = SymbolWeight.W400))
     * iconConfig("custom", MyCustomIconConfig(style = "filled"))
     * ```
     *
     * @param name Icon name
     * @param config IconConfig implementation
     */
    fun iconConfig(name: String, config: IconConfig) {
        iconsConfig.getOrPut(name) { mutableListOf() }.add(config)
    }

    /**
     * Batch configure multiple icons with the same configuration.
     *
     * @param names Icon names
     * @param configFactory Factory function that creates IconConfig for each icon name
     */
    fun iconConfigs(vararg names: String, configFactory: (String) -> IconConfig) {
        names.forEach { name ->
            iconConfig(name, configFactory(name))
        }
    }

    /**
     * Configure Material Symbols icons with DSL builder.
     *
     * Example:
     * ```kotlin
     * materialSymbol("home") {
     *     weights(400, 500, 700)
     *     variant = SymbolVariant.OUTLINED
     * }
     * ```
     *
     * @param name Material Symbol icon name
     * @param configure Configuration block
     */
    fun materialSymbol(name: String, configure: MaterialSymbolsBuilder.() -> Unit) {
        val builder = MaterialSymbolsBuilder()
        builder.configure()
        builder.configs.forEach { config ->
            iconConfig(name, config)
        }
    }

    /**
     * Convenience overload for configuring multiple Material Symbols with the same style block.
     *
     * @param names Material Symbol icon names
     * @param configure Configuration block applied to each icon
     */
    fun materialSymbols(vararg names: String, configure: MaterialSymbolsBuilder.() -> Unit) {
        names.forEach { name -> materialSymbol(name, configure) }
    }

    /**
     * Configure external icon from other icon libraries with URL template.
     *
     * The URL template supports the following placeholders:
     * - `{cdn}`: Replaced with cdnBaseUrl (default: "https://esm.sh")
     * - `{name}`: Replaced with the icon name
     * - `{key}`: Replaced with custom style parameter values
     *
     * Examples:
     * ```kotlin
     * // Using {cdn} placeholder (will use configured CDN URL)
     * externalIcon("bell", libraryName = "bootstrap-icons") {
     *     urlTemplate = "{cdn}/bootstrap-icons/fill/{name}.svg"
     * }
     *
     * // With custom style parameters
     * externalIcon("home", libraryName = "heroicons") {
     *     urlTemplate = "{cdn}/heroicons/{size}/{name}.svg"
     *     styleParam("size", "24")
     * }
     *
     * // Multi-value style parameters for variants
     * externalIcon("home", libraryName = "official") {
     *     urlTemplate = "https://example.com/{name}_{fill}_24px.svg"
     *     styleParam("fill") {
     *         values("", "fill1")  // unfilled, filled variants
     *     }
     * }
     *
     * // Direct URL without {cdn} placeholder
     * externalIcon("my-icon", libraryName = "mylib") {
     *     urlTemplate = "https://my-cdn.com/icons/{name}.svg"
     * }
     * ```
     *
     * @param name Icon name (replaces {name} in URL template)
     * @param libraryName Library identifier (used for cache isolation)
     * @param configure Configuration block
     */
    fun externalIcon(name: String, libraryName: String, configure: ExternalIconBuilder.() -> Unit) {
        val builder = ExternalIconBuilder(libraryName)
        builder.configure()
        val configs = builder.build()
        configs.forEach { config ->
            iconConfig(name, config)
        }
    }

    /**
     * Convenience overload for configuring multiple external icons from the same library.
     *
     * All icons will share the same URL template and style parameters.
     *
     * Examples:
     * ```kotlin
     * // Multiple icons from Bootstrap Icons
     * externalIcons("bell", "house", "person", libraryName = "bootstrap-icons") {
     *     urlTemplate = "{cdn}/bootstrap-icons/fill/{name}.svg"
     * }
     *
     * // Multiple icons with style parameters
     * externalIcons("home", "search", "user", libraryName = "heroicons") {
     *     urlTemplate = "{cdn}/heroicons/{size}/{name}.svg"
     *     styleParam("size", "24")
     * }
     *
     * // Multi-value style parameters for variants
     * externalIcons("home", "search", "settings", libraryName = "official") {
     *     urlTemplate = "https://example.com/{name}_{fill}_24px.svg"
     *     styleParam("fill") {
     *         values("", "fill1")  // unfilled, filled variants
     *     }
     * }
     * ```
     *
     * @param names Icon names to configure
     * @param libraryName Library identifier shared by all icons
     * @param configure Configuration block applied to each icon
     */
    fun externalIcons(vararg names: String, libraryName: String, configure: ExternalIconBuilder.() -> Unit) {
        names.forEach { name -> externalIcon(name, libraryName, configure) }
    }

    /**
     * Returns an immutable snapshot of all icon requests declared via the DSL.
     */
    fun getIconsConfig(): Map<String, List<IconConfig>> = iconsConfig.toMap()

    /**
     * Computes a deterministic hash for the current configuration.
     *
     * The hash is used to decide whether `generateSymbolCraftIcons` can reuse cached outputs.
     */
    fun getConfigHash(): String {
        val configString = buildString {
            append("version:2.0|")

            append("icons:")
            iconsConfig.toSortedMap().forEach { (name, configs) ->
                append("$name-[")
                configs.sortedBy { "${it.libraryId}-${it.getSignature()}" }.forEach { config ->
                    append("${config.libraryId}:${config.getSignature()},")
                }
                append("]")
            }
            append("|package:").append(packageName.orNull)
            append("|outputDir:").append(outputDirectory.orNull)
            append("|preview:").append(generatePreview.orNull)
            append("|cdnBaseUrl:").append(cdnBaseUrl.orNull)
        }
        return configString.hashCode().toString()
    }
}

/**
 * Builder for Material Symbols configuration.
 */
class MaterialSymbolsBuilder {
    val configs = mutableListOf<MaterialSymbolsConfig>()

    /**
     * Add a single style configuration using SymbolWeight enum.
     */
    fun style(
        weight: SymbolWeight = SymbolWeight.W400,
        variant: SymbolVariant = SymbolVariant.OUTLINED,
        fill: SymbolFill = SymbolFill.UNFILLED,
        grade: Int = 0,
        opticalSize: Int = 24
    ) {
        configs.add(MaterialSymbolsConfig(weight, variant, fill, grade, opticalSize))
    }

    /**
     * Add a single style configuration using integer weight value.
     */
    fun style(
        weight: Int,
        variant: SymbolVariant = SymbolVariant.OUTLINED,
        fill: SymbolFill = SymbolFill.UNFILLED,
        grade: Int = 0,
        opticalSize: Int = 24
    ) {
        val symbolWeight = SymbolWeight.fromValue(weight)
        configs.add(MaterialSymbolsConfig(symbolWeight, variant, fill, grade, opticalSize))
    }

    /**
     * Add multiple weight variants for the same variant/fill combination.
     */
    fun weights(
        vararg weights: SymbolWeight,
        variant: SymbolVariant = SymbolVariant.OUTLINED,
        fill: SymbolFill = SymbolFill.UNFILLED
    ) {
        weights.forEach { weight ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    /**
     * Add multiple weights expressed as integers.
     */
    fun weights(
        vararg weights: Int,
        variant: SymbolVariant = SymbolVariant.OUTLINED,
        fill: SymbolFill = SymbolFill.UNFILLED
    ) {
        weights.forEach { weight ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    /**
     * Add standard Material Design weight trio (400/500/700).
     */
    fun standardWeights(
        variant: SymbolVariant = SymbolVariant.OUTLINED,
        fill: SymbolFill = SymbolFill.UNFILLED
    ) {
        weights(SymbolWeight.W400, SymbolWeight.W500, SymbolWeight.W700, variant = variant, fill = fill)
    }

    /**
     * Generate all visual variants (outlined, rounded, sharp) for the supplied weight.
     */
    fun allVariants(
        weight: SymbolWeight = SymbolWeight.W400,
        fill: SymbolFill = SymbolFill.UNFILLED
    ) {
        SymbolVariant.entries.forEach { variant ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    /**
     * Generate all visual variants using integer weight value.
     */
    fun allVariants(weight: Int, fill: SymbolFill = SymbolFill.UNFILLED) {
        SymbolVariant.entries.forEach { variant ->
            style(weight = weight, variant = variant, fill = fill)
        }
    }

    /**
     * Add both filled and unfilled versions for the supplied weight/variant pair.
     */
    fun bothFills(
        weight: SymbolWeight = SymbolWeight.W400,
        variant: SymbolVariant = SymbolVariant.OUTLINED
    ) {
        style(weight = weight, variant = variant, fill = SymbolFill.UNFILLED)
        style(weight = weight, variant = variant, fill = SymbolFill.FILLED)
    }

    /**
     * Add both filled and unfilled versions using integer weight value.
     */
    fun bothFills(weight: Int, variant: SymbolVariant = SymbolVariant.OUTLINED) {
        style(weight = weight, variant = variant, fill = SymbolFill.UNFILLED)
        style(weight = weight, variant = variant, fill = SymbolFill.FILLED)
    }
}

/**
 * Builder for external icon configuration with support for multi-value style parameters.
 */
class ExternalIconBuilder(private val libraryName: String) {
    var urlTemplate: String = ""
    private val singleValueParams = mutableMapOf<String, String>()
    private val multiValueParams = mutableMapOf<String, List<String>>()

    /**
     * Add a single-value style parameter for URL template replacement.
     *
     * @param key Parameter name (used as {key} in template)
     * @param value Parameter value
     */
    fun styleParam(key: String, value: String) {
        singleValueParams[key] = value
    }

    /**
     * Add a multi-value style parameter with builder syntax.
     *
     * Example:
     * ```kotlin
     * styleParam("fill") {
     *     values("", "fill1")  // unfilled, filled variants
     * }
     * ```
     *
     * @param key Parameter name (used as {key} in template)
     * @param configure Configuration block for defining multiple values
     */
    fun styleParam(key: String, configure: StyleParamBuilder.() -> Unit) {
        val builder = StyleParamBuilder()
        builder.configure()
        multiValueParams[key] = builder.valuesList
    }

    fun build(): List<ExternalIconConfig> {
        require(urlTemplate.isNotBlank()) {
            "urlTemplate must be specified for external icon"
        }

        // If no multi-value params, return single config (backward compatibility)
        if (multiValueParams.isEmpty()) {
            return listOf(ExternalIconConfig(libraryName, urlTemplate, singleValueParams.toMap()))
        }

        // Generate Cartesian product of all parameter combinations
        return generateCartesianProduct().map { paramCombination ->
            ExternalIconConfig(libraryName, urlTemplate, paramCombination)
        }
    }

    private fun generateCartesianProduct(): List<Map<String, String>> {
        // Combine single-value and multi-value parameters
        val allParams = mutableMapOf<String, List<String>>()

        // Add single-value params as single-item lists
        singleValueParams.forEach { (key, value) ->
            allParams[key] = listOf(value)
        }

        // Add multi-value params
        allParams.putAll(multiValueParams)

        // Generate Cartesian product
        return cartesianProduct(allParams)
    }

    private fun cartesianProduct(params: Map<String, List<String>>): List<Map<String, String>> {
        if (params.isEmpty()) return listOf(emptyMap())

        return params.entries.fold(listOf(emptyMap())) { acc, (key, values) ->
            acc.flatMap { map ->
                values.map { value ->
                    map + (key to value)
                }
            }
        }
    }
}

/**
 * Builder for multi-value style parameters.
 */
class StyleParamBuilder {
    internal val valuesList = mutableListOf<String>()

    /**
     * Define multiple values for this style parameter.
     *
     * Example:
     * ```kotlin
     * values("", "fill1")           // unfilled, filled
     * values("outline", "solid")    // outline, solid
     * values("24", "48")            // different sizes
     * ```
     *
     * @param values The parameter values
     */
    fun values(vararg values: String) {
        valuesList.addAll(values)
    }
}
