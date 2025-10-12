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

public val Icons.HomeW500Rounded: ImageVector
    get() {
        if (_homeW500Rounded != null) {
            return _homeW500Rounded!!
        }
        _homeW500Rounded = Builder(name = "HomeW500Rounded", defaultWidth = 48.0.dp, defaultHeight = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(222.15f, 777.85f)
                horizontalLineToRelative(143.78f)
                lineTo(365.93f, 560.0f)
                quadToRelative(0.0f, -14.42f, 9.83f, -24.24f)
                quadToRelative(9.82f, -9.83f, 24.24f, -9.83f)
                horizontalLineToRelative(160.0f)
                quadToRelative(14.42f, 0.0f, 24.24f, 9.83f)
                quadToRelative(9.83f, 9.82f, 9.83f, 24.24f)
                verticalLineToRelative(217.85f)
                horizontalLineToRelative(143.78f)
                verticalLineToRelative(-386.81f)
                lineTo(480.0f, 197.63f)
                lineTo(222.15f, 391.04f)
                verticalLineToRelative(386.81f)
                close()
                moveTo(154.02f, 777.85f)
                verticalLineToRelative(-386.89f)
                quadToRelative(0.0f, -16.12f, 7.14f, -30.53f)
                quadToRelative(7.13f, -14.42f, 19.97f, -23.93f)
                lineToRelative(257.85f, -193.57f)
                quadToRelative(17.83f, -13.67f, 40.86f, -13.67f)
                reflectiveQuadToRelative(41.18f, 13.67f)
                lineTo(778.87f, 336.5f)
                quadToRelative(12.95f, 9.51f, 20.15f, 23.93f)
                quadToRelative(7.2f, 14.41f, 7.2f, 30.53f)
                verticalLineToRelative(386.89f)
                quadToRelative(0.0f, 28.35f, -19.96f, 48.24f)
                reflectiveQuadToRelative(-48.41f, 19.89f)
                lineTo(562.63f, 845.98f)
                quadToRelative(-14.42f, 0.0f, -24.24f, -9.82f)
                quadToRelative(-9.82f, -9.82f, -9.82f, -24.25f)
                verticalLineToRelative(-220.48f)
                horizontalLineToRelative(-97.14f)
                verticalLineToRelative(220.48f)
                quadToRelative(0.0f, 14.43f, -9.82f, 24.25f)
                reflectiveQuadToRelative(-24.24f, 9.82f)
                lineTo(222.15f, 845.98f)
                quadToRelative(-28.35f, 0.0f, -48.24f, -19.89f)
                reflectiveQuadToRelative(-19.89f, -48.24f)
                close()
                moveTo(480.0f, 487.24f)
                close()
            }
        }
        .build()
        return _homeW500Rounded!!
    }

private var _homeW500Rounded: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.HomeW500Rounded, contentDescription = "")
    }
}
