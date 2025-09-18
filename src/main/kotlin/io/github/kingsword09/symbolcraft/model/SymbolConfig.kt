package io.github.kingsword09.symbolcraft.model

import kotlinx.serialization.Serializable

@Serializable
data class SymbolConfig(
    val symbols: Map<String, List<SymbolStyle>>,
    val config: GenerationConfig = GenerationConfig()
) {
    val totalVariants: Int
        get() = symbols.values.sumOf { it.size }

    fun getConfigHash(): String {
        return toString().hashCode().toString()
    }
}

@Serializable
data class GenerationConfig(
    val cacheEnabled: Boolean = true,
    val cacheDirectory: String = "build/material-symbols-cache",
    val outputDirectory: String = "src/main/kotlin/generated/symbols",
    val forceRegenerate: Boolean = false,
    val minifyOutput: Boolean = true,
    val packageName: String = "io.github.kingsword09.symbolcraft.symbols"
)

