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
 * Material Symbol: SearchW500OutlinedFill
 * Generated from SVG by SymbolCraft
 * 
 * 注意：这是一个示例图标，真实的SVG转换功能正在开发中。
 */
val SearchW500OutlinedFill: ImageVector
    get() {
        if (_SearchW500OutlinedFill != null) {
            return _SearchW500OutlinedFill!!
        }
        _SearchW500OutlinedFill = ImageVector.Builder(
            name = "SearchW500OutlinedFill",
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
                // Search icon path
moveTo(15.5f, 14f)
horizontalLineToRelative(-0.79f)
lineToRelative(-0.28f, -0.27f)
curveToRelative(0.98f, -1.14f, 1.57f, -2.62f, 1.57f, -4.23f)
curveToRelative(0f, -3.59f, -2.91f, -6.5f, -6.5f, -6.5f)
reflectiveCurveTo(3f, 5.91f, 3f, 9.5f)
reflectiveCurveTo(5.91f, 16f, 9.5f, 16f)
curveToRelative(1.61f, 0f, 3.09f, -0.59f, 4.23f, -1.57f)
lineToRelative(0.27f, 0.28f)
verticalLineToRelative(0.79f)
lineToRelative(5f, 4.99f)
lineTo(20.49f, 19f)
lineToRelative(-4.99f, -5f)
close()
moveTo(9.5f, 14f)
curveToRelative(-2.49f, 0f, -4.5f, -2.01f, -4.5f, -4.5f)
reflectiveCurveTo(7.01f, 5f, 9.5f, 5f)
reflectiveCurveTo(14f, 7.01f, 14f, 9.5f)
reflectiveCurveTo(11.99f, 14f, 9.5f, 14f)
close()
            }
        }.build()
        return _SearchW500OutlinedFill!!
    }

private var _SearchW500OutlinedFill: ImageVector? = null