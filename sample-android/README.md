# SymbolCraft 示例应用

这是一个展示 SymbolCraft 插件功能的 Android 示例应用。

## 📱 功能展示

该示例应用演示了如何：
- 配置 SymbolCraft 插件
- 生成 Material Symbols 图标
- 在 Jetpack Compose 中使用生成的图标

## 🚀 快速开始

### 1. 构建插件

首先，从项目根目录构建 SymbolCraft 插件：

```bash
cd ..
./gradlew build
```

### 2. 生成图标

生成配置的 Material Symbols 图标：

```bash
./gradlew generateMaterialSymbols
```

### 3. 运行应用

构建并运行示例应用：

```bash
./gradlew assembleDebug
```

或者直接安装到设备：

```bash
./gradlew installDebug
```

## 📝 配置示例

在 `app/build.gradle.kts` 中的配置：

```kotlin
materialSymbols {
    outputDirectory.set("src/main/kotlin/generated/symbols")
    packageName.set("io.github.kingsword09.symbolcraft.symbols")
    cacheDirectory.set("build/material-symbols-cache")
    cacheEnabled.set(true)
    assetsDirectory.set("src/main/assets/material-symbols")
    
    // 配置图标
    symbol("search") {
        style(weight = 400, variant = OUTLINED, fill = UNFILLED)
        style(weight = 500, variant = OUTLINED, fill = FILLED)
    }
    
    symbol("cast") {
        style(weight = 400, variant = OUTLINED, fill = UNFILLED)
    }
    
    symbol("home") {
        style(weight = 400, variant = ROUNDED, fill = UNFILLED)
    }
}
```

## 🎨 在 Compose 中使用

在 `MainActivity.kt` 中使用生成的图标：

```kotlin
import io.github.kingsword09.symbolcraft.symbols.HomeW400Rounded
import io.github.kingsword09.symbolcraft.symbols.PersonW400Outlined
import io.github.kingsword09.symbolcraft.symbols.SearchW400Outlined

@Composable
fun MainScreen() {
    Column {
        Icon(
            imageVector = HomeW400Rounded,
            contentDescription = "Home"
        )
        
        Icon(
            imageVector = SearchW400Outlined,
            contentDescription = "Search"
        )
        
        Icon(
            imageVector = PersonW400Outlined,
            contentDescription = "Person"
        )
    }
}
```

## 📂 项目结构

```
sample-android/
├── app/
│   ├── build.gradle.kts          # 插件配置
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── kotlin/
│           │   ├── generated/
│           │   │   └── symbols/   # 生成的图标代码
│           │   │       ├── HomeW400Rounded.kt
│           │   │       ├── PersonW400Outlined.kt
│           │   │       └── SearchW400Outlined.kt
│           │   └── io/github/kingsword09/symbolcraft/sample/
│           │       └── MainActivity.kt
│           └── assets/
│               └── material-symbols/  # 缓存的 SVG 文件
├── gradle.properties
└── settings.gradle.kts
```

## 🛠 Gradle 任务

| 任务 | 描述 |
|------|------|
| `generateMaterialSymbols` | 生成配置的图标 |
| `cleanSymbolsCache` | 清理 SVG 缓存 |
| `validateSymbolsConfig` | 验证图标配置 |
| `previewSymbols` | 预览将要生成的图标 |

## 🔄 工作流程

1. **配置图标**：在 `build.gradle.kts` 中定义需要的图标
2. **生成代码**：运行 `generateMaterialSymbols` 任务
3. **导入使用**：在 Compose 代码中导入并使用生成的图标
4. **自动更新**：修改配置后重新运行生成任务

## 📌 注意事项

- 生成的图标代码位于 `src/main/kotlin/generated/symbols/` 目录
- 图标文件名格式：`{IconName}W{Weight}{Variant}{Fill}.kt`
- SVG 文件会自动缓存，避免重复下载
- 当前版本使用简化的图标生成器，生成示例图标路径

## 🐛 问题排查

如果图标没有显示：
1. 确认已运行 `generateMaterialSymbols` 任务
2. 检查生成的文件是否存在于正确目录
3. 确认包名和导入路径正确
4. 清理缓存后重新生成：`./gradlew cleanSymbolsCache generateMaterialSymbols`

## 📄 许可证

MIT License - 与主项目相同
