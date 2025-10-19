// Root project configuration for SymbolCraft Monorepo

plugins {
    // Declare plugins but don't apply to root project
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
}

// Unified version management for all modules
allprojects {
    group = "io.github.kingsword09"
    version = "0.4.0"  // Unified version across all modules
}

// Common configuration for all subprojects
subprojects {
    // Apply common settings after evaluation
    afterEvaluate {
        // Configure Kotlin compiler options if the project has Kotlin
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            compilerOptions {
                freeCompilerArgs.addAll(
                    listOf(
                        "-opt-in=kotlin.RequiresOptIn",
                        "-Xcontext-receivers"
                    )
                )
            }
        }
    }
}

// Unified publishing task
tasks.register("publishAll") {
    group = "publishing"
    description = "Publish all modules to Maven Central"

    dependsOn(
        ":symbolcraft-plugin:publishToMavenCentral",
        ":symbolcraft-runtime:publishToMavenCentral",
        ":symbolcraft-material-symbols:publishToMavenCentral"
    )
}

// Unified local publishing for testing
tasks.register("publishAllToMavenLocal") {
    group = "publishing"
    description = "Publish all modules to local Maven repository"

    dependsOn(
        ":symbolcraft-plugin:publishToMavenLocal",
        ":symbolcraft-runtime:publishToMavenLocal",
        ":symbolcraft-material-symbols:publishToMavenLocal"
    )
}
