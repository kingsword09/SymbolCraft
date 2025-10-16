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

public val Icons.SearchW700Outlined: ImageVector
    get() {
        if (_searchW700Outlined != null) {
            return _searchW700Outlined!!
        }
        _searchW700Outlined = Builder(name = "SearchW700Outlined", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(782.0f, 878.0f)
                lineTo(523.0f, 619.0f)
                quadToRelative(-29.0f, 20.0f, -67.5f, 32.0f)
                reflectiveQuadTo(372.0f, 663.0f)
                quadToRelative(-118.0f, 0.0f, -200.5f, -82.5f)
                reflectiveQuadTo(89.0f, 380.0f)
                quadToRelative(0.0f, -118.0f, 82.5f, -200.5f)
                reflectiveQuadTo(372.0f, 97.0f)
                quadToRelative(118.0f, 0.0f, 200.5f, 82.5f)
                reflectiveQuadTo(655.0f, 380.0f)
                quadToRelative(0.0f, 46.0f, -12.0f, 83.5f)
                reflectiveQuadTo(611.0f, 529.0f)
                lineToRelative(260.0f, 261.0f)
                lineToRelative(-89.0f, 88.0f)
                close()
                moveTo(372.0f, 537.0f)
                quadToRelative(66.0f, 0.0f, 111.5f, -45.5f)
                reflectiveQuadTo(529.0f, 380.0f)
                quadToRelative(0.0f, -66.0f, -45.5f, -111.5f)
                reflectiveQuadTo(372.0f, 223.0f)
                quadToRelative(-66.0f, 0.0f, -111.5f, 45.5f)
                reflectiveQuadTo(215.0f, 380.0f)
                quadToRelative(0.0f, 66.0f, 45.5f, 111.5f)
                reflectiveQuadTo(372.0f, 537.0f)
                close()
            }
        }
        .build()
        return _searchW700Outlined!!
    }

private var _searchW700Outlined: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.SearchW700Outlined, contentDescription = "")
    }
}
