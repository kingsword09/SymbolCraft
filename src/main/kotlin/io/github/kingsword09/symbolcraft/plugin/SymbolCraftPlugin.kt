package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.tasks.CleanSymbolsCacheTask
import io.github.kingsword09.symbolcraft.tasks.CleanSymbolsIconsTask
import io.github.kingsword09.symbolcraft.tasks.GenerateSymbolsTask
import io.github.kingsword09.symbolcraft.tasks.ValidateSymbolsConfigTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin entry point registered as `io.github.kingsword09.symbolcraft`.
 *
 * The plugin wires the [SymbolCraftExtension] DSL, registers generation/cleanup tasks, and ensures
 * Kotlin compilation depends on freshly generated icons.
 */
class SymbolCraftPlugin : Plugin<Project> {
    /** Installs the extension and all supporting tasks on the target [project]. */
    override fun apply(project: Project) {
        val extension = project.extensions.create("symbolCraft", SymbolCraftExtension::class.java)
        extension.projectDirectory.set(project.layout.projectDirectory.asFile.absolutePath)

        val generateTaskProvider =
            project.tasks.register("generateSymbolCraftIcons", GenerateSymbolsTask::class.java) {
                task ->
                task.group = "symbolcraft"
                task.description = "Generate icons from configured libraries"
                task.extension.set(extension)
                task.outputDir.set(project.layout.projectDirectory.dir(extension.outputDirectory))
                task.cacheDirectory.set(extension.cacheDirectory)
                task.gradleUserHomeDir.set(project.gradle.gradleUserHomeDir.absolutePath)
                task.projectBuildDir.set(project.layout.buildDirectory.get().asFile.absolutePath)
                task.inputs.property("symbolsConfig", extension.getConfigHash())
                task.inputs.property("generatePreview", extension.generatePreview)
                task.inputs.property("namingConfigSignature", extension.namingConfigSignature())
            }

        project.tasks.register("cleanSymbolCraftCache", CleanSymbolsCacheTask::class.java) { task ->
            task.group = "symbolcraft"
            task.description = "Clean SymbolCraft icon cache"
            task.cacheDirectory.set(extension.cacheDirectory)
            task.projectBuildDir.set(project.layout.buildDirectory.get().asFile.absolutePath)
        }

        project.tasks.register("cleanSymbolCraftIcons", CleanSymbolsIconsTask::class.java) { task ->
            task.group = "symbolcraft"
            task.description = "Clean generated SymbolCraft icon files"
            task.packageName.set(extension.packageName)
            task.outputDirectory.set(project.layout.projectDirectory.dir(extension.outputDirectory))
        }

        project.tasks.register(
            "validateSymbolCraftConfig",
            ValidateSymbolsConfigTask::class.java,
        ) { task ->
            task.group = "symbolcraft"
            task.description = "Validate SymbolCraft icon configuration"
            task.extension.set(extension)
        }

        project.afterEvaluate {
            // Make Kotlin compilation depend on our generation
            project.tasks.configureEach { task ->
                val n = task.name
                if (
                    n.startsWith("compile", ignoreCase = true) &&
                        n.contains("Kotlin", ignoreCase = true)
                ) {
                    task.dependsOn(generateTaskProvider)
                }
                // Fix metadata compilation dependency for multiplatform projects
                if (n.contains("compileCommonMainKotlinMetadata", ignoreCase = true)) {
                    task.dependsOn(generateTaskProvider)
                }
                // Fix Android compilation dependencies
                if (
                    n.contains("compileDebugKotlin", ignoreCase = true) ||
                        n.contains("compileReleaseKotlin", ignoreCase = true)
                ) {
                    task.dependsOn(generateTaskProvider)
                }
                // Fix Android asset merging dependency
                if (
                    n.contains("merge", ignoreCase = true) &&
                        n.contains("Assets", ignoreCase = true)
                ) {
                    task.dependsOn(generateTaskProvider)
                }
                // Also add dependency for resource processing
                if (
                    n.contains("process", ignoreCase = true) &&
                        n.contains("Resources", ignoreCase = true)
                ) {
                    task.dependsOn(generateTaskProvider)
                }
            }
        }
    }
}
