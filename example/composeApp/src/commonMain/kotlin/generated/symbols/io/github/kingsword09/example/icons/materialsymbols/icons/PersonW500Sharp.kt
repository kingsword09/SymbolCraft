package io.github.kingsword09.example.icons.materialsymbols.icons

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
import io.github.kingsword09.example.icons.materialsymbols.Icons
import org.jetbrains.compose.ui.tooling.preview.Preview

public val Icons.PersonW500Sharp: ImageVector
    get() {
        if (_personW500Sharp != null) {
            return _personW500Sharp!!
        }
        _personW500Sharp = Builder(name = "PersonW500Sharp", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(480.0f, 475.93f)
                quadToRelative(-69.59f, 0.0f, -118.86f, -49.27f)
                quadToRelative(-49.27f, -49.27f, -49.27f, -118.86f)
                quadToRelative(0.0f, -69.58f, 49.27f, -118.74f)
                quadToRelative(49.27f, -49.15f, 118.86f, -49.15f)
                reflectiveQuadToRelative(118.86f, 49.15f)
                quadToRelative(49.27f, 49.16f, 49.27f, 118.74f)
                quadToRelative(0.0f, 69.59f, -49.27f, 118.86f)
                quadToRelative(-49.27f, 49.27f, -118.86f, 49.27f)
                close()
                moveTo(151.87f, 812.2f)
                verticalLineToRelative(-120.61f)
                quadToRelative(0.0f, -36.23f, 18.74f, -66.59f)
                quadToRelative(18.74f, -30.37f, 49.8f, -46.35f)
                quadToRelative(62.72f, -31.24f, 127.67f, -46.98f)
                quadToRelative(64.94f, -15.74f, 131.92f, -15.74f)
                quadToRelative(67.43f, 0.0f, 132.39f, 15.62f)
                quadToRelative(64.96f, 15.62f, 127.2f, 46.86f)
                quadToRelative(31.06f, 15.95f, 49.8f, 46.25f)
                reflectiveQuadToRelative(18.74f, 66.91f)
                verticalLineToRelative(120.63f)
                lineTo(151.87f, 812.2f)
                close()
                moveTo(242.87f, 721.2f)
                horizontalLineToRelative(474.26f)
                verticalLineToRelative(-28.42f)
                quadToRelative(0.0f, -10.77f, -5.5f, -19.58f)
                quadToRelative(-5.5f, -8.81f, -14.5f, -13.7f)
                quadToRelative(-52.56f, -26.04f, -106.85f, -39.3f)
                quadTo(536.0f, 606.93f, 480.0f, 606.93f)
                quadToRelative(-55.52f, 0.0f, -110.28f, 13.27f)
                quadToRelative(-54.76f, 13.26f, -106.85f, 39.3f)
                quadToRelative(-9.0f, 4.89f, -14.5f, 13.7f)
                quadToRelative(-5.5f, 8.81f, -5.5f, 19.58f)
                verticalLineToRelative(28.42f)
                close()
                moveTo(479.99f, 384.93f)
                quadToRelative(31.81f, 0.0f, 54.48f, -22.65f)
                quadToRelative(22.66f, -22.65f, 22.66f, -54.47f)
                quadToRelative(0.0f, -31.81f, -22.65f, -54.35f)
                quadToRelative(-22.66f, -22.55f, -54.47f, -22.55f)
                reflectiveQuadToRelative(-54.48f, 22.59f)
                quadToRelative(-22.66f, 22.59f, -22.66f, 54.3f)
                quadToRelative(0.0f, 31.82f, 22.65f, 54.48f)
                quadToRelative(22.66f, 22.65f, 54.47f, 22.65f)
                close()
                moveTo(480.0f, 307.8f)
                close()
                moveTo(480.0f, 721.2f)
                close()
            }
        }
        .build()
        return _personW500Sharp!!
    }

private var _personW500Sharp: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.PersonW500Sharp, contentDescription = "")
    }
}
