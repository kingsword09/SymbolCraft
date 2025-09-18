package io.github.kingsword09.symbolcraft.symbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Material Symbol: HomeW400Rounded
 * Generated from SVG by SymbolCraft
 * 
 * 注意：这是一个示例图标，真实的SVG转换功能正在开发中。
 */
val HomeW400Rounded: ImageVector
    get() {
        if (_HomeW400Rounded != null) {
            return _HomeW400Rounded!!
        }
        _HomeW400Rounded = ImageVector.Builder(
            name = "HomeW400Rounded",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4f,
                pathFillType = PathFillType.NonZero
            ) {
                // Home icon path
moveTo(10f, 20f)
verticalLineTo(14f)
horizontalLineTo(14f)
verticalLineTo(20f)
horizontalLineTo(19f)
verticalLineTo(12f)
horizontalLineTo(22f)
lineTo(12f, 3f)
lineTo(2f, 12f)
horizontalLineTo(5f)
verticalLineTo(20f)
horizontalLineTo(10f)
close()
            }
        }.build()
        return _HomeW400Rounded!!
    }

private var _HomeW400Rounded: ImageVector? = null