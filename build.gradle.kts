import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    `java-gradle-plugin`
    signing
}

group = "io.github.kingsword09"
version = "0.3.2"

kotlin {
    jvmToolchain(17)
}

// Configure Kotlin compiler options
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
    testImplementation(gradleTestKit())
}

// Configure Gradle Plugin Portal publication
gradlePlugin {
    website = "https://github.com/kingsword09/SymbolCraft"
    vcsUrl = "https://github.com/kingsword09/SymbolCraft"

    plugins {
        create("symbolcraft") {
            id = "io.github.kingsword09.symbolcraft"
            implementationClass = "io.github.kingsword09.symbolcraft.plugin.SymbolCraftPlugin"
            displayName = "SymbolCraft - Multi-Library Icon Generator"
            description = "Generate icons on-demand from multiple libraries (Material Symbols, Bootstrap Icons, etc.) for Compose Multiplatform with smart caching."
            tags = listOf("KMP", "Compose-Multiplatform", "material", "icons", "symbols", "generator")
        }
    }
}

// Configure Vanniktech Maven Publish
mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    coordinates(group.toString(), "symbolcraft", version.toString())

    pom {
        name.set("SymbolCraft")
        description.set("Generate icons on-demand from multiple libraries (Material Symbols, Bootstrap Icons, etc.) for Compose Multiplatform with smart caching.")
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

    // Always set isRequired to false
    isRequired = false

    if (signingKey != null && signingPassword != null && signingKey.isNotBlank() && signingPassword.isNotBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)

        // Configure signing after all publications are created
        afterEvaluate {
            sign(publishing.publications)
        }
    }
}

// Configure test framework
tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }

    addTestListener(object : org.gradle.api.tasks.testing.TestListener {
        override fun beforeSuite(suite: org.gradle.api.tasks.testing.TestDescriptor) = Unit
        override fun beforeTest(test: org.gradle.api.tasks.testing.TestDescriptor) = Unit

        override fun afterTest(test: org.gradle.api.tasks.testing.TestDescriptor, result: org.gradle.api.tasks.testing.TestResult) = Unit

        override fun afterSuite(suite: org.gradle.api.tasks.testing.TestDescriptor, result: org.gradle.api.tasks.testing.TestResult) {
            if (suite.parent == null) {
                println(
                    "\nTest Summary: ${result.resultType} | Total: ${result.testCount}, " +
                        "Passed: ${result.successfulTestCount}, Failed: ${result.failedTestCount}, " +
                        "Skipped: ${result.skippedTestCount}"
                )
            }
        }
    })
}

// Generate sources and javadoc JARs
java {
    withSourcesJar()
    withJavadocJar()
}

// Configure javadocJar to use Dokka V2 output
tasks.named<Jar>("javadocJar") {
    dependsOn("dokkaGeneratePublicationJavadoc")
    from(layout.buildDirectory.dir("dokka/javadoc"))
}

// Configure JAR manifest
tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "kingsword09",
            "Built-By" to System.getProperty("user.name"),
            "Built-JDK" to System.getProperty("java.version"),
            "Built-Gradle" to gradle.gradleVersion
        )
    }
}
