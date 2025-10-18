enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
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
