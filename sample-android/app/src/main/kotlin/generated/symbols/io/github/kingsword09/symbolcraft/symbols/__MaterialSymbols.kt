package io.github.kingsword09.symbolcraft.symbols

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.kingsword09.symbolcraft.symbols.materialsymbols.HomeW400Rounded
import io.github.kingsword09.symbolcraft.symbols.materialsymbols.PersonW400Outlined
import io.github.kingsword09.symbolcraft.symbols.materialsymbols.SearchW400Outlined
import io.github.kingsword09.symbolcraft.symbols.materialsymbols.SearchW500OutlinedFill
import kotlin.collections.List as ____KtList

public object MaterialSymbols

private var __AllIcons: ____KtList<ImageVector>? = null

public val MaterialSymbols.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(HomeW400Rounded, PersonW400Outlined, SearchW400Outlined,
        SearchW500OutlinedFill)
    return __AllIcons!!
  }
