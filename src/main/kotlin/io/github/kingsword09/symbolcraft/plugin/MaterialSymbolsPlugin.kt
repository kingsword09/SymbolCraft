package io.github.kingsword09.symbolcraft.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class MaterialSymbolsPlugin : Plugin<Project> {
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
            task.assetsDir.set(project.layout.projectDirectory.dir(extension.assetsDirectory))
            task.cacheDirectory.set(extension.cacheDirectory)
            task.gradleUserHomeDir.set(project.gradle.gradleUserHomeDir.absolutePath)
            task.projectBuildDir.set(project.layout.buildDirectory.get().asFile.absolutePath)

            // Only detect preview capabilities if preview generation is enabled
            if (extension.generatePreview.getOrElse(false)) {
                val detector = io.github.kingsword09.symbolcraft.util.PreviewDependencyDetector(project, project.logger)
                val capabilities = detector.detectPreviewCapabilities()
                task.hasAndroidxPreview.set(capabilities.hasAndroidxPreview)
                task.hasJetpackPreview.set(capabilities.hasJetpackDesktopPreview)
            } else {
                // Preview disabled, set both to false
                task.hasAndroidxPreview.set(false)
                task.hasJetpackPreview.set(false)
            }

            task.inputs.property("symbolsConfig", extension.getConfigHash())
            task.inputs.property("generatePreview", extension.generatePreview)
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
