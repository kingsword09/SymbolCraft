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
        _searchW700Outlined = Builder(name = "SearchW700Outlined", defaultWidth = 48.0.dp, defaultHeight = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(795.0f, 867.0f)
                lineTo(526.0f, 599.0f)
                quadToRelative(-29.0f, 22.92f, -68.46f, 35.96f)
                quadTo(418.08f, 648.0f, 372.0f, 648.0f)
                quadToRelative(-115.16f, 0.0f, -195.08f, -80.0f)
                quadTo(97.0f, 488.0f, 97.0f, 375.0f)
                reflectiveQuadToRelative(80.0f, -193.0f)
                quadToRelative(80.0f, -80.0f, 193.5f, -80.0f)
                reflectiveQuadToRelative(193.0f, 80.0f)
                quadTo(643.0f, 262.0f, 643.0f, 375.15f)
                quadToRelative(0.0f, 44.85f, -12.5f, 83.35f)
                quadTo(618.0f, 497.0f, 593.0f, 531.0f)
                lineToRelative(270.0f, 268.0f)
                lineToRelative(-68.0f, 68.0f)
                close()
                moveTo(371.35f, 554.0f)
                quadToRelative(74.9f, 0.0f, 126.28f, -52.25f)
                quadTo(549.0f, 449.5f, 549.0f, 375.0f)
                reflectiveQuadToRelative(-51.52f, -126.75f)
                quadTo(445.96f, 196.0f, 371.35f, 196.0f)
                quadToRelative(-75.43f, 0.0f, -127.89f, 52.25f)
                quadTo(191.0f, 300.5f, 191.0f, 375.0f)
                reflectiveQuadToRelative(52.31f, 126.75f)
                quadTo(295.62f, 554.0f, 371.35f, 554.0f)
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
