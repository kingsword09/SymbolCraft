import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradle.plugin.publish)
    `java-gradle-plugin`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

group = "io.github.kingsword09"
version = "1.0.0"

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
}

// Configure Gradle Plugin Portal publication
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

// Configure Maven publication
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("SymbolCraft")
                description.set("A powerful Gradle plugin for generating Material Symbols icons on-demand in Kotlin Multiplatform projects")
                url.set("https://github.com/kingsword09/SymbolCraft")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("kingsword09")
                        name.set("kingsword09")
                        email.set("kingsword09@gmail.com")
                        url.set("https://github.com/kingsword09")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/kingsword09/SymbolCraft.git")
                    developerConnection.set("scm:git:ssh://github.com/kingsword09/SymbolCraft.git")
                    url.set("https://github.com/kingsword09/SymbolCraft")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/kingsword09/SymbolCraft/issues")
                }
            }
        }
    }

    repositories {
        maven {
            name = "sonatype"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME")
                password = project.findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}

// Configure artifact signing
signing {
    val signingKey = project.findProperty("signingKey") as String? ?: System.getenv("SIGNING_KEY")
    val signingPassword = project.findProperty("signingPassword") as String? ?: System.getenv("SIGNING_PASSWORD")

    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["maven"])
    }
}

// Configure test framework
tasks.test {
    useJUnitPlatform()
}

// Generate sources JAR
java {
    withSourcesJar()
    withJavadocJar()
}

// Configure Nexus publishing
nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME"))
            password.set(project.findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD"))
        }
    }
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