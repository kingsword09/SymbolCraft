package io.github.kingsword09.example.icons.official

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.kingsword09.example.icons.official.icons.ArrowBackFill
import io.github.kingsword09.example.icons.official.icons.ArrowBackOfficial
import io.github.kingsword09.example.icons.official.icons.HomeFill
import io.github.kingsword09.example.icons.official.icons.HomeOfficial
import io.github.kingsword09.example.icons.official.icons.PersonFill
import io.github.kingsword09.example.icons.official.icons.PersonOfficial
import io.github.kingsword09.example.icons.official.icons.SearchFill
import io.github.kingsword09.example.icons.official.icons.SearchOfficial
import io.github.kingsword09.example.icons.official.icons.SettingsFill
import io.github.kingsword09.example.icons.official.icons.SettingsOfficial
import kotlin.collections.List as ____KtList

public object Icons

private var __AllIcons: ____KtList<ImageVector>? = null

public val Icons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(ArrowBackFill, ArrowBackOfficial, HomeFill, HomeOfficial, PersonFill, PersonOfficial, SearchFill, SearchOfficial, SettingsFill, SettingsOfficial)
    return __AllIcons!!
  }
