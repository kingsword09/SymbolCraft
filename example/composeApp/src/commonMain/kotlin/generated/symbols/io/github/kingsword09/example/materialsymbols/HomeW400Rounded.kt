package io.github.kingsword09.example.materialsymbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.github.kingsword09.example.MaterialSymbols

public val MaterialSymbols.HomeW400Rounded: ImageVector
    get() {
        if (_homeW400Rounded != null) {
            return _homeW400Rounded!!
        }
        _homeW400Rounded = Builder(name = "HomeW400Rounded", defaultWidth = 48.0.dp, defaultHeight =
                48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(220.0f, 780.0f)
                horizontalLineToRelative(150.0f)
                verticalLineToRelative(-220.0f)
                quadToRelative(0.0f, -12.75f, 8.63f, -21.38f)
                quadTo(387.25f, 530.0f, 400.0f, 530.0f)
                horizontalLineToRelative(160.0f)
                quadToRelative(12.75f, 0.0f, 21.38f, 8.62f)
                quadTo(590.0f, 547.25f, 590.0f, 560.0f)
                verticalLineToRelative(220.0f)
                horizontalLineToRelative(150.0f)
                verticalLineToRelative(-390.0f)
                lineTo(480.0f, 195.0f)
                lineTo(220.0f, 390.0f)
                verticalLineToRelative(390.0f)
                close()
                moveTo(160.0f, 780.0f)
                verticalLineToRelative(-390.0f)
                quadToRelative(0.0f, -14.25f, 6.38f, -27.0f)
                quadToRelative(6.37f, -12.75f, 17.62f, -21.0f)
                lineToRelative(260.0f, -195.0f)
                quadToRelative(15.68f, -12.0f, 35.84f, -12.0f)
                quadTo(500.0f, 135.0f, 516.0f, 147.0f)
                lineToRelative(260.0f, 195.0f)
                quadToRelative(11.25f, 8.25f, 17.63f, 21.0f)
                quadToRelative(6.37f, 12.75f, 6.37f, 27.0f)
                verticalLineToRelative(390.0f)
                quadToRelative(0.0f, 24.75f, -17.62f, 42.37f)
                quadTo(764.75f, 840.0f, 740.0f, 840.0f)
                lineTo(560.0f, 840.0f)
                quadToRelative(-12.75f, 0.0f, -21.37f, -8.63f)
                quadTo(530.0f, 822.75f, 530.0f, 810.0f)
                verticalLineToRelative(-220.0f)
                lineTo(430.0f, 590.0f)
                verticalLineToRelative(220.0f)
                quadToRelative(0.0f, 12.75f, -8.62f, 21.37f)
                quadTo(412.75f, 840.0f, 400.0f, 840.0f)
                lineTo(220.0f, 840.0f)
                quadToRelative(-24.75f, 0.0f, -42.37f, -17.63f)
                quadTo(160.0f, 804.75f, 160.0f, 780.0f)
                close()
                moveTo(480.0f, 487.0f)
                close()
            }
        }
        .build()
        return _homeW400Rounded!!
    }

private var _homeW400Rounded: ImageVector? = null
