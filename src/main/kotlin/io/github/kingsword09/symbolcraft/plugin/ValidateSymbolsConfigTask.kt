package io.github.kingsword09.symbolcraft.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * Task that validates the DSL configuration before generation.
 *
 * Exposed to consumers as `validateSymbolsConfig`.
 */
abstract class ValidateSymbolsConfigTask : DefaultTask() {
    @get:Internal
    abstract val extension: Property<MaterialSymbolsExtension>

    /**
     * Checks that at least one symbol and variant has been declared.
     */
    @TaskAction
    fun validate() {
        val config = extension.get().getSymbolsConfig()
        if (config.isEmpty()) {
            throw IllegalStateException("No symbols configured. Use materialSymbols { } in build.gradle.kts")
        }
        val count = config.values.sumOf { it.size }
        logger.lifecycle("Valid configuration. Symbols: ${config.size}, Variants: $count")
    }
}
