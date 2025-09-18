package io.github.kingsword09.symbolcraft.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class PreviewSymbolsTask : DefaultTask() {
    @get:Internal
    abstract val extension: Property<MaterialSymbolsExtension>

    @TaskAction
    fun preview() {
        val map = extension.get().getSymbolsConfig()
        if (map.isEmpty()) {
            logger.lifecycle("No symbols configured.")
            return
        }
        logger.lifecycle("Previewing configured symbols:")
        map.forEach { (name, styles) ->
            val variants = styles.joinToString { it.signature }
            logger.lifecycle(" - $name: $variants")
        }
    }
}
