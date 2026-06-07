package io.github.kingsword09.symbolcraft.model

import kotlin.test.Test
import kotlin.test.assertEquals

class MaterialSymbolsConfigTest {

    @Test
    fun `filled Material Symbols keep Google Fonts fill1 URL suffix`() {
        val config =
            MaterialSymbolsConfig(
                weight = SymbolWeight.W400,
                variant = SymbolVariant.OUTLINED,
                fill = SymbolFill.FILLED,
            )

        assertEquals(
            "https://fonts.gstatic.com/s/i/short-term/release/materialsymbolsoutlined/home/fill1/24px.svg",
            config.buildUrl("home"),
        )
    }

    @Test
    fun `filled Material Symbols use Fill in generated name signature`() {
        val outlinedConfig =
            MaterialSymbolsConfig(
                weight = SymbolWeight.W400,
                variant = SymbolVariant.OUTLINED,
                fill = SymbolFill.FILLED,
            )
        val roundedConfig =
            MaterialSymbolsConfig(
                weight = SymbolWeight.W500,
                variant = SymbolVariant.ROUNDED,
                fill = SymbolFill.FILLED,
            )

        assertEquals("W400OutlinedFill", outlinedConfig.getSignature())
        assertEquals("W500RoundedFill", roundedConfig.getSignature())
    }

    @Test
    fun `unfilled Material Symbols signatures stay unchanged`() {
        val config =
            MaterialSymbolsConfig(
                weight = SymbolWeight.W400,
                variant = SymbolVariant.OUTLINED,
                fill = SymbolFill.UNFILLED,
            )

        assertEquals("W400Outlined", config.getSignature())
    }
}
