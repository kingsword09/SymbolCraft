package io.github.kingsword09.example.icons.mdi

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.kingsword09.example.icons.mdi.icons.AbTestingMdi
import io.github.kingsword09.example.icons.mdi.icons.AbacusMdi
import kotlin.collections.List as ____KtList

public object Icons

private var __AllIcons: ____KtList<ImageVector>? = null

public val Icons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(AbTestingMdi, AbacusMdi)
    return __AllIcons!!
  }
