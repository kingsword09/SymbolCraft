package io.github.kingsword09.symbolcraft.symbols

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Material Symbol: HomeW400Rounded
 * 
 * 注意：这是一个临时占位符图标。
 * 要获得正确的图标，请确保 s2c 工具正确安装。
 * 
 * 安装方法：
 * 1. 访问 https://github.com/rafaeltonholo/svg-to-compose
 * 2. 下载对应平台的二进制文件
 * 3. 或通过项目自动下载功能获取
 */
val HomeW400Rounded: ImageVector
    get() {
        if (_HomeW400Rounded != null) {
            return _HomeW400Rounded!!
        }
        _HomeW400Rounded = ImageVector.Builder(
            name = "HomeW400Rounded",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // 警告图标路径（作为占位符）
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(1f, 21f)
                horizontalLineToRelative(22f)
                lineTo(12f, 2f)
                lineTo(1f, 21f)
                close()
                moveTo(13f, 18f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                close()
                moveTo(13f, 14f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-4f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(4f)
                close()
            }
        }.build()
        return _HomeW400Rounded!!
    }

private var _HomeW400Rounded: ImageVector? = null