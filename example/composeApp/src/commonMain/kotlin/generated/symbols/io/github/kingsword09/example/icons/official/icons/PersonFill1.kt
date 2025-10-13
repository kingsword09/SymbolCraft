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
import androidx.compose.ui.unit.dp
import io.github.kingsword09.example.icons.official.Icons
import org.jetbrains.compose.ui.tooling.preview.Preview

public val Icons.PersonFill1: ImageVector
    get() {
        if (_personFill1 != null) {
            return _personFill1!!
        }
        _personFill1 = Builder(name = "PersonFill1", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(480.0f, 480.0f)
                quadToRelative(-66.0f, 0.0f, -113.0f, -47.0f)
                reflectiveQuadToRelative(-47.0f, -113.0f)
                quadToRelative(0.0f, -66.0f, 47.0f, -113.0f)
                reflectiveQuadToRelative(113.0f, -47.0f)
                quadToRelative(66.0f, 0.0f, 113.0f, 47.0f)
                reflectiveQuadToRelative(47.0f, 113.0f)
                quadToRelative(0.0f, 66.0f, -47.0f, 113.0f)
                reflectiveQuadToRelative(-113.0f, 47.0f)
                close()
                moveTo(160.0f, 720.0f)
                verticalLineToRelative(-32.0f)
                quadToRelative(0.0f, -34.0f, 17.5f, -62.5f)
                reflectiveQuadTo(224.0f, 582.0f)
                quadToRelative(62.0f, -31.0f, 126.0f, -46.5f)
                reflectiveQuadTo(480.0f, 520.0f)
                quadToRelative(66.0f, 0.0f, 130.0f, 15.5f)
                reflectiveQuadTo(736.0f, 582.0f)
                quadToRelative(29.0f, 15.0f, 46.5f, 43.5f)
                reflectiveQuadTo(800.0f, 688.0f)
                verticalLineToRelative(32.0f)
                quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
                reflectiveQuadTo(720.0f, 800.0f)
                lineTo(240.0f, 800.0f)
                quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
                reflectiveQuadTo(160.0f, 720.0f)
                close()
            }
        }
        .build()
        return _personFill1!!
    }

private var _personFill1: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.PersonFill1, contentDescription = "")
    }
}
