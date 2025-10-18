// SymbolCraft Runtime Module
// This module provides runtime support for icon loading and caching
// TODO: Will be implemented in future iterations

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.maven.publish)
    signing
}

kotlin {
    jvmToolchain(17)

    // Target platforms
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    jvm()

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        iosX64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SymbolCraftRuntime"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose dependencies
            implementation(compose.runtime)
            implementation(compose.ui)

            // Kotlin coroutines
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
    }
}

android {
    namespace = "io.github.kingsword09.symbolcraft.runtime"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Maven publishing configuration
mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(
        groupId = group.toString(),
        artifactId = "symbolcraft-runtime",
        version = version.toString()
    )

    pom {
        name.set("SymbolCraft Runtime")
        description.set("Runtime library for SymbolCraft icon system with lazy loading and caching support")
        inceptionYear.set("2025")
        url.set("https://github.com/kingsword09/SymbolCraft")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("kingsword09")
                name.set("kingsword09")
                url.set("https://github.com/kingsword09")
                email.set("kingsword09@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/kingsword09/SymbolCraft")
            connection.set("scm:git:git://github.com/kingsword09/SymbolCraft.git")
            developerConnection.set("scm:git:ssh://git@github.com/kingsword09/SymbolCraft.git")
        }
    }
}

signing {
    val signingKey = project.findProperty("signingKey") as String? ?: System.getenv("SIGNING_KEY")
    val signingPassword = project.findProperty("signingPassword") as String? ?: System.getenv("SIGNING_PASSWORD")

    isRequired = false

    if (signingKey != null && signingPassword != null && signingKey.isNotBlank() && signingPassword.isNotBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)

        afterEvaluate {
            sign(publishing.publications)
        }
    }
}
