# SymbolCraft 🎨

一个强大的 Gradle 插件，用于在 Android Compose 项目中按需生成 Material Symbols 图标，支持智能缓存和自定义样式。

## ✨ 特性

- 🚀 **按需生成** - 仅生成你实际使用的图标，减少 APK 体积
- 💾 **智能缓存** - 自动缓存下载的 SVG 文件，避免重复网络请求
- 🎯 **灵活配置** - 支持多种图标样式（Outlined、Rounded、Sharp）
- ⚡ **自动集成** - 与 Kotlin 编译任务自动关联
- 🔧 **简单易用** - 清晰的 DSL 配置接口

## 📦 安装

### 1. 添加插件到项目

在你的 `build.gradle.kts` 文件中：

```kotlin
plugins {
    id("io.github.kingsword09.symbolcraft") version "1.0.0"
}
```

### 2. 配置插件

```kotlin
materialSymbols {
    // 输出目录（相对于项目根目录）
    outputDirectory.set("src/main/kotlin/generated/symbols")
    
    // 生成的 Kotlin 包名
    packageName.set("com.yourcompany.app.icons")
    
    // 缓存目录（可选，默认：build/material-symbols-cache）
    cacheDirectory.set("build/material-symbols-cache")
    
    // 启用缓存（可选，默认：true）
    cacheEnabled.set(true)
    
    // 资源目录（可选，用于存储 SVG 文件）
    assetsDirectory.set("src/main/assets/material-symbols")
    
    // 配置图标
    symbol("home") {
        style(weight = 400, variant = ROUNDED, fill = UNFILLED)
    }
    
    symbol("search") {
        style(weight = 400, variant = OUTLINED, fill = UNFILLED)
        style(weight = 500, variant = OUTLINED, fill = FILLED)
    }
    
    symbol("person") {
        style(weight = 400, variant = OUTLINED, fill = UNFILLED)
    }
    
    symbol("settings") {
        style(weight = 300, variant = SHARP, fill = FILLED)
    }
}
```

## 🎯 使用方法

### 1. 生成图标

运行以下命令生成配置的图标：

```bash
./gradlew generateMaterialSymbols
```

### 2. 在 Compose 中使用

生成的图标可以直接在 Compose 代码中使用：

```kotlin
import com.yourcompany.app.icons.HomeW400Rounded
import com.yourcompany.app.icons.SearchW400Outlined
import com.yourcompany.app.icons.PersonW400Outlined
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun MyScreen() {
    Icon(
        imageVector = HomeW400Rounded,
        contentDescription = "Home"
    )
    
    Icon(
        imageVector = SearchW400Outlined,
        contentDescription = "Search"
    )
}
```

## 📋 配置选项

### 图标样式参数

- **weight**: 图标线条粗细（100-700）
  - 100: Thin
  - 200: Extra Light
  - 300: Light
  - 400: Regular（默认）
  - 500: Medium
  - 600: Semi Bold
  - 700: Bold

- **variant**: 图标风格
  - `OUTLINED`: 线条风格（默认）
  - `ROUNDED`: 圆角风格
  - `SHARP`: 尖角风格

- **fill**: 填充状态
  - `UNFILLED`: 空心（默认）
  - `FILLED`: 实心

### 生成的文件命名规则

图标文件名格式：`{IconName}W{Weight}{Variant}{Fill}.kt`

例如：
- `HomeW400Rounded.kt` - Home 图标，400 权重，圆角风格，未填充
- `SearchW500OutlinedFill.kt` - Search 图标，500 权重，线条风格，已填充

## 🛠 Gradle 任务

插件提供以下 Gradle 任务：

| 任务 | 描述 |
|------|------|
| `generateMaterialSymbols` | 生成配置的 Material Symbols 图标 |
| `cleanSymbolsCache` | 清理缓存的 SVG 文件 |
| `validateSymbolsConfig` | 验证图标配置的有效性 |
| `previewSymbols` | 预览将要生成的图标列表 |

## 🗂 项目结构

使用插件后，你的项目结构可能如下：

```
your-android-project/
├── build.gradle.kts
├── src/
│   └── main/
│       ├── kotlin/
│       │   ├── generated/
│       │   │   └── symbols/    # 生成的图标代码
│       │   │       ├── HomeW400Rounded.kt
│       │   │       ├── SearchW400Outlined.kt
│       │   │       └── ...
│       │   └── com/yourcompany/app/
│       │       └── MainActivity.kt
│       └── assets/
│           └── material-symbols/  # 缓存的 SVG 文件（可选）
└── build/
    └── material-symbols-cache/    # 临时缓存目录
```

## 🔄 缓存机制

- SVG 文件自动缓存到本地，避免重复下载
- 缓存有效期：7天（可配置）
- 全局缓存位置：`~/.gradle/caches/symbolcraft/`
- 项目缓存位置：`build/material-symbols-cache/`

## 📝 高级配置

### 使用外部 SVG 转换工具

如果你有自己的 SVG 到 Compose 转换工具，可以配置插件使用它：

```kotlin
materialSymbols {
    // 外部转换工具路径
    converterPath.set("/path/to/svg-to-compose")
    
    // 工具参数（{from}、{to}、{pkg} 会被自动替换）
    converterArgs.set(listOf(
        "--input", "{from}",
        "--output", "{to}",
        "--package", "{pkg}"
    ))
}
```

### 批量配置图标

对于大量图标，可以使用循环配置：

```kotlin
materialSymbols {
    val icons = listOf("home", "search", "person", "settings", "favorite")
    
    icons.forEach { iconName ->
        symbol(iconName) {
            style(weight = 400, variant = OUTLINED, fill = UNFILLED)
        }
    }
}
```

## 🚧 当前限制

- **SVG 转换**：当前使用简化的图标生成器，生成的是示例图标路径。完整的 SVG 路径解析功能正在开发中。
- **实时预览**：暂不支持在 IDE 中实时预览生成的图标
- **图标搜索**：需要手动在 [Material Symbols](https://fonts.google.com/icons) 网站查找图标名称

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

### 开发环境设置

1. 克隆仓库：
```bash
git clone https://github.com/kingsword09/SymbolCraft.git
cd SymbolCraft
```

2. 构建插件：
```bash
./gradlew build
```

3. 发布到本地 Maven 仓库测试：
```bash
./gradlew publishToMavenLocal
```

4. 运行示例应用：
```bash
cd sample-android
../gradlew assembleDebug
```

## 📄 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

## 🙏 致谢

- [Material Symbols](https://fonts.google.com/icons) - Google 提供的图标资源
- [Compose](https://developer.android.com/jetpack/compose) - Android 现代 UI 工具包

## 📮 联系方式

- GitHub: [@kingsword09](https://github.com/kingsword09)
- Issues: [GitHub Issues](https://github.com/kingsword09/SymbolCraft/issues)

---

**注意**：该项目仍在积极开发中，API 可能会有变化。建议在生产环境使用前进行充分测试。
