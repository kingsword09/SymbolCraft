package io.github.kingsword09.symbolcraft.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class MaterialSymbolsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            "materialSymbols",
            MaterialSymbolsExtension::class.java,
            project
        )

        val generateTaskProvider = project.tasks.register("generateMaterialSymbols", GenerateSymbolsTask::class.java) { task ->
            task.group = "material symbols"
            task.description = "Generate Material Symbols icons based on configuration"
            task.extension.set(extension)
            task.inputs.property("symbolsConfig", extension.getConfigHash())
            task.outputs.dir(project.file(extension.outputDirectory.get()))
        }

        project.tasks.register("cleanSymbolsCache", CleanSymbolsCacheTask::class.java) { task ->
            task.group = "material symbols"
            task.description = "Clean Material Symbols cache"
            task.extension.set(extension)
        }

        project.tasks.register("validateSymbolsConfig", ValidateSymbolsConfigTask::class.java) { task ->
            task.group = "material symbols"
            task.description = "Validate Material Symbols configuration"
            task.extension.set(extension)
        }

        project.afterEvaluate {
            // Make Kotlin compilation depend on our generation
            project.tasks.configureEach { task ->
                val n = task.name
                if (n.startsWith("compile", ignoreCase = true) && n.endsWith("Kotlin", ignoreCase = true)) {
                    task.dependsOn(project.tasks.named("generateMaterialSymbols"))
                }
                // Fix Android asset merging dependency
                if (n.contains("merge", ignoreCase = true) && n.contains("Assets", ignoreCase = true)) {
                    task.dependsOn(project.tasks.named("generateMaterialSymbols"))
                }
                // Also add dependency for resource processing
                if (n.contains("process", ignoreCase = true) && n.contains("Resources", ignoreCase = true)) {
                    task.dependsOn(project.tasks.named("generateMaterialSymbols"))
                }
            }

            // No external conversion plugin wiring: we ship assets for runtime rendering.
        }
    }
}
