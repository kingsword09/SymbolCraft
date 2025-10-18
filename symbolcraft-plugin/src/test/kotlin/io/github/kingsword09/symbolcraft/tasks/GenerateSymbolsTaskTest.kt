package io.github.kingsword09.symbolcraft.tasks

import io.github.kingsword09.symbolcraft.model.ExternalIconConfig
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Path
import java.security.MessageDigest

@OptIn(ExperimentalPathApi::class)
class GenerateSymbolsTaskTest {

    private lateinit var projectDir: Path

    @BeforeTest
    fun setUp() {
        projectDir = createTempDirectory("symbolcraft-testkit")
    }

    @AfterTest
    fun tearDown() {
        if (::projectDir.isInitialized) {
            projectDir.toFile().deleteRecursively()
        }
    }

    @Test
    fun `local icons with custom library name generate compose sources`() {
        createSettings("symbolcraft-local-icons")
        createBuildScript(
            """
                cacheEnabled.set(false)
                localIcons(libraryName = "brand") {
                    directory = "src/icons"
                }
            """.trimIndent()
        )

        writeSvg("src/icons/brand/telephone-svgrepo-com.svg")
        writeSvg("src/icons/brand/marketing/mark.svg")

        val result = runGradle("generateSymbolCraftIcons")
        assertEquals(TaskOutcome.SUCCESS, result.task(":generateSymbolCraftIcons")?.outcome)

        val libraryDir = generatedDir("icons/brand")
        assertTrue(libraryDir.toFile().exists(), "Expected icons/brand directory to exist")

        val generatedIcons = libraryDir.toFile()
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .toList()

        assertTrue(generatedIcons.isNotEmpty(), "Expected generated icon files for local library")

        val iconNames = generatedIcons.map { it.nameWithoutExtension }
        assertTrue(iconNames.any { it == "BrandTelephoneSvgrepoCom" }, "Generated names: $iconNames")
        assertTrue(iconNames.none { it.contains("BrandTelephoneSvgrepoComBrandTelephoneSvgrepoCom") }, "Generated names: $iconNames")

        val telephoneFile = generatedIcons.first { it.nameWithoutExtension == "BrandTelephoneSvgrepoCom" }
        val iconContent = telephoneFile.readText()
        assertTrue(iconContent.contains("package com.test.symbols.icons.brand"))
        assertTrue(iconContent.contains("ImageVector"))
    }

    @Test
    fun `local icons default to library id local when omitted`() {
        createSettings("symbolcraft-local-default")
        createBuildScript(
            """
                cacheEnabled.set(false)
                localIcons {
                    directory = "src/icons"
                }
            """.trimIndent()
        )

        writeSvg("src/icons/ui/search.svg")

        val result = runGradle("generateSymbolCraftIcons")
        assertEquals(TaskOutcome.SUCCESS, result.task(":generateSymbolCraftIcons")?.outcome)

        val libraryDir = generatedDir("icons/local")
        assertTrue(libraryDir.toFile().exists(), "Expected icons/local directory for default library id")

        val generatedIcons = libraryDir.toFile()
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .toList()

        assertTrue(generatedIcons.isNotEmpty(), "Expected generated icon files in default library directory")
        val iconContent = generatedIcons.first().readText()
        assertTrue(iconContent.contains("package com.test.symbols.icons.local"))
    }

    @Test
    fun `local and remote icons generate when remote svg is cached`() {
        createSettings("symbolcraft-mixed")
        createBuildScript(
            """
                cacheEnabled.set(true)
                cacheDirectory.set("symbolcraft-cache")
                localIcons(libraryName = "brand") {
                    directory = "src/icons"
                }
                materialSymbol("home") {
                    style()
                }
            """.trimIndent()
        )

        writeSvg("src/icons/brand/logo.svg")
        seedMaterialSymbolsCache(iconName = "home")

        val result = runGradle("generateSymbolCraftIcons")
        assertEquals(TaskOutcome.SUCCESS, result.task(":generateSymbolCraftIcons")?.outcome)

        val localDir = generatedDir("icons/brand")
        val remoteDir = generatedDir("icons/materialsymbols")

        assertTrue(localDir.toFile().exists(), "Expected icons/brand directory to exist")
        assertTrue(remoteDir.toFile().exists(), "Expected icons/materialsymbols directory to exist")

        val localIcons = localDir.toFile().walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
        val remoteIcons = remoteDir.toFile().walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()

        assertTrue(localIcons.isNotEmpty(), "Expected generated local icon files")
        assertTrue(remoteIcons.isNotEmpty(), "Expected generated remote icon files from cache")

        val remoteContent = remoteIcons.first().readText()
        assertTrue(remoteContent.contains("package com.test.symbols.icons.materialsymbols"))
        assertTrue(remoteContent.contains("ImageVector"))
    }

    @Test
    fun `external icons reuse seeded cache alongside local icons`() {
        createSettings("symbolcraft-external")
        createBuildScript(
            """
                cacheEnabled.set(true)
                cacheDirectory.set("symbolcraft-cache")
                localIcons(libraryName = "brand") {
                    directory = "src/icons"
                }
                externalIcon("globe", libraryName = "brandcdn") {
                    urlTemplate = "https://static.example.com/icons/{name}.svg"
                    styleParam("variant", "default")
                }
            """.trimIndent()
        )

        writeSvg("src/icons/brand/logo.svg")
        val externalConfig = ExternalIconConfig(
            libraryName = "brandcdn",
            urlTemplate = "https://static.example.com/icons/{name}.svg",
            styleParams = mapOf("variant" to "default")
        )
        seedExternalIconCache(iconName = "globe", config = externalConfig)

        val result = runGradle("generateSymbolCraftIcons")
        assertEquals(TaskOutcome.SUCCESS, result.task(":generateSymbolCraftIcons")?.outcome)

        val localDir = generatedDir("icons/brand")
        val externalDir = generatedDir("icons/brandcdn")

        assertTrue(localDir.toFile().exists(), "Expected icons/brand directory to exist")
        assertTrue(externalDir.toFile().exists(), "Expected icons/brandcdn directory to exist")

        val externalIcons = externalDir.toFile()
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .toList()

        assertTrue(externalIcons.isNotEmpty(), "Expected generated external icon files from cache")
        val externalContent = externalIcons.first().readText()
        assertTrue(externalContent.contains("package com.test.symbols.icons.brandcdn"))
        assertTrue(externalContent.contains("ImageVector"))
    }

    @Test
    fun `subsequent mixed run is up to date with unchanged inputs`() {
        createSettings("symbolcraft-mixed-up-to-date")
        createBuildScript(
            """
                cacheEnabled.set(true)
                cacheDirectory.set("symbolcraft-cache")
                localIcons(libraryName = "brand") {
                    directory = "src/icons"
                }
                materialSymbol("home") {
                    style()
                }
            """.trimIndent()
        )

        writeSvg("src/icons/brand/logo.svg")
        seedMaterialSymbolsCache(iconName = "home")

        val firstRun = runGradle("generateSymbolCraftIcons")
        assertEquals(TaskOutcome.SUCCESS, firstRun.task(":generateSymbolCraftIcons")?.outcome)

        val secondRun = runGradle("generateSymbolCraftIcons")
        assertEquals(TaskOutcome.UP_TO_DATE, secondRun.task(":generateSymbolCraftIcons")?.outcome)
    }

    private fun createSettings(projectName: String) {
        writeProjectFile("settings.gradle.kts", """rootProject.name = "$projectName"""")
    }

    private fun createBuildScript(configBody: String) {
        writeProjectFile(
            "build.gradle.kts",
            buildScript(configBody)
        )
    }

    private fun generatedDir(suffix: String): Path =
        projectDir.resolve("build/generated/symbols/com/test/symbols/$suffix")

    private fun writeProjectFile(relativePath: String, content: String) {
        val target = projectDir.resolve(relativePath)
        target.parent?.createDirectories()
        target.writeText(content.trimIndent() + "\n")
    }

    private fun writeSvg(relativePath: String) {
        val svgContent = """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                <path d="M12 2a10 10 0 1 1-0.001 20.001A10 10 0 0 1 12 2z" fill="currentColor"/>
            </svg>
        """.trimIndent()
        val target = projectDir.resolve(relativePath)
        target.parent?.createDirectories()
        target.writeText(svgContent)
    }

    private fun buildScript(configBody: String): String = """
        plugins {
            id("io.github.kingsword09.symbolcraft")
        }

        repositories {
            mavenCentral()
        }

        symbolCraft {
            packageName.set("com.test.symbols")
            outputDirectory.set("build/generated/symbols")
            $configBody
        }
    """.trimIndent()

    private fun runGradle(vararg arguments: String) =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withArguments(*arguments, "--stacktrace")
            .withPluginClasspath()
            .forwardOutput()
            .build()

    private fun seedMaterialSymbolsCache(iconName: String) {
        val cacheDir = projectDir.resolve("build/symbolcraft-cache/svg-cache")
        cacheDir.createDirectories()

        val cacheKey = "${sanitize(iconName)}_material-symbols_400_outlined_unfilled"
        val svgContent = """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                <path d="M12 2a10 10 0 1 1-0.001 20.001A10 10 0 0 1 12 2z" fill="currentColor"/>
            </svg>
        """.trimIndent()
        val cacheFile = cacheDir.resolve("$cacheKey.svg")
        val metaFile = cacheDir.resolve("$cacheKey.meta")
        val url = "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsoutlined/$iconName/default/24px.svg"
        val hash = sha256(svgContent)

        cacheFile.writeText(svgContent)
        metaFile.writeText("${System.currentTimeMillis()}\n$url\n$hash")
    }

    private fun seedExternalIconCache(iconName: String, config: ExternalIconConfig) {
        val cacheDir = projectDir.resolve("build/symbolcraft-cache/svg-cache")
        cacheDir.createDirectories()

        val cacheKey = config.getCacheKey(iconName)
        val svgContent = """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                <path d="M12 2a10 10 0 1 1-0.001 20.001A10 10 0 0 1 12 2z" fill="currentColor"/>
            </svg>
        """.trimIndent()
        val cacheFile = cacheDir.resolve("$cacheKey.svg")
        val metaFile = cacheDir.resolve("$cacheKey.meta")
        val url = config.buildUrl(iconName)
        val hash = sha256(svgContent)

        cacheFile.writeText(svgContent)
        metaFile.writeText("${System.currentTimeMillis()}\n$url\n$hash")
    }

    private fun sha256(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(content.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    private fun sanitize(iconName: String): String =
        iconName
            .replace("/", "_")
            .replace("\\", "_")
            .replace(Regex("[^a-zA-Z0-9_-]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')
            .ifEmpty { "icon" }
}
