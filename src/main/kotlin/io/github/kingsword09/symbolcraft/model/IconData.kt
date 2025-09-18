package io.github.kingsword09.symbolcraft.model

import kotlinx.serialization.Serializable

@Serializable
data class IconData(
    val name: String,
    val style: SymbolStyle,
    val svgPath: String,
    val viewBox: ViewBox,
    val sourceUrl: String,
    val generatedAt: Long = System.currentTimeMillis()
) {
    fun contentHash(): String {
        return ("$name-${style.signature}-${svgPath.hashCode()}").hashCode().toString()
    }
}

@Serializable
data class ViewBox(
    val width: Float = 24f,
    val height: Float = 24f,
    val minX: Float = 0f,
    val minY: Float = 0f
) {
    companion object {
        fun default() = ViewBox()
        
        fun parse(viewBoxString: String): ViewBox {
            val values = viewBoxString.trim()
                .split("""\s+""".toRegex())
                .mapNotNull { it.toFloatOrNull() }
                
            return if (values.size >= 4) {
                ViewBox(
                    width = values[2],
                    height = values[3],
                    minX = values[0], 
                    minY = values[1]
                )
            } else {
                default()
            }
        }
    }
}

