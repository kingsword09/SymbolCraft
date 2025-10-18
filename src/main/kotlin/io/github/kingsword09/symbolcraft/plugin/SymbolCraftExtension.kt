package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.converter.NamingConvention
import io.github.kingsword09.symbolcraft.model.*
import io.github.kingsword09.symbolcraft.plugin.samples.localIconsExcludeSample
import io.github.kingsword09.symbolcraft.plugin.samples.localIconsIncludeSample
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher

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
 * @property maxRetries maximum number of retry attempts for failed downloads (default: 3).
 * @property retryDelayMs initial delay between retries in milliseconds (default: 1000ms).
 */
abstract class SymbolCraftExtension {
    @get:Inject
    protected abstract val objects: ObjectFactory
    abstract val cacheEnabled: Property<Boolean>
    abstract val cacheDirectory: Property<String>
    abstract val outputDirectory: Property<String>
    abstract val packageName: Property<String>
    abstract val generatePreview: Property<Boolean>
    abstract val maxRetries: Property<Int>
    abstract val retryDelayMs: Property<Long>
    abstract val projectDirectory: Property<String>
    
    /**
     * Icon naming configuration。
     *
     * Use the [naming] method to configure naming transformation.
     */
    internal val namingConfig: NamingConfig by lazy {
        objects.newInstance(NamingConfig::class.java)
    }

    private val iconsConfig = mutableMapOf<String, MutableList<IconConfig>>()

    init {
        cacheEnabled.convention(true)
        cacheDirectory.convention("symbolcraft-cache")
        outputDirectory.convention("src/main/kotlin")
        packageName.convention("io.github.kingsword09.symbolcraft.symbols")
        generatePreview.convention(false)
        maxRetries.convention(3)
        retryDelayMs.convention(1000L)
        projectDirectory.convention("")
    }

    /**
     * Configure icon naming transformation.
     *
     * Example:
     * ```kotlin
     * naming {
     *     pascalCase(suffix = "Icon")  // Preset
     *     // Or customize:
     *     namingConvention.set(NamingConvention.PASCAL_CASE)
     *     suffix.set("Icon")
     *     removePrefix.set("ic_")
     * }
     * ```
     *
     * @param action Configuration action for [NamingConfig]
     */
    fun naming(action: Action<NamingConfig>) {
        action.execute(namingConfig)
    }

    internal fun namingConfigSignature(): String = namingConfig.snapshotSignature()

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
     * - `{name}`: Replaced with the icon name
     * - `{key}`: Replaced with custom style parameter values
     *
     * Examples:
     * ```kotlin
     * // Simple icon URL
     * externalIcon("bell", libraryName = "bootstrap-icons") {
     *     urlTemplate = "https://esm.sh/bootstrap-icons/fill/{name}.svg"
     * }
     *
     * // With custom style parameters
     * externalIcon("home", libraryName = "heroicons") {
     *     urlTemplate = "https://cdn.jsdelivr.net/npm/heroicons/{size}/{name}.svg"
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
     * // Custom CDN
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
     *     urlTemplate = "https://esm.sh/bootstrap-icons/fill/{name}.svg"
     * }
     *
     * // Multiple icons with style parameters
     * externalIcons("home", "search", "user", libraryName = "heroicons") {
     *     urlTemplate = "https://cdn.jsdelivr.net/npm/heroicons/{size}/{name}.svg"
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
     * Configure local SVG icons discovered from the file system.
     *
     * Example:
     * ```kotlin
     * localIcons(libraryName = "brand") {
     *     directory = "src/commonMain/resources/icons"
     *     include("brand/" + "**" + "/icon.svg")
     *     exclude("legacy/" + "**")
     * }
     * ```
     *
     * When no include pattern is specified, all SVG files under the directory are discovered recursively.
     * Paths can be absolute or relative to the project directory. Icon names are derived from the relative file path and
     * passed through the standard naming transformers.
     *
     * @param libraryName Logical grouping displayed in the generated source package (default: `local`)
     * @param configure Configuration block describing the local icon search
     */
    fun localIcons(libraryName: String = "local", configure: LocalIconsBuilder.() -> Unit) {
        val projectDir = projectDirectory.orNull
            ?: throw IllegalStateException("Project directory is not set on SymbolCraftExtension")

        validateLocalLibraryName(libraryName)

        val builder = LocalIconsBuilder(projectDir)
        builder.configure()
        val localConfigs = builder.build(libraryName)
        localConfigs.forEach { (iconName, config) ->
            iconConfig(iconName, config)
        }
    }

    private fun validateLocalLibraryName(libraryName: String) {
        require(libraryName.matches(Regex("[a-zA-Z0-9_-]+"))) {
            "Library name for local icons can only contain alphanumeric characters, hyphens, and underscores. Got: $libraryName"
        }
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
            append("|namingConfig:").append(namingConfig.snapshotSignature())
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
 * Builder for configuring local SVG icons.
 */
class LocalIconsBuilder internal constructor(
    private val projectDir: String
) {
    /**
     * Root directory used for scanning SVG files. Must point to an existing directory.
     * Accepts absolute paths or paths relative to the Gradle project directory.
     */
    var directory: String? = null

    private val includePatterns = mutableListOf<String>()
    private val excludePatterns = mutableListOf<String>()

    /**
     * Add include glob patterns. When none are supplied, every SVG under the directory is included.
     *
     * @sample io.github.kingsword09.symbolcraft.plugin.localIconsIncludeSample
     */
    fun include(vararg patterns: String) {
        patterns.filter { it.isNotBlank() }.forEach { pattern ->
            includePatterns.add(pattern)
            if (pattern.contains("**/")) {
                includePatterns.add(pattern.replace("**/", ""))
            }
        }
    }

    /**
     * Add exclude glob patterns that will be filtered after includes.
     *
     * @sample io.github.kingsword09.symbolcraft.plugin.localIconsExcludeSample
     */
    fun exclude(vararg patterns: String) {
        excludePatterns.addAll(patterns.filter { it.isNotBlank() })
    }

    internal fun build(libraryName: String): Map<String, LocalIconConfig> {
        val dirValue = directory?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("directory must be specified for localIcons")

        val baseDir = resolveAgainstProject(dirValue).canonicalFile

        require(baseDir.exists()) {
            "Local icons directory does not exist: ${baseDir.absolutePath}"
        }
        require(baseDir.isDirectory) {
            "Local icons path must be a directory: ${baseDir.absolutePath}"
        }

        val includes = if (includePatterns.isEmpty()) listOf("**/*.svg", "*.svg") else includePatterns
        val includeMatchers = includes.map { compileGlob(it) }
        val excludeMatchers = excludePatterns.map { compileGlob(it) }

        val iconMap = linkedMapOf<String, LocalIconConfig>()

        baseDir.walkTopDown()
            .filter { it.isFile && it.extension.equals("svg", ignoreCase = true) }
            .forEach { file ->
                val relativePath = baseDir.toPath().relativize(file.toPath())
                if (!matches(relativePath, includeMatchers)) return@forEach
                if (matches(relativePath, excludeMatchers)) return@forEach

                val relativeNormalized = relativePath.toString().replace(File.separatorChar, '/')
                val iconNameBase = buildIconName(relativeNormalized)
                val iconName = ensureUniqueName(iconNameBase, iconMap.keys)

                val relativeWithoutExt = stripSvgExtension(relativeNormalized)

                iconMap[iconName] = LocalIconConfig(
                    libraryName = libraryName,
                    absolutePath = file.absolutePath,
                    relativePath = relativeWithoutExt
                )
            }

        if (iconMap.isEmpty()) {
            throw IllegalStateException(
                "No SVG icons found in ${baseDir.absolutePath} for includes $includes and excludes $excludePatterns"
            )
        }

        return iconMap
    }

    private fun compileGlob(pattern: String): PathMatcher {
        val normalized = pattern.trim().ifBlank { "**/*.svg" }
        val systemPattern = normalized.replace("/", File.separator)
        return FileSystems.getDefault().getPathMatcher("glob:$systemPattern")
    }

    private fun matches(relativePath: Path, matchers: List<PathMatcher>): Boolean {
        if (matchers.isEmpty()) return false
        return matchers.any { it.matches(relativePath) }
    }

    private fun buildIconName(relativePath: String): String {
        val withoutExt = stripSvgExtension(relativePath)
        val candidate = withoutExt.replace('/', '_').replace('-', '_')
        val sanitized = sanitizeLocalName(candidate)
        return sanitized.ifBlank { "icon" }
    }

    private fun ensureUniqueName(baseName: String, existing: Set<String>): String {
        if (baseName !in existing) return baseName

        var index = 2
        var candidate: String
        do {
            candidate = "${baseName}_${index}"
            index++
        } while (candidate in existing)
        return candidate
    }

    private fun resolveAgainstProject(path: String): File {
        val file = File(path)
        return if (file.isAbsolute) file else File(projectDir, path)
    }

    private fun stripSvgExtension(path: String): String {
        return if (path.endsWith(".svg", ignoreCase = true)) {
            path.substring(0, path.length - 4)
        } else {
            path
        }
    }

    private fun sanitizeLocalName(input: String): String {
        return input
            .replace("/", "_")
            .replace("\\", "_")
            .replace(Regex("[^a-zA-Z0-9_-]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')
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
