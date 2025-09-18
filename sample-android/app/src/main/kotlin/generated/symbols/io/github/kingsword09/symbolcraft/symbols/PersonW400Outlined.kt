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
 * Material Symbol: PersonW400Outlined
 * Generated from SVG by SymbolCraft
 * 
 * 注意：这是一个示例图标，真实的SVG转换功能正在开发中。
 */
val PersonW400Outlined: ImageVector
    get() {
        if (_PersonW400Outlined != null) {
            return _PersonW400Outlined!!
        }
        _PersonW400Outlined = ImageVector.Builder(
            name = "PersonW400Outlined",
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
                // Person icon path
moveTo(12f, 12f)
curveToRelative(2.21f, 0f, 4f, -1.79f, 4f, -4f)
reflectiveCurveToRelative(-1.79f, -4f, -4f, -4f)
reflectiveCurveToRelative(-4f, 1.79f, -4f, 4f)
reflectiveCurveToRelative(1.79f, 4f, 4f, 4f)
close()
moveTo(12f, 14f)
curveToRelative(-2.67f, 0f, -8f, 1.34f, -8f, 4f)
verticalLineTo(20f)
horizontalLineToRelative(16f)
verticalLineTo(18f)
curveToRelative(0f, -2.66f, -5.33f, -4f, -8f, -4f)
close()
            }
        }.build()
        return _PersonW400Outlined!!
    }

private var _PersonW400Outlined: ImageVector? = null