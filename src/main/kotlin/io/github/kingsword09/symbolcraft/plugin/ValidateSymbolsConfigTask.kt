package io.github.kingsword09.symbolcraft.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class ValidateSymbolsConfigTask : DefaultTask() {
    @get:Internal
    abstract val extension: Property<MaterialSymbolsExtension>

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
