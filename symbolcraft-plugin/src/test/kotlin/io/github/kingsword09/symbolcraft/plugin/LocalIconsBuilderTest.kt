package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.model.LocalIconConfig
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalPathApi::class)
class LocalIconsBuilderTest {

    private lateinit var projectDir: java.nio.file.Path

    @BeforeTest
    fun setUp() {
        projectDir = createTempDirectory("symbolcraft-local-icons-test")
    }

    @AfterTest
    fun tearDown() {
        if (projectDir.exists()) {
            projectDir.deleteRecursively()
        }
    }

    @Test
    fun `default include picks every svg recursively`() {
        writeSvg("assets/icons/home.svg")
        writeSvg("assets/icons/nested/search.svg")
        writeText("assets/icons/notes.txt", "ignore me")

        val configs = buildLocalIcons {
            directory = "assets/icons"
        }

        assertEquals(2, configs.size)
        assertTrue(configs.containsKey("home"))
        assertTrue(configs.containsKey("nested_search"))
    }

    @Test
    fun `include pattern filters svg set`() {
        writeSvg("design/exported/brand/logo.svg")
        writeSvg("design/exported/brand/sub/icon.svg")
        writeSvg("design/exported/system/alert.svg")

        val configs = buildLocalIcons {
            directory = "design/exported"
            include("brand/**/*.svg")
        }

        println("include filter keys: ${configs.keys}")
        assertEquals(2, configs.size)
        assertTrue(configs.keys.containsAll(listOf("brand_logo", "brand_sub_icon")))
    }

    @Test
    fun `exclude pattern removes matched files`() {
        writeSvg("resources/icons/default/search.svg")
        writeSvg("resources/icons/legacy/home.svg")

        val configs = buildLocalIcons {
            directory = "resources/icons"
            exclude("legacy/**")
        }

        assertEquals(1, configs.size)
        assertTrue(configs.containsKey("default_search"))
    }

    @Test
    fun `duplicate sanitized names gain numeric suffix`() {
        writeSvg("assets/icons/brand/icon.svg")
        writeSvg("assets/icons/brand/icon!.svg") // sanitized to same base name

        val configs = buildLocalIcons {
            directory = "assets/icons"
        }

        assertEquals(2, configs.size)
        assertTrue(configs.containsKey("brand_icon"))
        assertTrue(configs.containsKey("brand_icon_2"))
    }

    @Test
    fun `empty result throws informative error`() {
        writeSvg("assets/icons/readme.svg") // Will be excluded

        val exception = assertFailsWith<IllegalStateException> {
            buildLocalIcons(libraryName = "empty") {
                directory = "assets/icons"
                include("no-match/**")
            }
        }
        assertTrue(exception.message?.contains("No SVG icons found") == true)
    }

    private fun buildLocalIcons(
        libraryName: String = "local",
        configure: LocalIconsBuilder.() -> Unit
    ): Map<String, LocalIconConfig> {
        val builder = LocalIconsBuilder(projectDir.toAbsolutePath().toString())
        builder.configure()
        return builder.build(libraryName)
    }

    private fun writeSvg(relativePath: String) {
        writeText(relativePath, "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>")
    }

    private fun writeText(relativePath: String, content: String) {
        val path = projectDir.resolve(relativePath)
        path.parent?.createDirectories()
        path.writeText(content)
    }
}
