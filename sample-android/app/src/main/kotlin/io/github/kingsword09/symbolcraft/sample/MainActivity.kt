package io.github.kingsword09.symbolcraft.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 导入生成的图标
import io.github.kingsword09.symbolcraft.symbols.HomeW400Rounded
import io.github.kingsword09.symbolcraft.symbols.PersonW400Outlined
import io.github.kingsword09.symbolcraft.symbols.SearchW400Outlined
import io.github.kingsword09.symbolcraft.symbols.SearchW500OutlinedFill

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SampleScreen() }
    }
}

@Composable
fun SampleScreen() {
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "SymbolCraft 图标展示",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "生成的 Material Symbols 图标：",
                fontSize = 16.sp
            )
            
            // 展示生成的图标
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Home 图标
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = HomeW400Rounded,
                        contentDescription = "Home",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Home (Weight 400, Rounded)")
                }
                
                // Person 图标
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = PersonW400Outlined,
                        contentDescription = "Person",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text("Person (Weight 400, Outlined)")
                }
                
                // Search 图标 - Weight 400
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = SearchW400Outlined,
                        contentDescription = "Search",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text("Search (Weight 400, Outlined)")
                }
                
                // Search 图标 - Weight 500 Filled
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = SearchW500OutlinedFill,
                        contentDescription = "Search Filled",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text("Search (Weight 500, Filled)")
                }
            }
            
            Text(
                text = "注意：当前显示的是占位符图标（警告三角形）",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "需要实现真正的 SVG 路径解析器来显示正确的图标",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SamplePreview() {
    SampleScreen()
}
