enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    
    // Force Kotlin version to prevent CI from using dev versions
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "org.jetbrains.kotlin") {
                useVersion("2.0.0")
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "SymbolCraft"

// Include all submodules
include(":symbolcraft-plugin")
include(":symbolcraft-runtime")
include(":symbolcraft-material-symbols")

// Note: example 作为独立项目，避免插件版本冲突，build it separately in example/ directory
