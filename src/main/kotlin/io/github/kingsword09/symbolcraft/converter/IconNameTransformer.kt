package io.github.kingsword09.symbolcraft.converter

/**
 * Strategy interface for transforming icon file names into Kotlin class names.
 *
 * Different icon libraries may have different naming conventions.
 * This interface allows users to implement custom naming transformers for their specific libraries.
 */
interface IconNameTransformer {
    /**
     * Transform an icon file name into a valid Kotlin class name.
     *
     * @param fileName The file name (with or without .svg extension)
     * @return Transformed Kotlin class name
     */
    fun transform(fileName: String): String
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
) : IconNameTransformer {

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
            it.replaceFirstChar { char -> char.titlecase() }
        }
    }

    private fun toCamelCase(input: String): String {
        val words = splitWords(input)
        if (words.isEmpty()) return ""
        return words.first().lowercase() + words.drop(1).joinToString("") {
            it.replaceFirstChar { char -> char.titlecase() }
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
            .replace(Regex("([a-z])([A-Z])"), "$1_$2") // camelCase → camel_Case
            .split(Regex("[\\s\\-_]+")) // Split by -, _, space
            .filter { it.isNotBlank() }
    }
}

/**
 * Naming transformer that applies custom logic via a lambda.
 *
 * Provides maximum flexibility for complex naming requirements.
 *
 * Example:
 * ```kotlin
 * val transformer = LambdaNameTransformer { fileName ->
 *     when {
 *         fileName.startsWith("ic_") -> fileName.removePrefix("ic_").capitalize()
 *         fileName.endsWith("_24dp") -> fileName.removeSuffix("_24dp").capitalize()
 *         else -> fileName.capitalize()
 *     }
 * }
 * ```
 *
 * @property transformFn Lambda function that performs the transformation
 */
class LambdaNameTransformer(
    private val transformFn: (String) -> String
) : IconNameTransformer {
    override fun transform(fileName: String): String {
        return transformFn(fileName)
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
        return when (libraryId) {
            "material-symbols" -> pascalCase()
            else -> pascalCase()
        }
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

    /**
     * Create a custom transformer using a lambda.
     *
     * @param transformFn Lambda function for transformation
     */
    fun custom(transformFn: (String) -> String): IconNameTransformer =
        LambdaNameTransformer(transformFn)
}
