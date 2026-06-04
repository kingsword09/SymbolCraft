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

public val Icons.SettingsFill: ImageVector
    get() {
        if (_settingsFill != null) {
            return _settingsFill!!
        }
        _settingsFill = Builder(name = "SettingsFill", defaultWidth = 48.0.dp, defaultHeight = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(421.0f, 880.0f)
                quadToRelative(-14.0f, 0.0f, -25.0f, -9.0f)
                reflectiveQuadToRelative(-13.0f, -23.0f)
                lineToRelative(-15.0f, -94.0f)
                quadToRelative(-19.0f, -7.0f, -40.0f, -19.0f)
                reflectiveQuadToRelative(-37.0f, -25.0f)
                lineToRelative(-86.0f, 40.0f)
                quadToRelative(-14.0f, 6.0f, -28.0f, 1.5f)
                reflectiveQuadTo(155.0f, 734.0f)
                lineTo(97.0f, 630.0f)
                quadToRelative(-8.0f, -13.0f, -4.5f, -27.0f)
                reflectiveQuadToRelative(15.5f, -23.0f)
                lineToRelative(80.0f, -59.0f)
                quadToRelative(-2.0f, -9.0f, -2.5f, -20.5f)
                reflectiveQuadTo(185.0f, 480.0f)
                quadToRelative(0.0f, -9.0f, 0.5f, -20.5f)
                reflectiveQuadTo(188.0f, 439.0f)
                lineToRelative(-80.0f, -59.0f)
                quadToRelative(-12.0f, -9.0f, -15.5f, -23.0f)
                reflectiveQuadToRelative(4.5f, -27.0f)
                lineToRelative(58.0f, -104.0f)
                quadToRelative(8.0f, -13.0f, 22.0f, -17.5f)
                reflectiveQuadToRelative(28.0f, 1.5f)
                lineToRelative(86.0f, 40.0f)
                quadToRelative(16.0f, -13.0f, 37.0f, -25.0f)
                reflectiveQuadToRelative(40.0f, -18.0f)
                lineToRelative(15.0f, -95.0f)
                quadToRelative(2.0f, -14.0f, 13.0f, -23.0f)
                reflectiveQuadToRelative(25.0f, -9.0f)
                horizontalLineToRelative(118.0f)
                quadToRelative(14.0f, 0.0f, 25.0f, 9.0f)
                reflectiveQuadToRelative(13.0f, 23.0f)
                lineToRelative(15.0f, 94.0f)
                quadToRelative(19.0f, 7.0f, 40.5f, 18.5f)
                reflectiveQuadTo(669.0f, 250.0f)
                lineToRelative(86.0f, -40.0f)
                quadToRelative(14.0f, -6.0f, 27.5f, -1.5f)
                reflectiveQuadTo(804.0f, 226.0f)
                lineToRelative(59.0f, 104.0f)
                quadToRelative(8.0f, 13.0f, 4.5f, 27.5f)
                reflectiveQuadTo(852.0f, 380.0f)
                lineToRelative(-80.0f, 57.0f)
                quadToRelative(2.0f, 10.0f, 2.5f, 21.5f)
                reflectiveQuadToRelative(0.5f, 21.5f)
                quadToRelative(0.0f, 10.0f, -0.5f, 21.0f)
                reflectiveQuadToRelative(-2.5f, 21.0f)
                lineToRelative(80.0f, 58.0f)
                quadToRelative(12.0f, 8.0f, 15.5f, 22.5f)
                reflectiveQuadTo(863.0f, 630.0f)
                lineToRelative(-58.0f, 104.0f)
                quadToRelative(-8.0f, 13.0f, -22.0f, 17.5f)
                reflectiveQuadToRelative(-28.0f, -1.5f)
                lineToRelative(-86.0f, -40.0f)
                quadToRelative(-16.0f, 13.0f, -36.5f, 25.5f)
                reflectiveQuadTo(592.0f, 754.0f)
                lineToRelative(-15.0f, 94.0f)
                quadToRelative(-2.0f, 14.0f, -13.0f, 23.0f)
                reflectiveQuadToRelative(-25.0f, 9.0f)
                lineTo(421.0f, 880.0f)
                close()
                moveTo(480.0f, 610.0f)
                quadToRelative(54.0f, 0.0f, 92.0f, -38.0f)
                reflectiveQuadToRelative(38.0f, -92.0f)
                quadToRelative(0.0f, -54.0f, -38.0f, -92.0f)
                reflectiveQuadToRelative(-92.0f, -38.0f)
                quadToRelative(-54.0f, 0.0f, -92.0f, 38.0f)
                reflectiveQuadToRelative(-38.0f, 92.0f)
                quadToRelative(0.0f, 54.0f, 38.0f, 92.0f)
                reflectiveQuadToRelative(92.0f, 38.0f)
                close()
            }
        }
        .build()
        return _settingsFill!!
    }

private var _settingsFill: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.SettingsFill, contentDescription = "")
    }
}
