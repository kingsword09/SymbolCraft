package io.github.kingsword09.symbolcraft.symbols.materialsymbols

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kingsword09.symbolcraft.symbols.MaterialSymbols

public val MaterialSymbols.PersonW400Outlined: ImageVector
    get() {
        if (_personW400Outlined != null) {
            return _personW400Outlined!!
        }
        _personW400Outlined = Builder(name = "PersonW400Outlined", defaultWidth = 48.0.dp,
                defaultHeight = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(480.0f, 479.0f)
                quadToRelative(-66.0f, 0.0f, -108.0f, -42.0f)
                reflectiveQuadToRelative(-42.0f, -108.0f)
                quadToRelative(0.0f, -66.0f, 42.0f, -108.0f)
                reflectiveQuadToRelative(108.0f, -42.0f)
                quadToRelative(66.0f, 0.0f, 108.0f, 42.0f)
                reflectiveQuadToRelative(42.0f, 108.0f)
                quadToRelative(0.0f, 66.0f, -42.0f, 108.0f)
                reflectiveQuadToRelative(-108.0f, 42.0f)
                close()
                moveTo(160.0f, 800.0f)
                verticalLineToRelative(-94.0f)
                quadToRelative(0.0f, -38.0f, 19.0f, -65.0f)
                reflectiveQuadToRelative(49.0f, -41.0f)
                quadToRelative(67.0f, -30.0f, 128.5f, -45.0f)
                reflectiveQuadTo(480.0f, 540.0f)
                quadToRelative(62.0f, 0.0f, 123.0f, 15.5f)
                reflectiveQuadToRelative(127.92f, 44.69f)
                quadToRelative(31.3f, 14.13f, 50.19f, 40.97f)
                quadTo(800.0f, 668.0f, 800.0f, 706.0f)
                verticalLineToRelative(94.0f)
                lineTo(160.0f, 800.0f)
                close()
                moveTo(220.0f, 740.0f)
                horizontalLineToRelative(520.0f)
                verticalLineToRelative(-34.0f)
                quadToRelative(0.0f, -16.0f, -9.5f, -30.5f)
                reflectiveQuadTo(707.0f, 654.0f)
                quadToRelative(-64.0f, -31.0f, -117.0f, -42.5f)
                reflectiveQuadTo(480.0f, 600.0f)
                quadToRelative(-57.0f, 0.0f, -111.0f, 11.5f)
                reflectiveQuadTo(252.0f, 654.0f)
                quadToRelative(-14.0f, 7.0f, -23.0f, 21.5f)
                reflectiveQuadToRelative(-9.0f, 30.5f)
                verticalLineToRelative(34.0f)
                close()
                moveTo(480.0f, 419.0f)
                quadToRelative(39.0f, 0.0f, 64.5f, -25.5f)
                reflectiveQuadTo(570.0f, 329.0f)
                quadToRelative(0.0f, -39.0f, -25.5f, -64.5f)
                reflectiveQuadTo(480.0f, 239.0f)
                quadToRelative(-39.0f, 0.0f, -64.5f, 25.5f)
                reflectiveQuadTo(390.0f, 329.0f)
                quadToRelative(0.0f, 39.0f, 25.5f, 64.5f)
                reflectiveQuadTo(480.0f, 419.0f)
                close()
                moveTo(480.0f, 329.0f)
                close()
                moveTo(480.0f, 740.0f)
                close()
            }
        }
        .build()
        return _personW400Outlined!!
    }

private var _personW400Outlined: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MaterialSymbols.PersonW400Outlined, contentDescription = "")
    }
}
