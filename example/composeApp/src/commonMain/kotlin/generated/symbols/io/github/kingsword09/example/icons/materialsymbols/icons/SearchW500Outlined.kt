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

public val Icons.SearchW500Outlined: ImageVector
    get() {
        if (_searchW500Outlined != null) {
            return _searchW500Outlined!!
        }
        _searchW500Outlined = Builder(name = "SearchW500Outlined", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(783.52f, 849.09f)
                lineTo(529.85f, 595.41f)
                quadToRelative(-29.76f, 23.05f, -68.64f, 36.57f)
                quadToRelative(-38.88f, 13.52f, -83.12f, 13.52f)
                quadToRelative(-111.16f, 0.0f, -188.33f, -77.17f)
                quadToRelative(-77.17f, -77.18f, -77.17f, -188.33f)
                reflectiveQuadToRelative(77.17f, -188.33f)
                quadToRelative(77.17f, -77.17f, 188.33f, -77.17f)
                quadToRelative(111.15f, 0.0f, 188.32f, 77.17f)
                quadToRelative(77.18f, 77.18f, 77.18f, 188.33f)
                quadToRelative(0.0f, 44.48f, -13.52f, 83.12f)
                quadToRelative(-13.53f, 38.64f, -36.57f, 68.16f)
                lineToRelative(253.91f, 254.15f)
                lineToRelative(-63.89f, 63.66f)
                close()
                moveTo(378.09f, 554.5f)
                quadToRelative(72.84f, 0.0f, 123.67f, -50.83f)
                quadToRelative(50.83f, -50.82f, 50.83f, -123.67f)
                reflectiveQuadToRelative(-50.83f, -123.67f)
                quadToRelative(-50.83f, -50.83f, -123.67f, -50.83f)
                quadToRelative(-72.85f, 0.0f, -123.68f, 50.83f)
                quadToRelative(-50.82f, 50.82f, -50.82f, 123.67f)
                reflectiveQuadToRelative(50.82f, 123.67f)
                quadToRelative(50.83f, 50.83f, 123.68f, 50.83f)
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
        Image(imageVector = Icons.SearchW500Outlined, contentDescription = "")
    }
}
