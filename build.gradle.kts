import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradle.plugin.publish)
    `java-gradle-plugin`
    `maven-publish`
}

group = "io.github.kingsword09"
version = "1.0.0"

kotlin {
    jvmToolchain(17)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
      freeCompilerArgs.addAll(
        listOf(
          "-opt-in=kotlin.RequiresOptIn",
          "-Xcontext-receivers"
        )
      )
    }
}

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // HTTP Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    // SVG to Compose (with automatic transitive dependency resolution)
    implementation(libs.svg.to.compose)

    // Gradle API
    compileOnly(libs.gradle.api)
    compileOnly(libs.kotlin.gradle.plugin)

    // Testing
    testImplementation(libs.kotlin.test)
}

gradlePlugin {
    website = "https://github.com/kingsword09/SymbolCraft"
    vcsUrl = "https://github.com/kingsword09/SymbolCraft"

    plugins {
        create("materialSymbols") {
            id = "io.github.kingsword09.symbolcraft"
            implementationClass = "io.github.kingsword09.symbolcraft.plugin.MaterialSymbolsPlugin"
            displayName = "SymbolCraft - Material Symbols Generator"
            description = "Generate Material Symbols icons on-demand with caching support for Compose"
            tags = listOf("android", "compose", "material", "icons", "symbols")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}