package io.github.kingsword09.symbolcraft.tasks

import io.github.kingsword09.symbolcraft.plugin.SymbolCraftExtension
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
    @get:Internal abstract val extension: Property<SymbolCraftExtension>

    /** Checks that at least one icon configuration has been declared. */
    @TaskAction
    fun validate() {
        val config = extension.get().getIconsConfig()
        if (config.isEmpty()) {
            throw IllegalStateException(
                "No icons configured. Use symbolCraft { } in build.gradle.kts"
            )
        }
        val count = config.values.sumOf { it.size }
        logger.lifecycle(
            "✅ Valid configuration. Icons: ${config.size}, Total configurations: $count"
        )
    }
}
