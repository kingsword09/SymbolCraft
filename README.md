# SymbolCraft 🎨

一个强大的 Gradle 插件，用于在 Android Compose 项目中按需生成 Material Symbols 图标，支持智能缓存、确定性构建和高性能并行生成。

## ✨ 特性

- 🚀 **按需生成** - 仅生成你实际使用的图标，相比 Material Icons Extended (11.3MB) 减少 99%+ 体积
- 💾 **智能缓存** - 7天有效期的 SVG 文件缓存，避免重复网络请求
- ⚡ **并行下载** - 使用 Kotlin 协程并行下载 SVG 文件，大幅提升生成速度
- 🎯 **确定性构建** - 保证每次生成的代码完全一致，Git 友好，缓存友好
- 🎨 **全样式支持** - 支持 Material Symbols 所有样式（权重、变体、填充状态）
- 🔧 **智能DSL** - 提供便捷的批量配置方法和预设样式
- 📱 **高质量输出** - 使用 DevSrSouza/svg-to-compose 库生成真实的 SVG 路径数据
- 🔄 **增量构建** - Gradle 任务缓存支持，只重新生成变更的图标
- 🏗️ **配置缓存兼容** - 完全支持 Gradle 配置缓存，提升构建性能
- 🔗 **多平台支持** - 支持 Android、Kotlin Multiplatform、JVM 等项目

## 📦 安装

### 1. 添加插件到项目

在你的 `build.gradle.kts` 文件中：

```kotlin
plugins {
    id("io.github.kingsword09.symbolcraft") version "x.x.x"
}
```

### 2. 配置插件

```kotlin
materialSymbols {
    // 基础配置
    packageName.set("com.yourcompany.app.symbols")
    outputDirectory.set("src/commonMain/kotlin")  // 支持多平台项目
    cacheEnabled.set(true)

    // 单个图标配置
    symbol("search") {
        style(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
        style(weight = 500, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)
    }

    // 便捷的批量配置方法
    symbol("home") {
        standardWeights() // 自动添加 400, 500, 700 权重
    }

    symbol("person") {
        allVariants(weight = 400) // 添加所有变体 (outlined, rounded, sharp)
    }

    symbol("settings") {
        bothFills(weight = 500, variant = SymbolVariant.ROUNDED) // 同时添加填充和未填充
    }

    // 批量配置多个图标
    symbols("favorite", "star", "bookmark") {
        weights(400, 500, variant = SymbolVariant.OUTLINED)
    }
}
```

## 🎯 使用方法

### 1. 生成图标

运行以下命令生成配置的图标：

```bash
./gradlew generateMaterialSymbols
```

生成过程会显示详细进度：
```
🎨 Generating Material Symbols...
📊 Symbols to generate: 12 icons
⬇️ Downloading SVG files...
   Progress: 5/12
   Progress: 10/12
   Progress: 12/12
✅ Download completed:
   📁 Total: 12
   ✅ Success: 12
   ❌ Failed: 0
   💾 From cache: 8
🔄 Converting SVGs to Compose ImageVectors...
✅ Successfully converted 12 icons
📦 SVG Cache: 45 files, 2.31 MB
```

### 2. 在 Compose 中使用

生成的图标可以直接在 Compose 代码中使用：

```kotlin
import com.yourcompany.app.symbols.MaterialSymbols
import com.yourcompany.app.symbols.materialsymbols.SearchW400Outlined
import com.yourcompany.app.symbols.materialsymbols.HomeW400Rounded
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun MyScreen() {
    // 方式1：直接导入使用
    Icon(
        imageVector = SearchW400Outlined,
        contentDescription = "Search"
    )

    // 方式2：通过 MaterialSymbols 对象使用
    Icon(
        imageVector = MaterialSymbols.SearchW400Outlined,
        contentDescription = "Search"
    )

    Icon(
        imageVector = MaterialSymbols.HomeW400Rounded,
        contentDescription = "Home"
    )
}
```

## 📋 配置选项

### 基础配置

```kotlin
materialSymbols {
    // 生成的 Kotlin 包名
    packageName.set("com.yourcompany.app.symbols")

    // 输出目录（支持多平台项目）
    outputDirectory.set("src/commonMain/kotlin")

    // 缓存配置
    cacheEnabled.set(true)
    cacheDirectory.set("build/material-symbols-cache")

    // 其他选项
    forceRegenerate.set(false)  // 强制重新生成所有图标
    minifyOutput.set(true)      // 压缩输出代码
}
```

### 图标样式参数

- **weight**: 图标线条粗细（300-700）
  - 300: Light
  - 400: Regular（默认）
  - 500: Medium
  - 700: Bold

- **variant**: 图标风格
  - `SymbolVariant.OUTLINED`: 线条风格（默认）
  - `SymbolVariant.ROUNDED`: 圆角风格
  - `SymbolVariant.SHARP`: 尖角风格

- **fill**: 填充状态
  - `SymbolFill.UNFILLED`: 空心（默认）
  - `SymbolFill.FILLED`: 实心

### 便捷配置方法

```kotlin
materialSymbols {
    symbol("example") {
        // 基础方法
        style(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)

        // 批量权重配置
        weights(400, 500, 700, variant = SymbolVariant.ROUNDED)

        // Material Design 标准权重
        standardWeights(variant = SymbolVariant.OUTLINED)  // 添加 400, 500, 700

        // 所有变体（outlined, rounded, sharp）
        allVariants(weight = 400, fill = SymbolFill.UNFILLED)

        // 同时添加填充和未填充版本
        bothFills(weight = 500, variant = SymbolVariant.OUTLINED)
    }
}
```

### 生成的文件命名规则

图标文件名格式：`{IconName}W{Weight}{Variant}{Fill}.kt`

例如：
- `SearchW400Outlined.kt` - Search 图标，400 权重，线条风格，未填充
- `HomeW500RoundedFill.kt` - Home 图标，500 权重，圆角风格，已填充
- `PersonW700Sharp.kt` - Person 图标，700 权重，尖角风格，未填充

## 🛠 Gradle 任务

插件提供以下 Gradle 任务：

| 任务 | 描述 |
|------|------|
| `generateMaterialSymbols` | 生成配置的 Material Symbols 图标 |
| `cleanSymbolsCache` | 清理缓存的 SVG 文件 |
| `validateSymbolsConfig` | 验证图标配置的有效性 |

### 任务示例

```bash
# 生成图标（增量构建）
./gradlew generateMaterialSymbols

# 强制重新生成所有图标
./gradlew generateMaterialSymbols --rerun-tasks

# 清理缓存
./gradlew cleanSymbolsCache

# 验证配置
./gradlew validateSymbolsConfig
```

## 🗂 项目结构

使用插件后，你的项目结构可能如下：

```
your-project/
├── build.gradle.kts
├── .gitignore                              # 建议添加生成文件到忽略列表
├── src/
│   └── commonMain/                         # 多平台项目支持
│       └── kotlin/
│           ├── com/yourcompany/app/
│           │   └── MainActivity.kt
│           └── generated/                  # 生成的代码目录
│               └── symbols/                # 图标包
│                   ├── MaterialSymbols.kt  # 图标访问对象
│                   └── com/yourcompany/app/symbols/materialsymbols/
│                       ├── SearchW400Outlined.kt
│                       ├── HomeW500RoundedFill.kt
│                       └── PersonW700Sharp.kt
└── build/
    └── material-symbols-cache/             # 临时缓存目录
        └── temp-svgs/                      # SVG 临时文件
```

## 📁 Git 配置建议

### .gitignore 配置

为了避免生成的文件在 Git 中显示为新文件，建议将生成目录添加到 `.gitignore`：

```gitignore
# SymbolCraft 生成的文件
**/generated/symbols/
src/**/generated/
build/material-symbols-cache/

# 或者更具体的忽略
src/commonMain/kotlin/generated/
src/main/kotlin/generated/
```

### 生成文件管理策略

有两种处理生成文件的策略：

1. **忽略生成文件（推荐）**
   - 将生成目录添加到 `.gitignore`
   - 在 CI/CD 中运行 `generateMaterialSymbols` 任务
   - 优点：保持仓库干净，避免合并冲突

2. **提交生成文件**
   - 生成文件提交到仓库
   - 适合需要离线构建的场景
   - 缺点：增加仓库大小，可能产生合并冲突

## 🔄 缓存机制

### 多层缓存架构

1. **SVG 下载缓存**
   - 位置：`~/.gradle/caches/symbolcraft/svg-cache/`
   - 有效期：7天
   - 包含：SVG 文件 + 元数据（时间戳、URL、哈希值）

2. **Gradle 任务缓存**
   - 增量构建支持
   - 基于配置哈希值的变更检测
   - 支持 `@CacheableTask` 注解

### 缓存统计

生成完成后会显示缓存使用情况：
```
📦 SVG Cache: 45 files, 2.31 MB
💾 From cache: 8/12 icons
```

## 🚀 性能优化

### 并行下载

- 使用 Kotlin 协程并行下载 SVG 文件
- 支持进度跟踪和错误重试
- 智能缓存命中检测

### 确定性构建

- 移除时间戳和其他非确定性内容
- 标准化浮点数精度
- 统一导入语句排序
- 确保相同输入产生相同输出

### 配置缓存支持

- 完全兼容 Gradle 配置缓存（Configuration Cache）
- 避免任务执行时访问 Project 对象
- 使用 Provider API 提升构建性能
- 支持 `--configuration-cache` 参数

### 错误处理

- 网络错误自动重试
- 详细的错误分类和建议
- 优雅降级到备用生成器

## 📝 高级配置

### 图标查找和选择

使用 [Material Symbols Demo](https://marella.github.io/material-symbols/demo/) 来：
- 🔍 搜索和浏览所有可用图标
- 👀 预览不同样式（Outlined, Rounded, Sharp）
- 📋 复制图标名称用于配置
- 🎨 查看不同权重和填充状态的效果

### 批量配置图标

```kotlin
materialSymbols {
    // 基础图标集
    val basicIcons = listOf("home", "search", "person", "settings")
    basicIcons.forEach { icon ->
        symbol(icon) {
            standardWeights()
        }
    }

    // 导航图标集
    val navIcons = listOf("arrow_back", "arrow_forward", "menu", "close")
    symbols(*navIcons.toTypedArray()) {
        weights(400, 500)
        bothFills(weight = 400)
    }
}
```

### 自定义缓存配置

```kotlin
materialSymbols {
    // 禁用缓存（不推荐）
    cacheEnabled.set(false)

    // 自定义缓存目录
    cacheDirectory.set("custom-cache")

    // 强制重新生成
    forceRegenerate.set(true)
}
```

## 🔍 故障排除

### 常见问题

1. **网络问题**
   ```
   ❌ Generation failed: Network issue
   💡 Network issue detected. Check internet connection and try again.
   ```

2. **缓存问题**
   ```bash
   ./gradlew cleanSymbolsCache
   ./gradlew generateMaterialSymbols --rerun-tasks
   ```

3. **图标未找到**
   ```
   ⚠️ Failed to download: icon-name-W400Outlined (Icon not found in Material Symbols)
   ```
   检查图标名称是否在 [Material Symbols Demo](https://marella.github.io/material-symbols/demo/) 中存在

4. **配置缓存问题**
   如果遇到配置缓存相关错误，可以暂时禁用：
   ```bash
   ./gradlew generateMaterialSymbols --no-configuration-cache
   ```

5. **生成文件在 Git 中显示为新文件**
   将生成目录添加到 `.gitignore`：
   ```gitignore
   **/generated/symbols/
   src/**/generated/
   ```

### 调试选项

```bash
# 详细日志
./gradlew generateMaterialSymbols --info

# 堆栈跟踪
./gradlew generateMaterialSymbols --stacktrace
```

## 🏗 架构设计

### 核心组件

- **MaterialSymbolsPlugin** - 主插件类
- **MaterialSymbolsExtension** - DSL 配置接口
- **GenerateSymbolsTask** - 核心生成任务
- **SvgDownloader** - 智能 SVG 下载器
- **Svg2ComposeConverter** - SVG 转 Compose 转换器

### 数据流

```
配置 → 样式解析 → 并行下载 → SVG 转换 → 确定性处理 → 生成代码
```

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
../gradlew generateMaterialSymbols
../gradlew assembleDebug
```

## 📄 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

## 🙏 致谢

- [Material Symbols](https://fonts.google.com/icons) - Google 提供的图标资源
- [marella/material-symbols](https://github.com/marella/material-symbols) - 提供便捷的图标浏览和搜索功能
- [Material Symbols Demo](https://marella.github.io/material-symbols/demo/) - 图标查找和预览工具
- [DevSrSouza/svg-to-compose](https://github.com/DevSrSouza/svg-to-compose) - 优秀的 SVG 转 Compose 库
- [esm.sh](https://esm.sh) - 提供 CDN 服务的 Material Symbols SVG 文件
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Android 现代 UI 工具包

## 📮 联系方式

- GitHub: [@kingsword09](https://github.com/kingsword09)
- Issues: [GitHub Issues](https://github.com/kingsword09/SymbolCraft/issues)

---

**注意**：该项目已经过充分测试和优化，可以在生产环境中使用。具备确定性构建、智能缓存和高性能并行处理能力。