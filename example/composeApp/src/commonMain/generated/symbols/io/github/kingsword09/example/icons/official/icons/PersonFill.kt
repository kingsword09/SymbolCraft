package io.github.kingsword09.example.icons.official.icons

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
import io.github.kingsword09.example.icons.official.Icons

public val Icons.PersonFill: ImageVector
    get() {
        if (_personFill != null) {
            return _personFill!!
        }
        _personFill = Builder(name = "PersonFill", defaultWidth = 48.0.dp, defaultHeight = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(372.0f, 437.0f)
                quadToRelative(-42.0f, -42.0f, -42.0f, -108.0f)
                reflectiveQuadToRelative(42.0f, -108.0f)
                quadToRelative(42.0f, -42.0f, 108.0f, -42.0f)
                reflectiveQuadToRelative(108.0f, 42.0f)
                quadToRelative(42.0f, 42.0f, 42.0f, 108.0f)
                reflectiveQuadToRelative(-42.0f, 108.0f)
                quadToRelative(-42.0f, 42.0f, -108.0f, 42.0f)
                reflectiveQuadToRelative(-108.0f, -42.0f)
                close()
                moveTo(160.0f, 740.0f)
                verticalLineToRelative(-34.0f)
                quadToRelative(0.0f, -38.0f, 19.0f, -65.0f)
                reflectiveQuadToRelative(49.0f, -41.0f)
                quadToRelative(67.0f, -30.0f, 128.5f, -45.0f)
                reflectiveQuadTo(480.0f, 540.0f)
                quadToRelative(62.0f, 0.0f, 123.0f, 15.5f)
                reflectiveQuadTo(731.0f, 600.0f)
                quadToRelative(31.0f, 14.0f, 50.0f, 41.0f)
                reflectiveQuadToRelative(19.0f, 65.0f)
                verticalLineToRelative(34.0f)
                quadToRelative(0.0f, 25.0f, -17.5f, 42.5f)
                reflectiveQuadTo(740.0f, 800.0f)
                lineTo(220.0f, 800.0f)
                quadToRelative(-25.0f, 0.0f, -42.5f, -17.5f)
                reflectiveQuadTo(160.0f, 740.0f)
                close()
            }
        }
        .build()
        return _personFill!!
    }

private var _personFill: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.PersonFill, contentDescription = "")
    }
}
