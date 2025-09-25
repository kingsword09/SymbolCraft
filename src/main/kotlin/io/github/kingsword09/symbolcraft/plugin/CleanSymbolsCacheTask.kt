package io.github.kingsword09.symbolcraft.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class CleanSymbolsCacheTask : DefaultTask() {
    @get:Internal
    abstract val extension: Property<MaterialSymbolsExtension>

    @TaskAction
    fun clean() {
        val dir = File(extension.get().cacheDirectory.get())
        if (dir.exists()) {
            dir.deleteRecursively()
            logger.lifecycle("Cleaned symbols cache: $dir")
        } else {
            logger.lifecycle("No cache to clean: $dir")
        }
    }
}
