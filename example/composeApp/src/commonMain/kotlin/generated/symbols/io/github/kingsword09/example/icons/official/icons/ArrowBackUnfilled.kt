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

public val Icons.ArrowBackUnfilled: ImageVector
    get() {
        if (_arrowBackUnfilled != null) {
            return _arrowBackUnfilled!!
        }
        _arrowBackUnfilled = Builder(name = "ArrowBackUnfilled", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(313.0f, 520.0f)
                lineToRelative(196.0f, 196.0f)
                quadToRelative(12.0f, 12.0f, 11.5f, 28.0f)
                reflectiveQuadTo(508.0f, 772.0f)
                quadToRelative(-12.0f, 11.0f, -28.0f, 11.5f)
                reflectiveQuadTo(452.0f, 772.0f)
                lineTo(188.0f, 508.0f)
                quadToRelative(-6.0f, -6.0f, -8.5f, -13.0f)
                reflectiveQuadToRelative(-2.5f, -15.0f)
                quadToRelative(0.0f, -8.0f, 2.5f, -15.0f)
                reflectiveQuadToRelative(8.5f, -13.0f)
                lineToRelative(264.0f, -264.0f)
                quadToRelative(11.0f, -11.0f, 27.5f, -11.0f)
                reflectiveQuadToRelative(28.5f, 11.0f)
                quadToRelative(12.0f, 12.0f, 12.0f, 28.5f)
                reflectiveQuadTo(508.0f, 245.0f)
                lineTo(313.0f, 440.0f)
                horizontalLineToRelative(447.0f)
                quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
                reflectiveQuadTo(800.0f, 480.0f)
                quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
                reflectiveQuadTo(760.0f, 520.0f)
                lineTo(313.0f, 520.0f)
                close()
            }
        }
        .build()
        return _arrowBackUnfilled!!
    }

private var _arrowBackUnfilled: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.ArrowBackUnfilled, contentDescription = "")
    }
}
