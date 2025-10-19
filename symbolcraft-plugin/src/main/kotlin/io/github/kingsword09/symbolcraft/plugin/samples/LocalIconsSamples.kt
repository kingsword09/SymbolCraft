package io.github.kingsword09.symbolcraft.plugin.samples

import io.github.kingsword09.symbolcraft.plugin.SymbolCraftExtension

/**
 * Sample functions referenced from KDoc to demonstrate local icon configuration.
 */
@Suppress("unused") // Referenced via KDoc @sample
internal fun SymbolCraftExtension.localIconsIncludeSample() {
    localIcons(libraryName = "brand") {
        directory = "design/exported"
        include("brand/**/*.svg")
    }
}

@Suppress("unused") // Referenced via KDoc @sample
internal fun SymbolCraftExtension.localIconsExcludeSample() {
    localIcons {
        directory = "src/commonMain/resources/icons"
        exclude("legacy/**")
    }
}
