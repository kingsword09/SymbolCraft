package io.github.kingsword09.example.icons

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
import io.github.kingsword09.example.Icons
import org.jetbrains.compose.ui.tooling.preview.Preview

public val Icons.SearchW400Outlined: ImageVector
    get() {
        if (_searchW400Outlined != null) {
            return _searchW400Outlined!!
        }
        _searchW400Outlined = Builder(name = "SearchW400Outlined", defaultWidth = 48.0.dp, defaultHeight = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(796.0f, 839.0f)
                lineTo(533.0f, 576.0f)
                quadToRelative(-30.0f, 26.0f, -69.96f, 40.5f)
                quadTo(423.08f, 631.0f, 378.0f, 631.0f)
                quadToRelative(-108.16f, 0.0f, -183.08f, -75.0f)
                quadTo(120.0f, 481.0f, 120.0f, 375.0f)
                reflectiveQuadToRelative(75.0f, -181.0f)
                quadToRelative(75.0f, -75.0f, 181.5f, -75.0f)
                reflectiveQuadToRelative(181.0f, 75.0f)
                quadTo(632.0f, 269.0f, 632.0f, 375.15f)
                quadTo(632.0f, 418.0f, 618.0f, 458.0f)
                quadToRelative(-14.0f, 40.0f, -42.0f, 75.0f)
                lineToRelative(264.0f, 262.0f)
                lineToRelative(-44.0f, 44.0f)
                close()
                moveTo(377.0f, 571.0f)
                quadToRelative(81.25f, 0.0f, 138.13f, -57.5f)
                quadTo(572.0f, 456.0f, 572.0f, 375.0f)
                reflectiveQuadToRelative(-56.87f, -138.5f)
                quadTo(458.25f, 179.0f, 377.0f, 179.0f)
                quadToRelative(-82.08f, 0.0f, -139.54f, 57.5f)
                quadTo(180.0f, 294.0f, 180.0f, 375.0f)
                reflectiveQuadToRelative(57.46f, 138.5f)
                quadTo(294.92f, 571.0f, 377.0f, 571.0f)
                close()
            }
        }
        .build()
        return _searchW400Outlined!!
    }

private var _searchW400Outlined: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.SearchW400Outlined, contentDescription = "")
    }
}
