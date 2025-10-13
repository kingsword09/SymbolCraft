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

public val Icons.SettingsFill1: ImageVector
    get() {
        if (_settingsFill1 != null) {
            return _settingsFill1!!
        }
        _settingsFill1 = Builder(name = "SettingsFill1", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(433.0f, 880.0f)
                quadToRelative(-27.0f, 0.0f, -46.5f, -18.0f)
                reflectiveQuadTo(363.0f, 818.0f)
                lineToRelative(-9.0f, -66.0f)
                quadToRelative(-13.0f, -5.0f, -24.5f, -12.0f)
                reflectiveQuadTo(307.0f, 725.0f)
                lineToRelative(-62.0f, 26.0f)
                quadToRelative(-25.0f, 11.0f, -50.0f, 2.0f)
                reflectiveQuadToRelative(-39.0f, -32.0f)
                lineToRelative(-47.0f, -82.0f)
                quadToRelative(-14.0f, -23.0f, -8.0f, -49.0f)
                reflectiveQuadToRelative(27.0f, -43.0f)
                lineToRelative(53.0f, -40.0f)
                quadToRelative(-1.0f, -7.0f, -1.0f, -13.5f)
                verticalLineToRelative(-27.0f)
                quadToRelative(0.0f, -6.5f, 1.0f, -13.5f)
                lineToRelative(-53.0f, -40.0f)
                quadToRelative(-21.0f, -17.0f, -27.0f, -43.0f)
                reflectiveQuadToRelative(8.0f, -49.0f)
                lineToRelative(47.0f, -82.0f)
                quadToRelative(14.0f, -23.0f, 39.0f, -32.0f)
                reflectiveQuadToRelative(50.0f, 2.0f)
                lineToRelative(62.0f, 26.0f)
                quadToRelative(11.0f, -8.0f, 23.0f, -15.0f)
                reflectiveQuadToRelative(24.0f, -12.0f)
                lineToRelative(9.0f, -66.0f)
                quadToRelative(4.0f, -26.0f, 23.5f, -44.0f)
                reflectiveQuadToRelative(46.5f, -18.0f)
                horizontalLineToRelative(94.0f)
                quadToRelative(27.0f, 0.0f, 46.5f, 18.0f)
                reflectiveQuadToRelative(23.5f, 44.0f)
                lineToRelative(9.0f, 66.0f)
                quadToRelative(13.0f, 5.0f, 24.5f, 12.0f)
                reflectiveQuadToRelative(22.5f, 15.0f)
                lineToRelative(62.0f, -26.0f)
                quadToRelative(25.0f, -11.0f, 50.0f, -2.0f)
                reflectiveQuadToRelative(39.0f, 32.0f)
                lineToRelative(47.0f, 82.0f)
                quadToRelative(14.0f, 23.0f, 8.0f, 49.0f)
                reflectiveQuadToRelative(-27.0f, 43.0f)
                lineToRelative(-53.0f, 40.0f)
                quadToRelative(1.0f, 7.0f, 1.0f, 13.5f)
                verticalLineToRelative(27.0f)
                quadToRelative(0.0f, 6.5f, -2.0f, 13.5f)
                lineToRelative(53.0f, 40.0f)
                quadToRelative(21.0f, 17.0f, 27.0f, 43.0f)
                reflectiveQuadToRelative(-8.0f, 49.0f)
                lineToRelative(-48.0f, 82.0f)
                quadToRelative(-14.0f, 23.0f, -39.0f, 32.0f)
                reflectiveQuadToRelative(-50.0f, -2.0f)
                lineToRelative(-60.0f, -26.0f)
                quadToRelative(-11.0f, 8.0f, -23.0f, 15.0f)
                reflectiveQuadToRelative(-24.0f, 12.0f)
                lineToRelative(-9.0f, 66.0f)
                quadToRelative(-4.0f, 26.0f, -23.5f, 44.0f)
                reflectiveQuadTo(527.0f, 880.0f)
                horizontalLineToRelative(-94.0f)
                close()
                moveTo(482.0f, 620.0f)
                quadToRelative(58.0f, 0.0f, 99.0f, -41.0f)
                reflectiveQuadToRelative(41.0f, -99.0f)
                quadToRelative(0.0f, -58.0f, -41.0f, -99.0f)
                reflectiveQuadToRelative(-99.0f, -41.0f)
                quadToRelative(-59.0f, 0.0f, -99.5f, 41.0f)
                reflectiveQuadTo(342.0f, 480.0f)
                quadToRelative(0.0f, 58.0f, 40.5f, 99.0f)
                reflectiveQuadToRelative(99.5f, 41.0f)
                close()
            }
        }
        .build()
        return _settingsFill1!!
    }

private var _settingsFill1: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.SettingsFill1, contentDescription = "")
    }
}
