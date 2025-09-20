package io.github.kingsword09.symbolcraft.util

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.logging.Logger

/**
 * Detects available Compose Preview dependencies in the project
 * to determine which preview annotations to use
 */
class PreviewDependencyDetector(
    private val project: Project,
    private val logger: Logger
) {

    data class PreviewCapabilities(
        val hasAndroidxPreview: Boolean = false,
        val hasJetpackDesktopPreview: Boolean = false,
        val detectedDependencies: List<String> = emptyList()
    )

    fun detectPreviewCapabilities(): PreviewCapabilities {
        val detectedDeps = mutableListOf<String>()
        var hasAndroidx = false
        var hasJetpack = false

        // Check all configurations for relevant dependencies
        project.configurations.forEach { config ->
            try {
                if (config.isCanBeResolved) {
                    config.resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
                        val moduleId = artifact.moduleVersion.id
                        val notation = "${moduleId.group}:${moduleId.name}"

                        when {
                            // androidx.compose.ui:ui-tooling-preview
                            notation.contains("androidx.compose.ui") &&
                            moduleId.name.contains("ui-tooling-preview") -> {
//                                hasAndroidx = true
                                detectedDeps.add("$notation:${moduleId.version}")
                                logger.info("🔍 Detected androidx.compose.ui:ui-tooling-preview")
                            }

                            // org.jetbrains.compose.ui:ui-tooling-preview
                            notation.contains("org.jetbrains.compose") &&
                            moduleId.name.contains("ui-tooling-preview") -> {
                                hasJetpack = true
                                detectedDeps.add("$notation:${moduleId.version}")
                                logger.info("🔍 Detected org.jetbrains.compose:ui-tooling-preview")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Some configurations might not be resolvable, ignore them
                logger.debug("Could not resolve configuration ${config.name}: ${e.message}")
            }
        }

        // Fallback: check declared dependencies if no resolved artifacts found
        if (!hasAndroidx && !hasJetpack) {
            val fallbackResult = checkDeclaredDependencies()
            hasAndroidx = fallbackResult.hasAndroidxPreview
            hasJetpack = fallbackResult.hasJetpackDesktopPreview
            detectedDeps.addAll(fallbackResult.detectedDependencies)
        }

        return PreviewCapabilities(
            hasAndroidxPreview = hasAndroidx,
            hasJetpackDesktopPreview = hasJetpack,
            detectedDependencies = detectedDeps
        )
    }

    private fun checkDeclaredDependencies(): PreviewCapabilities {
        val detectedDeps = mutableListOf<String>()
        var hasAndroidx = false
        var hasJetpack = false

        project.configurations.forEach { config ->
            config.dependencies.forEach { dependency ->
                val notation = "${dependency.group}:${dependency.name}"

                when {
                    // Check for androidx compose ui tooling
                    dependency.group == "androidx.compose.ui" && dependency.name == "ui-tooling-preview" -> {
                        hasAndroidx = true
                        detectedDeps.add("$notation:${dependency.version}")
                        logger.info("🔍 Found declared androidx.compose.ui:ui-tooling dependency")
                    }

                    // Check for jetbrains compose desktop tooling
                    (dependency.group == "androidx.compose.desktop" || dependency.group == "org.jetbrains.compose.ui") &&
                    dependency.name.contains("ui-tooling") -> {
                        hasJetpack = true
                        detectedDeps.add("$notation:${dependency.version}")
                        logger.info("🔍 Found declared desktop compose ui-tooling dependency")
                    }

                    // Log other compose dependencies but don't assume preview capability
                    dependency.group == "androidx.compose" ||
                    dependency.group?.startsWith("androidx.compose.") == true -> {
                        logger.debug("🔍 Found androidx.compose dependency: $notation (no preview capability assumed)")
                    }

                    dependency.group == "org.jetbrains.compose" ||
                    dependency.group?.startsWith("org.jetbrains.compose.") == true -> {
                        logger.debug("🔍 Found jetbrains.compose dependency: $notation (no preview capability assumed)")
                    }
                }
            }
        }

        return PreviewCapabilities(
            hasAndroidxPreview = hasAndroidx,
            hasJetpackDesktopPreview = hasJetpack,
            detectedDependencies = detectedDeps
        )
    }

    fun logPreviewCapabilities(capabilities: PreviewCapabilities) {
        logger.lifecycle("🔍 Preview Capabilities Detected:")
        logger.lifecycle("   📱 androidx.compose.ui Preview: ${if (capabilities.hasAndroidxPreview) "✅" else "❌"}")
        logger.lifecycle("   🖥️ jetbrains.compose.desktop Preview: ${if (capabilities.hasJetpackDesktopPreview) "✅" else "❌"}")

        if (capabilities.detectedDependencies.isNotEmpty()) {
            logger.lifecycle("   📦 Related dependencies:")
            capabilities.detectedDependencies.forEach { dep ->
                logger.lifecycle("      - $dep")
            }
        }

        if (!capabilities.hasAndroidxPreview && !capabilities.hasJetpackDesktopPreview) {
            logger.warn("⚠️ No Compose Preview dependencies detected.")
            logger.warn("   Add ui-tooling-preview dependency to enable preview generation.")
            logger.warn("   For Android: implementation(\"androidx.compose.ui:ui-tooling-preview:\$compose_version\")")
            logger.warn("   For Desktop: implementation(compose.desktop.ui-tooling-preview)")
        }
    }
}