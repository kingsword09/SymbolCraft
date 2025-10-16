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

public val Icons.HomeW400Outlined: ImageVector
    get() {
        if (_homeW400Outlined != null) {
            return _homeW400Outlined!!
        }
        _homeW400Outlined = Builder(name = "HomeW400Outlined", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(240.0f, 760.0f)
                horizontalLineToRelative(120.0f)
                verticalLineToRelative(-240.0f)
                horizontalLineToRelative(240.0f)
                verticalLineToRelative(240.0f)
                horizontalLineToRelative(120.0f)
                verticalLineToRelative(-360.0f)
                lineTo(480.0f, 220.0f)
                lineTo(240.0f, 400.0f)
                verticalLineToRelative(360.0f)
                close()
                moveTo(160.0f, 840.0f)
                verticalLineToRelative(-480.0f)
                lineToRelative(320.0f, -240.0f)
                lineToRelative(320.0f, 240.0f)
                verticalLineToRelative(480.0f)
                lineTo(520.0f, 840.0f)
                verticalLineToRelative(-240.0f)
                horizontalLineToRelative(-80.0f)
                verticalLineToRelative(240.0f)
                lineTo(160.0f, 840.0f)
                close()
                moveTo(480.0f, 490.0f)
                close()
            }
        }
        .build()
        return _homeW400Outlined!!
    }

private var _homeW400Outlined: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.HomeW400Outlined, contentDescription = "")
    }
}
