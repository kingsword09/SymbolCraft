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
        _homeW500Rounded = Builder(name = "HomeW500Rounded", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(242.87f, 757.13f)
                lineTo(354.5f, 757.13f)
                lineTo(354.5f, 560.0f)
                quadToRelative(0.0f, -19.15f, 13.17f, -32.33f)
                quadTo(380.85f, 514.5f, 400.0f, 514.5f)
                horizontalLineToRelative(160.0f)
                quadToRelative(19.15f, 0.0f, 32.33f, 13.17f)
                quadTo(605.5f, 540.85f, 605.5f, 560.0f)
                verticalLineToRelative(197.13f)
                horizontalLineToRelative(111.63f)
                verticalLineToRelative(-355.7f)
                lineTo(480.0f, 223.59f)
                lineTo(242.87f, 401.43f)
                verticalLineToRelative(355.7f)
                close()
                moveTo(151.87f, 757.13f)
                verticalLineToRelative(-355.7f)
                quadToRelative(0.0f, -21.57f, 9.58f, -40.87f)
                quadToRelative(9.57f, -19.3f, 26.72f, -31.97f)
                lineTo(425.3f, 150.74f)
                quadToRelative(24.11f, -18.39f, 54.7f, -18.39f)
                quadToRelative(30.59f, 0.0f, 54.7f, 18.39f)
                lineToRelative(237.13f, 177.85f)
                quadToRelative(17.15f, 12.67f, 26.72f, 31.97f)
                quadToRelative(9.58f, 19.3f, 9.58f, 40.87f)
                verticalLineToRelative(355.7f)
                quadToRelative(0.0f, 37.78f, -26.61f, 64.39f)
                reflectiveQuadToRelative(-64.39f, 26.61f)
                lineTo(563.59f, 848.13f)
                quadToRelative(-19.16f, 0.0f, -32.33f, -13.17f)
                quadToRelative(-13.17f, -13.18f, -13.17f, -32.33f)
                verticalLineToRelative(-200.72f)
                horizontalLineToRelative(-76.18f)
                verticalLineToRelative(200.72f)
                quadToRelative(0.0f, 19.15f, -13.17f, 32.33f)
                quadToRelative(-13.17f, 13.17f, -32.33f, 13.17f)
                lineTo(242.87f, 848.13f)
                quadToRelative(-37.78f, 0.0f, -64.39f, -26.61f)
                reflectiveQuadToRelative(-26.61f, -64.39f)
                close()
                moveTo(480.0f, 490.24f)
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
