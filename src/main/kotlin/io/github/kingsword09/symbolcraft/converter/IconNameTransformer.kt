package io.github.kingsword09.symbolcraft.converter

import java.io.Serializable

/**
 * Abstract base class for transforming icon file names into Kotlin class names.
 *
 * Different icon libraries may have different naming conventions.
 * This class allows users to implement custom naming transformers for their specific libraries.
 *
 * Implementations must be Serializable to support Gradle's configuration cache.
 *
 * Example:
 * ```kotlin
 * class MyTransformer : IconNameTransformer() {
 *     override fun transform(fileName: String): String {
 *         return fileName.uppercase() + "Icon"
 *     }
 * }
 * ```
 */
abstract class IconNameTransformer : Serializable {
    /**
     * Transform an icon file name into a valid Kotlin class name.
     *
     * @param fileName The file name (with or without .svg extension)
     * @return Transformed Kotlin class name
     */
    abstract fun transform(fileName: String): String

    /**
     * Provide a stable signature for this transformer.
     * This signature is used for Gradle's build cache.
     *
     * Default implementation uses the fully qualified class name,
     * which is stable across builds as long as the class definition doesn't change.
     *
     * Override this method if you need custom signature logic
     * (e.g., including constructor parameters in the signature).
     *
     * @return Stable signature string for build caching
     */
    open fun getSignature(): String = this::class.java.name

    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * Naming conventions for icon class names.
 */
enum class NamingConvention {
    /**
     * PascalCase: home-icon → HomeIcon
     */
    PASCAL_CASE,

    /**
     * camelCase: home-icon → homeIcon
     */
    CAMEL_CASE,

    /**
     * snake_case: home-icon → home_icon
     */
    SNAKE_CASE,

    /**
     * SCREAMING_SNAKE_CASE: home-icon → HOME_ICON
     */
    SCREAMING_SNAKE,

    /**
     * kebab-case: home_icon → home-icon
     */
    KEBAB_CASE,

    /**
     * lowercase: HomeIcon → homeicon
     */
    LOWER_CASE,

    /**
     * UPPERCASE: HomeIcon → HOMEICON
     */
    UPPER_CASE
}

/**
 * Naming transformer based on naming conventions.
 *
 * Supports common naming patterns with optional prefix/suffix.
 *
 * Example:
 * ```kotlin
 * val transformer = ConventionNameTransformer(
 *     convention = NamingConvention.PASCAL_CASE,
 *     suffix = "Icon"
 * )
 * transformer.transform("arrow-left") // → "ArrowLeftIcon"
 * ```
 *
 * @property convention The naming convention to apply
 * @property suffix Optional suffix to append (e.g., "Icon")
 * @property prefix Optional prefix to prepend (e.g., "Ic")
 * @property removePrefix Prefix to remove from input (e.g., "ic_")
 * @property removeSuffix Suffix to remove from input (e.g., "_24dp")
 */
class ConventionNameTransformer(
    private val convention: NamingConvention = NamingConvention.PASCAL_CASE,
    private val suffix: String = "",
    private val prefix: String = "",
    private val removePrefix: String = "",
    private val removeSuffix: String = ""
) : IconNameTransformer() {

    override fun transform(fileName: String): String {
        // Clean the file name
        val cleaned = fileName
            .removeSuffix(".svg")
            .let { if (removePrefix.isNotEmpty()) it.removePrefix(removePrefix) else it }
            .let { if (removeSuffix.isNotEmpty()) it.removeSuffix(removeSuffix) else it }

        // Apply naming convention
        val converted = when (convention) {
            NamingConvention.PASCAL_CASE -> toPascalCase(cleaned)
            NamingConvention.CAMEL_CASE -> toCamelCase(cleaned)
            NamingConvention.SNAKE_CASE -> toSnakeCase(cleaned, uppercase = false)
            NamingConvention.SCREAMING_SNAKE -> toSnakeCase(cleaned, uppercase = true)
            NamingConvention.KEBAB_CASE -> toKebabCase(cleaned)
            NamingConvention.LOWER_CASE -> cleaned.lowercase().replace(Regex("[^a-z0-9]"), "")
            NamingConvention.UPPER_CASE -> cleaned.uppercase().replace(Regex("[^A-Z0-9]"), "")
        }

        return "$prefix$converted$suffix"
    }

    private fun toPascalCase(input: String): String {
        return splitWords(input).joinToString("") {
            it.replaceFirstChar { it.titlecase() }
        }
    }

    private fun toCamelCase(input: String): String {
        val words = splitWords(input)
        if (words.isEmpty()) return ""
        return words.first().lowercase() + words.drop(1).joinToString("") {
            it.replaceFirstChar { it.titlecase() }
        }
    }

    private fun toSnakeCase(input: String, uppercase: Boolean): String {
        val words = splitWords(input)
        return if (uppercase) {
            words.joinToString("_") { it.uppercase() }
        } else {
            words.joinToString("_") { it.lowercase() }
        }
    }

    private fun toKebabCase(input: String): String {
        return splitWords(input).joinToString("-") { it.lowercase() }
    }

    private fun splitWords(input: String): List<String> {
        // Split by common delimiters: -, _, space, and detect camelCase/PascalCase boundaries
        return input
            .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1_$2")
            .replace(Regex("([a-z\\d])([A-Z])"), "$1_$2")
            .split(Regex("[\\s\\-_]+")) // Split by -, _, space
            .filter { it.isNotBlank() }
    }

    /**
     * Override getSignature to include constructor parameters in the signature.
     * This ensures that changes to the transformer configuration are detected by Gradle's cache.
     */
    override fun getSignature(): String {
        return buildString {
            append("ConventionNameTransformer(")
            append("convention=$convention,")
            append("suffix='$suffix',")
            append("prefix='$prefix',")
            append("removePrefix='$removePrefix',")
            append("removeSuffix='$removeSuffix'")
            append(")")
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}


/**
 * Factory for creating common name transformers.
 */
object NameTransformerFactory {
    /**
     * Create a name transformer for a specific library.
     *
     * @param libraryId Unique identifier for the icon library
     * @return Appropriate name transformer instance
     */
    fun create(libraryId: String): IconNameTransformer {
        // This can be expanded with more cases for specific libraries in the future.
        return pascalCase()
    }

    /**
     * Create a transformer with PascalCase convention.
     *
     * @param suffix Optional suffix to append
     * @param prefix Optional prefix to prepend
     */
    fun pascalCase(suffix: String = "", prefix: String = ""): IconNameTransformer =
        ConventionNameTransformer(
            convention = NamingConvention.PASCAL_CASE,
            suffix = suffix,
            prefix = prefix
        )

    /**
     * Create a transformer with camelCase convention.
     *
     * @param suffix Optional suffix to append
     * @param prefix Optional prefix to prepend
     */
    fun camelCase(suffix: String = "", prefix: String = ""): IconNameTransformer =
        ConventionNameTransformer(
            convention = NamingConvention.CAMEL_CASE,
            suffix = suffix,
            prefix = prefix
        )

    /**
     * Create a transformer with snake_case convention.
     *
     * @param uppercase If true, creates SCREAMING_SNAKE_CASE
     */
    fun snakeCase(uppercase: Boolean = false): IconNameTransformer =
        ConventionNameTransformer(
            convention = if (uppercase) NamingConvention.SCREAMING_SNAKE else NamingConvention.SNAKE_CASE
        )

    /**
     * Create a transformer with kebab-case convention.
     */
    fun kebabCase(): IconNameTransformer =
        ConventionNameTransformer(
            convention = NamingConvention.KEBAB_CASE
        )

    /**
     * Create a transformer with lowercase convention.
     */
    fun lowerCase(): IconNameTransformer =
        ConventionNameTransformer(
            convention = NamingConvention.LOWER_CASE
        )

    /**
     * Create a transformer with UPPERCASE convention.
     */
    fun upperCase(): IconNameTransformer =
        ConventionNameTransformer(
            convention = NamingConvention.UPPER_CASE
        )

    /**
     * Create a transformer from a naming convention.
     *
     * @param convention The naming convention to apply
     * @param suffix Optional suffix to append
     * @param prefix Optional prefix to prepend
     * @param removePrefix Prefix to remove from input
     * @param removeSuffix Suffix to remove from input
     */
    fun fromConvention(
        convention: NamingConvention,
        suffix: String = "",
        prefix: String = "",
        removePrefix: String = "",
        removeSuffix: String = ""
    ): IconNameTransformer = ConventionNameTransformer(
        convention = convention,
        suffix = suffix,
        prefix = prefix,
        removePrefix = removePrefix,
        removeSuffix = removeSuffix
    )

}
