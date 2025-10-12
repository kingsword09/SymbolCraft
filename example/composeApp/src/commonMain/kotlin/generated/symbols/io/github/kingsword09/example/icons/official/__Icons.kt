package io.github.kingsword09.example.icons.official

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.kingsword09.example.icons.official.icons.ArrowBackFilled
import io.github.kingsword09.example.icons.official.icons.ArrowBackUnfilled
import io.github.kingsword09.example.icons.official.icons.HomeFilled
import io.github.kingsword09.example.icons.official.icons.HomeUnfilled
import io.github.kingsword09.example.icons.official.icons.PersonFilled
import io.github.kingsword09.example.icons.official.icons.PersonUnfilled
import io.github.kingsword09.example.icons.official.icons.SearchFilled
import io.github.kingsword09.example.icons.official.icons.SearchUnfilled
import io.github.kingsword09.example.icons.official.icons.SettingsFilled
import io.github.kingsword09.example.icons.official.icons.SettingsUnfilled
import kotlin.collections.List as ____KtList

public object Icons

private var __AllIcons: ____KtList<ImageVector>? = null

public val Icons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(ArrowBackFilled, ArrowBackUnfilled, HomeFilled, HomeUnfilled, PersonFilled, PersonUnfilled, SearchFilled, SearchUnfilled, SettingsFilled, SettingsUnfilled)
    return __AllIcons!!
  }
