import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradle.plugin.publish)
    `java-gradle-plugin`
    `maven-publish`
}

group = "io.github.kingsword09.symbolcraft"
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
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    
    // Optional local jars (place svg-to-compose-jvm and its runtime deps in libs/)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Gradle API
    compileOnly(libs.gradle.api)
    compileOnly(libs.kotlin.gradle.plugin)

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
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
