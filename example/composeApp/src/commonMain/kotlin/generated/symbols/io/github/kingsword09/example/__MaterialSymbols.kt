package io.github.kingsword09.example

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.kingsword09.example.materialsymbols.HomeW400Outlined
import io.github.kingsword09.example.materialsymbols.HomeW400OutlinedFill
import io.github.kingsword09.example.materialsymbols.HomeW400Rounded
import io.github.kingsword09.example.materialsymbols.HomeW500Rounded
import io.github.kingsword09.example.materialsymbols.PersonW500Outlined
import io.github.kingsword09.example.materialsymbols.PersonW500Rounded
import io.github.kingsword09.example.materialsymbols.PersonW500Sharp
import io.github.kingsword09.example.materialsymbols.SearchW400Outlined
import io.github.kingsword09.example.materialsymbols.SearchW500Outlined
import io.github.kingsword09.example.materialsymbols.SearchW700Outlined
import io.github.kingsword09.example.materialsymbols.SettingsW400Outlined
import io.github.kingsword09.example.materialsymbols.SettingsW500RoundedFill
import kotlin.collections.List as ____KtList

public object MaterialSymbols

private var __AllIcons: ____KtList<ImageVector>? = null

public val MaterialSymbols.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(HomeW400Outlined, HomeW400OutlinedFill, HomeW400Rounded, HomeW500Rounded,
        PersonW500Outlined, PersonW500Rounded, PersonW500Sharp, SearchW400Outlined,
        SearchW500Outlined, SearchW700Outlined, SettingsW400Outlined, SettingsW500RoundedFill)
    return __AllIcons!!
  }
