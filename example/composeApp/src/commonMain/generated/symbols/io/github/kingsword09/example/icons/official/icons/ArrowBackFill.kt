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

public val Icons.ArrowBackFill: ImageVector
    get() {
        if (_arrowBackFill != null) {
            return _arrowBackFill!!
        }
        _arrowBackFill = Builder(name = "ArrowBackFill", defaultWidth = 48.0.dp, defaultHeight = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(274.0f, 510.0f)
                lineToRelative(227.0f, 227.0f)
                quadToRelative(9.0f, 9.0f, 9.0f, 21.0f)
                reflectiveQuadToRelative(-9.0f, 21.0f)
                quadToRelative(-9.0f, 9.0f, -21.0f, 9.0f)
                reflectiveQuadToRelative(-21.0f, -9.0f)
                lineTo(181.0f, 501.0f)
                quadToRelative(-5.0f, -5.0f, -7.0f, -10.0f)
                reflectiveQuadToRelative(-2.0f, -11.0f)
                quadToRelative(0.0f, -6.0f, 2.0f, -11.0f)
                reflectiveQuadToRelative(7.0f, -10.0f)
                lineToRelative(278.0f, -278.0f)
                quadToRelative(9.0f, -9.0f, 21.0f, -9.0f)
                reflectiveQuadToRelative(21.0f, 9.0f)
                quadToRelative(9.0f, 9.0f, 9.0f, 21.0f)
                reflectiveQuadToRelative(-9.0f, 21.0f)
                lineTo(274.0f, 450.0f)
                horizontalLineToRelative(496.0f)
                quadToRelative(13.0f, 0.0f, 21.5f, 8.5f)
                reflectiveQuadTo(800.0f, 480.0f)
                quadToRelative(0.0f, 13.0f, -8.5f, 21.5f)
                reflectiveQuadTo(770.0f, 510.0f)
                lineTo(274.0f, 510.0f)
                close()
            }
        }
        .build()
        return _arrowBackFill!!
    }

private var _arrowBackFill: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.ArrowBackFill, contentDescription = "")
    }
}
