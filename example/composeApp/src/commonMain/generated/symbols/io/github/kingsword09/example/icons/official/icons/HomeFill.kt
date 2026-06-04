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

public val Icons.HomeFill: ImageVector
    get() {
        if (_homeFill != null) {
            return _homeFill!!
        }
        _homeFill = Builder(name = "HomeFill", defaultWidth = 48.0.dp, defaultHeight = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
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
                lineTo(590.0f, 840.0f)
                quadToRelative(-12.75f, 0.0f, -21.37f, -8.63f)
                quadTo(560.0f, 822.75f, 560.0f, 810.0f)
                verticalLineToRelative(-220.0f)
                quadToRelative(0.0f, -12.75f, -8.62f, -21.38f)
                quadTo(542.75f, 560.0f, 530.0f, 560.0f)
                lineTo(430.0f, 560.0f)
                quadToRelative(-12.75f, 0.0f, -21.37f, 8.62f)
                quadTo(400.0f, 577.25f, 400.0f, 590.0f)
                verticalLineToRelative(220.0f)
                quadToRelative(0.0f, 12.75f, -8.62f, 21.37f)
                quadTo(382.75f, 840.0f, 370.0f, 840.0f)
                lineTo(220.0f, 840.0f)
                quadToRelative(-24.75f, 0.0f, -42.37f, -17.63f)
                quadTo(160.0f, 804.75f, 160.0f, 780.0f)
                close()
            }
        }
        .build()
        return _homeFill!!
    }

private var _homeFill: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.HomeFill, contentDescription = "")
    }
}
