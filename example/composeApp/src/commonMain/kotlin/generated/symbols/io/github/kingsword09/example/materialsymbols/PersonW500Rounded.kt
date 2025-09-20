package io.github.kingsword09.example.materialsymbols

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.github.kingsword09.example.MaterialSymbols

public val MaterialSymbols.PersonW500Rounded: ImageVector
    get() {
        if (_personW500Rounded != null) {
            return _personW500Rounded!!
        }
        _personW500Rounded = Builder(name = "PersonW500Rounded", defaultWidth = 48.0.dp,
                defaultHeight = 48.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(479.95f, 475.89f)
                quadToRelative(-68.68f, 0.0f, -112.3f, -43.62f)
                quadToRelative(-43.63f, -43.63f, -43.63f, -112.31f)
                reflectiveQuadToRelative(43.63f, -112.35f)
                quadToRelative(43.62f, -43.68f, 112.3f, -43.68f)
                reflectiveQuadToRelative(112.47f, 43.68f)
                quadToRelative(43.8f, 43.67f, 43.8f, 112.35f)
                quadToRelative(0.0f, 68.68f, -43.8f, 112.31f)
                quadToRelative(-43.79f, 43.62f, -112.47f, 43.62f)
                close()
                moveTo(154.02f, 740.96f)
                verticalLineToRelative(-32.33f)
                quadToRelative(0.0f, -39.51f, 19.92f, -67.99f)
                quadToRelative(19.91f, -28.49f, 51.43f, -43.27f)
                quadToRelative(67.48f, -30.24f, 129.69f, -45.36f)
                quadToRelative(62.2f, -15.12f, 124.88f, -15.12f)
                quadToRelative(63.13f, 0.0f, 124.79f, 15.62f)
                quadToRelative(61.66f, 15.62f, 128.82f, 45.05f)
                quadToRelative(32.88f, 14.6f, 52.78f, 43.0f)
                quadToRelative(19.89f, 28.4f, 19.89f, 68.07f)
                verticalLineToRelative(32.33f)
                quadToRelative(0.0f, 28.09f, -19.96f, 48.11f)
                reflectiveQuadToRelative(-48.41f, 20.02f)
                horizontalLineToRelative(-515.7f)
                quadToRelative(-28.35f, 0.0f, -48.24f, -20.02f)
                reflectiveQuadToRelative(-19.89f, -48.11f)
                close()
                moveTo(222.15f, 740.96f)
                horizontalLineToRelative(515.7f)
                verticalLineToRelative(-31.37f)
                quadToRelative(0.0f, -15.85f, -9.5f, -30.22f)
                quadToRelative(-9.5f, -14.38f, -23.5f, -21.3f)
                quadToRelative(-63.05f, -30.29f, -115.45f, -41.55f)
                quadToRelative(-52.4f, -11.26f, -109.52f, -11.26f)
                quadToRelative(-56.64f, 0.0f, -110.28f, 11.26f)
                reflectiveQuadToRelative(-115.35f, 41.51f)
                quadToRelative(-14.1f, 6.93f, -23.1f, 21.31f)
                quadToRelative(-9.0f, 14.39f, -9.0f, 30.25f)
                verticalLineToRelative(31.37f)
                close()
                moveTo(479.95f, 407.76f)
                quadToRelative(38.09f, 0.0f, 63.0f, -24.86f)
                quadToRelative(24.9f, -24.87f, 24.9f, -62.98f)
                quadToRelative(0.0f, -38.21f, -24.86f, -63.03f)
                quadToRelative(-24.85f, -24.82f, -62.94f, -24.82f)
                quadToRelative(-38.09f, 0.0f, -63.0f, 24.83f)
                quadToRelative(-24.9f, 24.84f, -24.9f, 62.9f)
                quadToRelative(0.0f, 38.17f, 24.86f, 63.06f)
                quadToRelative(24.85f, 24.9f, 62.94f, 24.9f)
                close()
                moveTo(480.0f, 319.91f)
                close()
                moveTo(480.0f, 740.96f)
                close()
            }
        }
        .build()
        return _personW500Rounded!!
    }

private var _personW500Rounded: ImageVector? = null
