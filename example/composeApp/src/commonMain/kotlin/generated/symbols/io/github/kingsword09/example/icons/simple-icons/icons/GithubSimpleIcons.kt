package io.github.kingsword09.example.icons.`simple-icons`.icons

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
import io.github.kingsword09.example.icons.`simple-icons`.Icons

public val Icons.GithubSimpleIcons: ImageVector
    get() {
        if (_githubSimpleIcons != null) {
            return _githubSimpleIcons!!
        }
        _githubSimpleIcons = Builder(name = "GithubSimpleIcons", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.0f, 0.30f)
                curveToRelative(-6.63f, 0.0f, -12.0f, 5.37f, -12.0f, 12.0f)
                curveToRelative(0.0f, 5.30f, 3.44f, 9.8f, 8.21f, 11.39f)
                curveToRelative(0.6f, 0.11f, 0.82f, -0.26f, 0.82f, -0.58f)
                curveToRelative(0.0f, -0.29f, -0.01f, -1.04f, -0.02f, -2.04f)
                curveToRelative(-3.34f, 0.72f, -4.04f, -1.61f, -4.04f, -1.61f)
                curveTo(4.42f, 18.07f, 3.63f, 17.7f, 3.63f, 17.7f)
                curveToRelative(-1.09f, -0.74f, 0.08f, -0.73f, 0.08f, -0.73f)
                curveToRelative(1.21f, 0.08f, 1.84f, 1.24f, 1.84f, 1.24f)
                curveToRelative(1.07f, 1.84f, 2.81f, 1.31f, 3.50f, 1.00f)
                curveToRelative(0.11f, -0.78f, 0.42f, -1.31f, 0.76f, -1.61f)
                curveToRelative(-2.67f, -0.3f, -5.47f, -1.33f, -5.47f, -5.93f)
                curveToRelative(0.0f, -1.31f, 0.47f, -2.38f, 1.24f, -3.22f)
                curveToRelative(-0.14f, -0.30f, -0.54f, -1.52f, 0.11f, -3.18f)
                curveToRelative(0.0f, 0.0f, 1.01f, -0.32f, 3.3f, 1.23f)
                curveToRelative(0.96f, -0.27f, 1.98f, -0.40f, 3.0f, -0.41f)
                curveToRelative(1.02f, 0.01f, 2.04f, 0.14f, 3.0f, 0.41f)
                curveToRelative(2.28f, -1.55f, 3.29f, -1.23f, 3.29f, -1.23f)
                curveToRelative(0.65f, 1.65f, 0.24f, 2.87f, 0.12f, 3.18f)
                curveToRelative(0.77f, 0.84f, 1.23f, 1.91f, 1.23f, 3.22f)
                curveToRelative(0.0f, 4.61f, -2.81f, 5.63f, -5.48f, 5.92f)
                curveToRelative(0.42f, 0.36f, 0.81f, 1.10f, 0.81f, 2.22f)
                curveToRelative(0.0f, 1.61f, -0.02f, 2.90f, -0.02f, 3.29f)
                curveToRelative(0.0f, 0.32f, 0.21f, 0.69f, 0.83f, 0.57f)
                curveTo(20.57f, 22.09f, 24.0f, 17.59f, 24.0f, 12.30f)
                curveToRelative(0.0f, -6.63f, -5.37f, -12.0f, -12.0f, -12.0f)
            }
        }
        .build()
        return _githubSimpleIcons!!
    }

private var _githubSimpleIcons: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.GithubSimpleIcons, contentDescription = "")
    }
}
