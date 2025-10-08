package io.github.kingsword09.symbolcraft.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * Gradle plugin entry point registered as `io.github.kingsword09.symbolcraft`.
 *
 * The plugin wires the [MaterialSymbolsExtension] DSL, registers generation/cleanup tasks,
 * and ensures Kotlin compilation depends on freshly generated icons.
 */
class MaterialSymbolsPlugin : Plugin<Project> {
    /**
     * Installs the extension and all supporting tasks on the target [project].
     */
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            "materialSymbols",
            MaterialSymbolsExtension::class.java
        )

        val generateTaskProvider = project.tasks.register("generateMaterialSymbols", GenerateSymbolsTask::class.java) { task ->
            task.group = "material symbols"
            task.description = "Generate Material Symbols icons based on configuration"
            task.extension.set(extension)
            task.outputDir.set(project.layout.projectDirectory.dir(extension.outputDirectory))
            task.cacheDirectory.set(extension.cacheDirectory)
            task.gradleUserHomeDir.set(project.gradle.gradleUserHomeDir.absolutePath)
            task.projectBuildDir.set(project.layout.buildDirectory.get().asFile.absolutePath)
            task.inputs.property("symbolsConfig", extension.getConfigHash())
            task.inputs.property("generatePreview", extension.generatePreview)
        }

        project.tasks.register("cleanSymbolsCache", CleanSymbolsCacheTask::class.java) { task ->
            task.group = "material symbols"
            task.description = "Clean Material Symbols cache"
            task.extension.set(extension)
            task.projectBuildDir.set(project.layout.buildDirectory.get().asFile.absolutePath)
        }

        project.tasks.register("cleanGeneratedSymbols") { task ->
            task.group = "material symbols"
            task.description = "Clean generated Material Symbols files"
            task.doLast {
                val packageName = extension.packageName.get()
                val outputDir = project.layout.projectDirectory.dir(extension.outputDirectory).get().asFile
                val packagePath = packageName.replace('.', '/')
                val symbolsDir = File(outputDir, "$packagePath/materialsymbols")
                val mainSymbolsFile = File(outputDir, "$packagePath/__MaterialSymbols.kt")

                var deletedCount = 0
                if (symbolsDir.exists()) {
                    symbolsDir.listFiles()?.forEach { file ->
                        if (file.isFile && file.extension == "kt") {
                            file.delete()
                            deletedCount++
                        }
                    }
                    if (symbolsDir.listFiles()?.isEmpty() == true) {
                        symbolsDir.delete()
                    }
                }

                if (mainSymbolsFile.exists()) {
                    mainSymbolsFile.delete()
                    deletedCount++
                }

                project.logger.lifecycle("🧹 Cleaned $deletedCount generated symbol files")
            }
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
                if (n.startsWith("compile", ignoreCase = true) && n.contains("Kotlin", ignoreCase = true)) {
                    task.dependsOn(generateTaskProvider)
                }
                // Fix metadata compilation dependency for multiplatform projects
                if (n.contains("compileCommonMainKotlinMetadata", ignoreCase = true)) {
                    task.dependsOn(generateTaskProvider)
                }
                // Fix Android compilation dependencies
                if (n.contains("compileDebugKotlin", ignoreCase = true) || n.contains("compileReleaseKotlin", ignoreCase = true)) {
                    task.dependsOn(generateTaskProvider)
                }
                // Fix Android asset merging dependency
                if (n.contains("merge", ignoreCase = true) && n.contains("Assets", ignoreCase = true)) {
                    task.dependsOn(generateTaskProvider)
                }
                // Also add dependency for resource processing
                if (n.contains("process", ignoreCase = true) && n.contains("Resources", ignoreCase = true)) {
                    task.dependsOn(generateTaskProvider)
                }
            }
        }
    }
}
