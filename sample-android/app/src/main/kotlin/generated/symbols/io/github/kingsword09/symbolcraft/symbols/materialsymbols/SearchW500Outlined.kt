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

public val MaterialSymbols.SearchW500Outlined: ImageVector
    get() {
        if (_searchW500Outlined != null) {
            return _searchW500Outlined!!
        }
        _searchW500Outlined = Builder(name = "SearchW500Outlined", defaultWidth = 48.0.dp,
                defaultHeight = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(795.76f, 845.7f)
                lineTo(531.33f, 581.5f)
                quadToRelative(-29.76f, 25.26f, -69.6f, 39.41f)
                quadToRelative(-39.84f, 14.16f, -85.16f, 14.16f)
                quadToRelative(-109.84f, 0.0f, -185.96f, -76.2f)
                quadTo(114.5f, 482.67f, 114.5f, 375.0f)
                reflectiveQuadToRelative(76.2f, -183.87f)
                quadToRelative(76.19f, -76.2f, 184.37f, -76.2f)
                quadToRelative(108.17f, 0.0f, 183.86f, 76.2f)
                quadToRelative(75.7f, 76.2f, 75.7f, 184.02f)
                quadToRelative(0.0f, 43.33f, -13.64f, 82.97f)
                reflectiveQuadToRelative(-40.92f, 74.4f)
                lineTo(845.5f, 795.96f)
                lineToRelative(-49.74f, 49.74f)
                close()
                moveTo(375.65f, 566.93f)
                quadToRelative(79.73f, 0.0f, 135.29f, -56.24f)
                quadTo(566.5f, 454.45f, 566.5f, 375.0f)
                quadToRelative(0.0f, -79.45f, -55.6f, -135.69f)
                quadToRelative(-55.59f, -56.24f, -135.25f, -56.24f)
                quadToRelative(-80.49f, 0.0f, -136.76f, 56.24f)
                quadToRelative(-56.26f, 56.24f, -56.26f, 135.69f)
                quadToRelative(0.0f, 79.45f, 56.23f, 135.69f)
                quadToRelative(56.23f, 56.24f, 136.79f, 56.24f)
                close()
            }
        }
        .build()
        return _searchW500Outlined!!
    }

private var _searchW500Outlined: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MaterialSymbols.SearchW500Outlined, contentDescription = "")
    }
}
