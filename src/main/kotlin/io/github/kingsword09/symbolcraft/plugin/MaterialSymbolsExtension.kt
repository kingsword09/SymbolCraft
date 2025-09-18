package io.github.kingsword09.symbolcraft.plugin

import io.github.kingsword09.symbolcraft.model.SymbolFill
import io.github.kingsword09.symbolcraft.model.SymbolVariant
import io.github.kingsword09.symbolcraft.model.SymbolStyle
import io.github.kingsword09.symbolcraft.model.SymbolStyles
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty

abstract class MaterialSymbolsExtension(private val project: Project) {
    abstract val cacheEnabled: Property<Boolean>
    abstract val cacheDirectory: Property<String>
    abstract val outputDirectory: Property<String>
    abstract val forceRegenerate: Property<Boolean>
    abstract val minifyOutput: Property<Boolean>
    abstract val packageName: Property<String>
    abstract val assetsDirectory: Property<String>
    // Optional: external SVG->Compose converter (CLI) support
    abstract val converterPath: Property<String>
    abstract val converterArgs: ListProperty<String>

    private val symbolsConfig = mutableMapOf<String, MutableList<SymbolStyle>>()

    init {
        cacheEnabled.convention(true)
        cacheDirectory.convention("build/material-symbols-cache")
        // Default to the main source set, inside the configured package
        // svg-to-compose will place files under this directory following the package path
        outputDirectory.convention("src/main/kotlin")
        forceRegenerate.convention(false)
        minifyOutput.convention(true)
        packageName.convention("io.github.kingsword09.symbolcraft.symbols")
        assetsDirectory.convention("src/main/assets/material-symbols")
        converterPath.convention("")
        converterArgs.convention(emptyList())
    }

    fun symbol(name: String, configure: SymbolConfigBuilder.() -> Unit) {
        val builder = SymbolConfigBuilder()
        builder.configure()
        symbolsConfig[name] = builder.styles
    }

    fun symbols(vararg names: String, configure: SymbolConfigBuilder.() -> Unit) {
        names.forEach { name -> symbol(name, configure) }
    }

    fun getSymbolsConfig(): Map<String, List<SymbolStyle>> = symbolsConfig.toMap()

    fun getConfigHash(): String = symbolsConfig.toString().hashCode().toString()
}

class SymbolConfigBuilder {
    val styles = mutableListOf<SymbolStyle>()

    fun style(
        weight: Int = 400,
        variant: SymbolVariant = SymbolVariant.OUTLINED,
        fill: SymbolFill = SymbolFill.UNFILLED,
        grade: Int = 0,
        opticalSize: Int = 24
    ) {
        styles.add(SymbolStyle(weight, variant, fill, grade, opticalSize))
    }

    fun styles(vararg pairs: Pair<Int, SymbolFill>) {
        pairs.forEach { (weight, fill) -> style(weight = weight, fill = fill) }
    }

    fun commonStyles() {
        styles.addAll(listOf(SymbolStyles.Regular, SymbolStyles.Medium, SymbolStyles.Bold))
    }

    fun lightStyles() {
        styles.addAll(listOf(SymbolStyles.Light, SymbolStyles.Regular))
    }
}
