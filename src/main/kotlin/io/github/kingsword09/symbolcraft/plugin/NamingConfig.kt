package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.converter.NamingConvention
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Configuration for icon naming transformation.
 *
 * Provides both preset methods and fine-grained control over naming conventions.
 *
 * Example:
 * ```kotlin
 * symbolCraft {
 *     naming {
 *         pascalCase(suffix = "Icon")  // Preset
 *         // Or customize:
 *         namingConvention.set(NamingConvention.PASCAL_CASE)
 *         suffix.set("Icon")
 *     }
 * }
 * ```
 */
abstract class NamingConfig @Inject constructor(
    private val objects: ObjectFactory
) {
    /**
     * Naming convention to apply (e.g., PASCAL_CASE, CAMEL_CASE).
     */
    abstract val namingConvention: Property<NamingConvention>
    
    /**
     * Suffix to append to generated class names (e.g., "Icon" → HomeIcon).
     */
    abstract val suffix: Property<String>
    
    /**
     * Prefix to prepend to generated class names (e.g., "Ic" → IcHome).
     */
    abstract val prefix: Property<String>
    
    /**
     * Prefix to remove from input file names before transformation (e.g., "ic_").
     */
    abstract val removePrefix: Property<String>
    
    /**
     * Suffix to remove from input file names before transformation (e.g., "_24dp").
     */
    abstract val removeSuffix: Property<String>
    
    /**
     * Custom transformer function that takes full control of naming.
     * When set, this overrides convention-based transformation.
     */
    abstract val transformer: Property<(String) -> String>
    
    init {
        namingConvention.convention(NamingConvention.PASCAL_CASE)
        suffix.convention("")
        prefix.convention("")
        removePrefix.convention("")
        removeSuffix.convention("")
    }
    
    // ========= Preset Methods =========
    
    /**
     * Apply PascalCase convention with optional suffix/prefix.
     *
     * Example: home-icon → HomeIcon (with suffix = "Icon")
     *
     * @param suffix Optional suffix to append (e.g., "Icon")
     * @param prefix Optional prefix to prepend (e.g., "Ic")
     */
    fun pascalCase(suffix: String = "", prefix: String = "") {
        namingConvention.set(NamingConvention.PASCAL_CASE)
        this.suffix.set(suffix)
        this.prefix.set(prefix)
    }
    
    /**
     * Apply camelCase convention with optional suffix/prefix.
     *
     * Example: home-icon → homeIcon (with prefix = "ic" → icHomeIcon)
     *
     * @param suffix Optional suffix to append
     * @param prefix Optional prefix to prepend (e.g., "ic")
     */
    fun camelCase(suffix: String = "", prefix: String = "") {
        namingConvention.set(NamingConvention.CAMEL_CASE)
        this.suffix.set(suffix)
        this.prefix.set(prefix)
    }
    
    /**
     * Apply snake_case or SCREAMING_SNAKE_CASE convention.
     *
     * Example:
     * - home-icon → home_icon (uppercase = false)
     * - home-icon → HOME_ICON (uppercase = true)
     *
     * @param uppercase If true, uses SCREAMING_SNAKE_CASE
     */
    fun snakeCase(uppercase: Boolean = false) {
        namingConvention.set(
            if (uppercase) NamingConvention.SCREAMING_SNAKE 
            else NamingConvention.SNAKE_CASE
        )
    }
    
    /**
     * Apply kebab-case convention.
     *
     * Example: home_icon → home-icon
     */
    fun kebabCase() {
        namingConvention.set(NamingConvention.KEBAB_CASE)
    }
    
    /**
     * Apply lowercase convention (removes all special characters).
     *
     * Example: Home-Icon → homeicon
     */
    fun lowerCase() {
        namingConvention.set(NamingConvention.LOWER_CASE)
    }
    
    /**
     * Apply UPPERCASE convention (removes all special characters).
     *
     * Example: home-icon → HOMEICON
     */
    fun upperCase() {
        namingConvention.set(NamingConvention.UPPER_CASE)
    }
    
    /**
     * Provide a custom transformation function.
     *
     * This gives you full control over the naming logic and overrides
     * convention-based transformation.
     *
     * Example:
     * ```kotlin
     * naming {
     *     custom { fileName ->
     *         fileName.removePrefix("ic_").capitalize() + "Icon"
     *     }
     * }
     * ```
     *
     * @param transform Lambda function that transforms a file name to a class name
     */
    fun custom(transform: (String) -> String) {
        transformer.set(transform)
    }

    /**
     * Produce a stable snapshot representation of the naming configuration.
     */
    internal fun snapshotSignature(): String {
        val builder = StringBuilder()
        builder.append("convention=")
        builder.append(namingConvention.orNull)
        builder.append("|suffix=")
        builder.append(suffix.orNull)
        builder.append("|prefix=")
        builder.append(prefix.orNull)
        builder.append("|removePrefix=")
        builder.append(removePrefix.orNull)
        builder.append("|removeSuffix=")
        builder.append(removeSuffix.orNull)
        val transformerLambda = transformer.orNull
        builder.append("|transformer=")
        if (transformerLambda != null) {
            builder.append(transformerLambda::class.java.name)
            builder.append('@')
            builder.append(System.identityHashCode(transformerLambda))
        } else {
            builder.append("null")
        }
        return builder.toString()
    }
}
