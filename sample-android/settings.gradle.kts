pluginManagement {
    // Resolve the local plugin from parent project without publishing
    includeBuild("..")
    plugins {
        id("com.android.application") version "8.5.2"
        id("org.jetbrains.kotlin.android") version "2.0.0"
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    }
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
    }
}

rootProject.name = "symbolcraft-sample-android"
include(":app")
