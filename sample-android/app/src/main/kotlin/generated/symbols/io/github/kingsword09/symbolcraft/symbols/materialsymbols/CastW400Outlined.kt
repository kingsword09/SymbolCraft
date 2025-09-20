package io.github.kingsword09.symbolcraft.symbols.materialsymbols

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
import io.github.kingsword09.symbolcraft.symbols.MaterialSymbols

public val MaterialSymbols.CastW400Outlined: ImageVector
    get() {
        if (_castW400Outlined != null) {
            return _castW400Outlined!!
        }
        _castW400Outlined = Builder(name = "CastW400Outlined", defaultWidth = 48.0.dp, defaultHeight
                = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(481.0f, 480.0f)
                close()
                moveTo(821.0f, 800.0f)
                lineTo(601.0f, 800.0f)
                quadToRelative(0.0f, -15.0f, -1.0f, -30.0f)
                reflectiveQuadToRelative(-3.0f, -30.0f)
                horizontalLineToRelative(224.0f)
                verticalLineToRelative(-520.0f)
                lineTo(141.0f, 220.0f)
                verticalLineToRelative(60.0f)
                quadToRelative(-15.0f, -2.0f, -30.0f, -3.0f)
                reflectiveQuadToRelative(-30.0f, -1.0f)
                verticalLineToRelative(-56.0f)
                quadToRelative(0.0f, -24.75f, 17.63f, -42.38f)
                quadTo(116.25f, 160.0f, 141.0f, 160.0f)
                horizontalLineToRelative(680.0f)
                quadToRelative(24.75f, 0.0f, 42.38f, 17.62f)
                quadTo(881.0f, 195.25f, 881.0f, 220.0f)
                verticalLineToRelative(520.0f)
                quadToRelative(0.0f, 24.75f, -17.62f, 42.37f)
                quadTo(845.75f, 800.0f, 821.0f, 800.0f)
                close()
                moveTo(81.0f, 800.0f)
                verticalLineToRelative(-104.0f)
                quadToRelative(41.67f, 0.0f, 70.83f, 30.33f)
                quadTo(181.0f, 756.67f, 181.0f, 800.0f)
                lineTo(81.0f, 800.0f)
                close()
                moveTo(281.0f, 800.0f)
                quadToRelative(0.0f, -84.66f, -58.0f, -144.33f)
                quadTo(165.0f, 596.0f, 81.0f, 596.0f)
                verticalLineToRelative(-60.0f)
                quadToRelative(108.64f, 0.0f, 184.32f, 77.5f)
                quadTo(341.0f, 691.0f, 341.0f, 800.0f)
                horizontalLineToRelative(-60.0f)
                close()
                moveTo(441.0f, 800.0f)
                quadToRelative(0.0f, -75.0f, -28.0f, -141.5f)
                reflectiveQuadToRelative(-77.0f, -116.0f)
                quadToRelative(-49.0f, -49.5f, -114.5f, -78.0f)
                reflectiveQuadTo(81.0f, 436.0f)
                verticalLineToRelative(-60.0f)
                quadToRelative(87.0f, 0.0f, 163.5f, 33.5f)
                reflectiveQuadToRelative(133.5f, 91.0f)
                quadToRelative(57.0f, 57.5f, 90.0f, 135.0f)
                reflectiveQuadTo(501.0f, 800.0f)
                horizontalLineToRelative(-60.0f)
                close()
            }
        }
        .build()
        return _castW400Outlined!!
    }

private var _castW400Outlined: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MaterialSymbols.CastW400Outlined, contentDescription = "")
    }
}
