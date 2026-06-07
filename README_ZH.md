# SymbolCraft 🎨

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.kingsword09/symbolcraft)


> **语言版本**: [English](README.md) | [中文](README_ZH.md)

一个强大的 Gradle 插件，用于在 Kotlin Multiplatform 项目中按需从多个图标库（Material Symbols、Bootstrap Icons、Heroicons 等）生成图标，支持智能缓存、确定性构建和高性能并行生成。

## ✨ 特性

- 🚀 **按需生成** - 仅生成你实际使用的图标，相比 Material Icons Extended (11.3MB) 减少 99%+ 体积
- 💾 **智能缓存** - 7天有效期的 SVG 文件缓存，避免重复网络请求
- 🗂️ **本地资源** - 直接将仓库中的 SVG 文件转为 Compose 代码，支持 glob 包含/排除，无需依赖远程 CDN
- ⚡ **并行下载** - 使用 Kotlin 协程并行下载 SVG 文件，支持配置重试逻辑
- 🎯 **确定性构建** - 保证每次生成的代码完全一致，Git 友好，缓存友好
- 🎨 **全样式支持** - 支持 Material Symbols 所有样式（权重、变体、填充状态）
- 🔧 **智能DSL** - 提供便捷的批量配置方法和预设样式
- 📚 **多图标库支持** - 支持 Material Symbols、Bootstrap Icons、Heroicons、Feather Icons 以及任何通过 URL 模板自定义的图标库
- 📱 **高质量输出** - 使用 svg-to-compose 库生成真实的 SVG 路径数据
- 🔄 **增量构建** - Gradle 任务缓存支持，只重新生成变更的图标
- 🏗️ **配置缓存兼容** - 完全支持 Gradle 配置缓存，提升构建性能
- 🔗 **多平台支持** - 支持 Android、Kotlin Multiplatform、JVM 等项目
- 👀 **Compose 预览** - 自动生成 Compose Preview 函数
- 🏷️ **灵活命名** - 自定义图标类名命名规则（PascalCase、camelCase、snake_case 等）

## 📦 安装

### 1. 添加插件到项目

在你的 `libs.versions.toml` 文件中：

```toml
[plugins]
symbolCraft = { id = "io.github.kingsword09.symbolcraft", version = "x.x.x" }
```

在你的 `build.gradle.kts` 文件中：

```kotlin
plugins {
    alias(libs.plugins.symbolCraft)
}
```

### 2. 配置插件

```kotlin
symbolCraft {
    // 基础配置
    packageName.set("com.app.symbols")
    outputDirectory.set("src/commonMain/kotlin")  // 支持多平台项目
    cacheEnabled.set(true)

    // 预览生成配置（可选）
    generatePreview.set(true)  // 启用预览生成
    previewAnnotationClass.set("androidx.compose.ui.tooling.preview.Preview")  // 默认值

    // 图标命名配置（可选）
    naming {
        pascalCase()  // 使用 PascalCase 命名规则（默认）
        // 或：camelCase()、snakeCase()、kebabCase() 等
    }

    // 单个图标配置（使用 Int 权重值）
    materialSymbol("search") {
        style(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
        style(weight = 500, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)
    }

    // 或使用 SymbolWeight 枚举以获得类型安全
    materialSymbol("home") {
        style(weight = SymbolWeight.W400, variant = SymbolVariant.OUTLINED)
        style(weight = SymbolWeight.W500, variant = SymbolVariant.ROUNDED)
    }

    // 便捷的批量配置方法
    materialSymbol("person") {
        standardWeights() // 自动添加 400, 500, 700 权重
    }

    materialSymbol("settings") {
        allVariants(weight = 400) // 添加所有变体 (outlined, rounded, sharp)
    }

    materialSymbol("favorite") {
        bothFills(weight = 500, variant = SymbolVariant.ROUNDED) // 同时添加填充和未填充
    }

    // 批量配置多个图标
    materialSymbols("star", "bookmark") {
        weights(400, 500, variant = SymbolVariant.OUTLINED)
    }

    // 仓库中的本地 SVG 文件
    localIcons {
        directory = "src/commonMain/resources/icons"
        // include("**/*.svg") // 可选，默认即为 **/*.svg
    }

    localIcons(libraryName = "brand") {
        directory = "design/exported"
        include("brand/**/*.svg")
        exclude("legacy/**")
    }
}
```

## 🎯 使用方法

### 1. 生成图标

运行以下命令生成配置的图标：

```bash
./gradlew generateSymbolCraftIcons
```

生成过程会显示详细进度：
```
🎨 Generating icons...
📊 Icons to generate: 12
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
// Material Symbols 图标
import com.yourcompany.app.symbols.icons.materialsymbols.Icons
import com.yourcompany.app.symbols.icons.materialsymbols.icons.SearchW400Outlined
import com.yourcompany.app.symbols.icons.materialsymbols.icons.HomeW400Rounded

// 外部图标库图标（例如：Bootstrap Icons）
import com.yourcompany.app.symbols.icons.bootstrapicons.Icons as BootstrapIcons
import com.yourcompany.app.symbols.icons.bootstrapicons.icons.BellBootstrapicons

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun MyScreen() {
    // Material Symbols 图标 - 方式1：直接导入使用
    Icon(
        imageVector = SearchW400Outlined,
        contentDescription = "Search"
    )

    // Material Symbols 图标 - 方式2：通过 Icons 对象使用
    Icon(
        imageVector = Icons.SearchW400Outlined,
        contentDescription = "Search"
    )

    Icon(
        imageVector = Icons.HomeW400Rounded,
        contentDescription = "Home"
    )

    // 外部库图标
    Icon(
        imageVector = BellBootstrapicons,
        contentDescription = "Notifications"
    )

    // 或通过访问器对象使用
    Icon(
        imageVector = BootstrapIcons.BellBootstrapicons,
        contentDescription = "Notifications"
    )
}
```

## 👀 Compose 预览功能

### 启用预览生成

```kotlin
symbolCraft {
    // 启用预览功能
    generatePreview.set(true)  // 生成 @Preview 函数

    // 配置图标...
    materialSymbol("home") {
        standardWeights()
    }
}
```

### 生成的预览文件

插件使用 `svg-to-compose` 库的预览生成功能为你的图标生成预览函数。SymbolCraft 默认会把生成的预览注解规范化为 `androidx.compose.ui.tooling.preview.Preview`，匹配 Compose Multiplatform 统一预览注解。

### 不同 Compose Multiplatform 版本的 Preview 注解

Compose Multiplatform 1.10+ 项目，包括示例项目当前使用的 Compose Multiplatform 1.11.1，直接使用默认 AndroidX 预览注解：

```kotlin
// commonMain 源码
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun IconPreview() {
    // 预览内容
}
```

```kotlin
symbolCraft {
    generatePreview.set(true)
    // 默认：androidx.compose.ui.tooling.preview.Preview
}
```

Compose Multiplatform 1.9.x 及更旧项目，请显式保留 JetBrains 旧预览注解：

```kotlin
// commonMain 源码
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun IconPreview() {
    // 预览内容
}
```

```kotlin
symbolCraft {
    generatePreview.set(true)
    previewAnnotationClass.set("org.jetbrains.compose.ui.tooling.preview.Preview")
}
```

如果你使用项目内自定义预览注解，可以配置完整注解类名：

```kotlin
symbolCraft {
    generatePreview.set(true)
    previewAnnotationClass.set("com.yourcompany.preview.IconPreview")
}
```

### 在 IDE 中查看预览

生成后，你可以在 Android Studio 或 IntelliJ IDEA 的 Preview 面板中查看：

1. 在输出目录的包路径下查找生成的预览文件
2. 点击 IDE 右侧的 "Preview" 面板（Android Studio/IntelliJ IDEA）
3. 在 IDE 中查看图标预览

### 多平台预览支持

默认生成的预览使用 `androidx.compose.ui.tooling.preview.Preview`，现代 Compose Multiplatform 项目可以在 common source set 中使用它。如果你仍面向较旧的 Compose Multiplatform 版本，请将 `previewAnnotationClass` 设置为项目实际提供的注解。

## 📋 配置选项

### 基础配置

```kotlin
symbolCraft {
    // 生成的 Kotlin 包名（必需）
    packageName.set("com.yourcompany.app.symbols")

    // 输出目录（支持多平台项目）
    outputDirectory.set("src/commonMain/kotlin")

    // 缓存配置
    cacheEnabled.set(true)  // 默认：true
    cacheDirectory.set("symbolcraft-cache")  // 默认："symbolcraft-cache"（相对于 build/）

    // 预览配置
    generatePreview.set(false)  // 默认：false - 是否生成 Compose @Preview 函数
    previewAnnotationClass.set("androidx.compose.ui.tooling.preview.Preview")  // 默认预览注解

    // 下载重试配置
    maxRetries.set(3)  // 默认：3 - 下载失败时的最大重试次数
    retryDelayMs.set(1000)  // 默认：1000ms - 重试之间的初始延迟

    // 图标命名配置（可选）
    naming {
        pascalCase()  // 默认命名规则
        // 可用选项：pascalCase()、camelCase()、snakeCase()、kebabCase() 等
    }
}
```

### 图标样式参数

- **weight**: 图标笔画粗细（100-700）
  - 100: 最细（SymbolWeight.W100 或 THIN）
  - 200: 超细（SymbolWeight.W200 或 EXTRA_LIGHT）
  - 300: 细（SymbolWeight.W300 或 LIGHT）
  - 400: 常规/正常（SymbolWeight.W400 或 REGULAR - 默认）
  - 500: 中等（SymbolWeight.W500 或 MEDIUM）
  - 600: 半粗（SymbolWeight.W600 或 SEMI_BOLD）
  - 700: 粗体（SymbolWeight.W700 或 BOLD）

- **variant**: 图标风格
  - `SymbolVariant.OUTLINED`: 线条风格（默认）
  - `SymbolVariant.ROUNDED`: 圆角风格
  - `SymbolVariant.SHARP`: 尖角风格

- **fill**: 填充状态
  - `SymbolFill.UNFILLED`: 空心（默认）
  - `SymbolFill.FILLED`: 实心

### 便捷配置方法

```kotlin
symbolCraft {
    materialSymbol("example") {
        // 基础方法（使用 Int）
        style(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)

        // 使用 SymbolWeight 枚举以获得类型安全
        style(weight = SymbolWeight.W400, variant = SymbolVariant.OUTLINED)

        // 批量权重配置（Int 值）
        weights(400, 500, 700, variant = SymbolVariant.ROUNDED)

        // 批量权重配置（SymbolWeight 枚举）
        weights(SymbolWeight.W400, SymbolWeight.W500, SymbolWeight.W700, variant = SymbolVariant.ROUNDED)

        // Material Design 标准权重（添加 400, 500, 700）
        standardWeights(variant = SymbolVariant.OUTLINED)

        // 所有变体（outlined, rounded, sharp）
        allVariants(weight = 400, fill = SymbolFill.UNFILLED)
        // 或使用枚举：allVariants(weight = SymbolWeight.W400, fill = SymbolFill.UNFILLED)

        // 同时添加填充和未填充版本
        bothFills(weight = 500, variant = SymbolVariant.OUTLINED)
        // 或使用枚举：bothFills(weight = SymbolWeight.W500, variant = SymbolVariant.OUTLINED)
    }
}
```

### 命名配置

控制生成的图标类名的转换方式：

```kotlin
symbolCraft {
    naming {
        // 预设命名规则
        pascalCase()              // HomeIcon（默认）
        pascalCase(suffix = "Icon")  // HomeIconIcon
        camelCase()               // homeIcon
        snakeCase()               // home_icon
        snakeCase(uppercase = true)  // HOME_ICON
        kebabCase()               // home-icon
        lowerCase()               // homeicon
        upperCase()               // HOMEICON

        // 细粒度控制
        namingConvention.set(NamingConvention.PASCAL_CASE)
        prefix.set("Ic")          // 前缀 → IcHome
        suffix.set("Icon")        // 后缀 → HomeIcon
        removePrefix.set("ic_")   // 移除输入前缀 → ic_home → Home
        removeSuffix.set("_24dp") // 移除输入后缀 → home_24dp → Home

        // 自定义转换器（高级）
        customTransformer(object : IconNameTransformer() {
            override fun transform(fileName: String): String {
                return fileName.uppercase() + "Icon"
            }
        })
    }
}
```

### 生成的文件命名规则

图标文件名格式：`{IconName}W{Weight}{Variant}{Fill}.kt`

例如：
- `SearchW400Outlined.kt` - Search 图标，400 权重，线条风格，未填充
- `HomeW500RoundedFill.kt` - Home 图标，500 权重，圆角风格，已填充
- `PersonW700Sharp.kt` - Person 图标，700 权重，尖角风格，未填充

### 从 0.4.x 迁移到 0.5.0

`0.5.0` 修复了已填充 Material Symbols 的生成命名问题。DSL 写法不变，但生成的 filled 图标名称会使用 `Fill`，不再把 Google Fonts URL 后缀 `fill1` 暴露到 Kotlin 名称里。

```kotlin
symbolCraft {
    materialSymbol("home") {
        bothFills(weight = 400)
    }
}
```

更新已填充 Material Symbols 的引用：

```text
旧：MaterialSymbols.HomeW400Outlinedfill1
新：MaterialSymbols.HomeW400OutlinedFill
```

这个变更只影响通过内置 `materialSymbol()` / `materialSymbols()` DSL 且使用 `SymbolFill.FILLED` 或 `bothFills()` 生成的图标。`externalIcon(s)` 的名称仍然来自你自己配置的 style 参数值。

## 🛠 Gradle 任务

插件提供以下 Gradle 任务：

| 任务 | 描述 |
|------|------|
| `generateSymbolCraftIcons` | 生成所有配置的图标库图标 |
| `cleanSymbolCraftCache` | 清理缓存的 SVG 文件 |
| `cleanSymbolCraftIcons` | 清理所有生成的图标文件 |
| `validateSymbolCraftConfig` | 验证图标配置的有效性 |

### 任务示例

```bash
# 生成图标（增量构建）
./gradlew generateSymbolCraftIcons

# 强制重新生成所有图标
./gradlew generateSymbolCraftIcons --rerun-tasks

# 清理缓存
./gradlew cleanSymbolCraftCache

# 清理生成的文件
./gradlew cleanSymbolCraftIcons

# 验证配置
./gradlew validateSymbolCraftConfig
```

## 📚 文档生成（Dokka）

SymbolCraft 提供 Dokka V2 配置，可为插件及其 DSL 生成可发布的 API 文档，便于同步到 Gradle Plugin Portal 或 Maven Central。

### 本地生成文档

```bash
# 生成 Javadoc 风格的文档（用于发布工件）
./gradlew dokkaGeneratePublicationJavadoc

# 可选：生成现代 HTML 文档
./gradlew dokkaGeneratePublicationHtml
```

任务会将结果写入 `build/dokka/` 目录中。打开 `build/dokka/javadoc/index.html`（或 `build/dokka/html/index.html`）即可在浏览器中查看。  
如果你在构建脚本中保留了兼容别名，`./gradlew dokkaJavadoc` 同样会转发到上述 Javadoc 任务。

> **提示：** 项目默认将 `org.jetbrains.dokka.experimental.gradle.pluginMode` 设置为 `V2Enabled`，直接使用 Dokka V2 的新任务名称。如果需要兼容旧任务，可暂时把该属性切换成 `V2EnabledWithHelpers`。

## 🗂 项目结构

使用插件后，你的项目结构可能如下：

```
your-project/
├── build.gradle.kts
├── .gitignore                                    # 建议添加生成文件到忽略列表
├── src/
│   └── commonMain/                               # 多平台项目支持
│       └── kotlin/
│           ├── com/app/
│           │   └── MainActivity.kt
│           └── com/app/symbols/                  # 生成的图标包
│               └── icons/                        # 按图标库组织的图标
│                   ├── materialsymbols/          # Material Symbols 图标
│                   │   ├── __Icons.kt            # Material Symbols 访问器
│                   │   └── icons/
│                   │       ├── SearchW400Outlined.kt
│                   │       ├── HomeW500RoundedFill.kt
│                   │       └── PersonW700Sharp.kt
│                   └── bootstrapicons/           # Bootstrap Icons (示例)
│                       ├── __Icons.kt            # Bootstrap Icons 访问器
│                       └── icons/
│                           ├── BellBootstrapicons.kt
│                           └── HouseBootstrapicons.kt
└── build/
    └── symbolcraft-cache/                        # 缓存目录（默认位置）
        ├── temp-svgs/                            # SVG 临时文件（按库组织）
        │   ├── material-symbols/
        │   └── external-bootstrapicons/
        └── svg-cache/                            # 缓存的 SVG 文件及元数据
```

## 📁 Git 配置建议

### .gitignore 配置

为了避免生成的文件在 Git 中显示为新文件，建议将生成目录添加到 `.gitignore`：

```gitignore
# SymbolCraft 生成的文件（根据你的配置调整包名）
**/icons/
**/__Icons.kt

# 或者忽略整个包
**/com/app/symbols/

# 缓存目录默认在 build/ 文件夹，执行 `./gradlew clean` 会自动清理
# 除非使用自定义缓存位置，否则无需添加到 .gitignore
```

### 生成文件管理策略

有两种处理生成文件的策略：

1. **忽略生成文件（推荐）**
   - 将生成目录添加到 `.gitignore`
   - 在 CI/CD 中运行 `generateSymbolCraftIcons` 任务
   - 优点：保持仓库干净，避免合并冲突

2. **提交生成文件**
   - 生成文件提交到仓库
   - 适合需要离线构建的场景
   - 缺点：增加仓库大小，可能产生合并冲突

## 🔄 缓存机制

### 多层缓存架构

1. **SVG 下载缓存**
   - 默认位置：`build/symbolcraft-cache/svg-cache/`
   - 有效期：7天
   - 包含：SVG 文件 + 元数据（时间戳、URL、哈希值）
   - 自动清理：配置变更时自动删除不再需要的缓存文件
   - 路径支持：同时支持相对路径（基于 build 目录）和绝对路径

2. **Gradle 任务缓存**
   - 增量构建支持
   - 基于配置哈希值的变更检测
   - 支持 `@CacheableTask` 注解

### 缓存路径配置

**相对路径（默认）：**
```kotlin
symbolCraft {
    cacheDirectory.set("symbolcraft-cache")  // → build/symbolcraft-cache/
    // 自动清理: ✅ 启用（项目私有缓存）
}
```

**绝对路径（用于共享/全局缓存）：**
```kotlin
symbolCraft {
    // Unix/Linux/macOS
    cacheDirectory.set("/var/tmp/symbolcraft")

    // Windows
    cacheDirectory.set("""C:\Temp\SymbolCraft""")

    // 网络共享（Windows UNC）
    cacheDirectory.set("""\\server\share\symbolcraft-cache""")

    // 自动清理: ❌ 禁用（避免多项目冲突）
}
```

### 共享缓存注意事项

当使用绝对路径配置多个项目共享缓存时：
- ✅ 缓存共享，减少重复下载，节省空间
- ✅ 切换项目时构建更快
- ⚠️ **自动清理功能被禁用**，避免缓存冲突
- 💡 可能需要手动清理旧文件

**使用共享缓存时的输出：**
```
ℹ️  Cache cleanup skipped: Using shared cache outside build directory
   Cache location: /var/tmp/symbolcraft
   Shared caches are preserved to avoid conflicts across projects
```

**手动清理（如需要）：**
```bash
# 清理30天前的旧文件
find /var/tmp/symbolcraft -type f -mtime +30 -delete

# 或者清理整个共享缓存
rm -rf /var/tmp/symbolcraft
```

### 缓存统计

生成完成后会显示缓存使用情况：
```
📦 SVG Cache: 45 files, 2.31 MB
💾 From cache: 8/12 icons
🧹 Cleaned 3 unused cache files
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
symbolCraft {
    // 基础图标集
    val basicIcons = listOf("home", "search", "person", "settings")
    basicIcons.forEach { icon ->
        materialSymbol(icon) {
            standardWeights()
        }
    }

    // 导航图标集
    val navIcons = listOf("arrow_back", "arrow_forward", "menu", "close")
    materialSymbols(*navIcons.toTypedArray()) {
        weights(400, 500)
        bothFills(weight = 400)
    }
}
```

### 图标来源配置

根据 SVG 的来源选择对应的 DSL 入口。

#### 内置 Material Symbols

Google Material Symbols 推荐使用 `materialSymbol()` 或 `materialSymbols()`。这条路径由 SymbolCraft 内部维护 Google Fonts 下载 URL，用户不需要关心 CDN 路径细节。

```kotlin
symbolCraft {
    materialSymbol("home") {
        bothFills(weight = 400)
    }

    materialSymbols("search", "settings", "person") {
        standardWeights()
    }
}
```

已填充 Material Symbols 生成的 Kotlin 名称使用 `Fill`：

```kotlin
MaterialSymbols.HomeW400Outlined
MaterialSymbols.HomeW400OutlinedFill
```

`0.5.0` 破坏性变更：

```text
0.4.x: MaterialSymbols.HomeW400Outlinedfill1
0.5.0: MaterialSymbols.HomeW400OutlinedFill
```

`fill1` 仍然只在内部用于 Google Fonts 下载 URL，不再暴露到生成的 Kotlin 名称中。

#### 外部 CDN 或 npm SVG 包

单个 SVG 使用 `externalIcon()`，同一来源的一组 SVG 使用 `externalIcons()`。`urlTemplate` 必须是完整的 `https://` URL。SymbolCraft 会把 `{name}` 替换为图标名称。

```kotlin
symbolCraft {
    externalIcons("abacus", "ab-testing", libraryName = "mdi") {
        urlTemplate = "https://esm.sh/@mdi/svg@latest/svg/{name}.svg"
    }

    externalIcons("bell", "calendar", "clock", libraryName = "bootstrap-icons") {
        urlTemplate = "https://esm.sh/bootstrap-icons@latest/icons/{name}.svg"
    }

    externalIcons("activity", "airplay", libraryName = "feather") {
        urlTemplate = "https://cdn.jsdelivr.net/npm/feather-icons/dist/icons/{name}.svg"
    }

    externalIcons("github", "kotlin", libraryName = "simple-icons") {
        urlTemplate = "https://simpleicons.org/icons/{name}.svg"
    }
}
```

#### 带变体的外部来源

远程路径里存在 style、size、fill、theme 等变体时，用 `styleParam()`。多个参数会生成笛卡尔积组合。

```kotlin
symbolCraft {
    externalIcons("home", "search", "person", libraryName = "official") {
        urlTemplate = "https://esm.sh/@material-symbols/svg-400@latest/rounded/{name}{fill}.svg"
        styleParam("fill") {
            values("", "-fill")
        }
    }
    // 生成类似 HomeOfficial、HomeFill 的名称。

    externalIcons("home", "user", "cog", libraryName = "heroicons") {
        urlTemplate = "https://cdn.jsdelivr.net/npm/heroicons@latest/24/{style}/{name}.svg"
        styleParam("style") {
            values("outline", "solid")
        }
    }

    externalIcon("status", libraryName = "internal") {
        urlTemplate = "https://cdn.example.com/icons/{size}/{theme}/{name}.svg"
        styleParam("size") {
            values("24", "48")
        }
        styleParam("theme") {
            values("light", "dark")
        }
    }
}
```

#### 本地 SVG 文件

项目内维护的品牌、产品或私有业务图标，使用 `localIcons()`。

```kotlin
symbolCraft {
    localIcons(libraryName = "brand") {
        directory = "src/commonMain/composeResources/files/icons"
        include("**/*.svg")
        exclude("draft/**")
    }
}
```

**URL 模板占位符：**
- `{name}` - 替换为图标名称
- `{key}` - 替换为 `styleParam("key", ...)` 定义的自定义样式参数值

### 自定义缓存配置

```kotlin
symbolCraft {
    // 禁用缓存（不推荐）
    cacheEnabled.set(false)

    // 自定义缓存目录（相对于 build 目录）
    cacheDirectory.set("custom-cache")  // → build/custom-cache/

    // 或使用绝对路径实现跨项目共享缓存
    cacheDirectory.set("/var/tmp/symbolcraft")  // → /var/tmp/symbolcraft/

    // 配置下载重试行为
    maxRetries.set(5)       // 增加重试次数
    retryDelayMs.set(2000)  // 更长的重试延迟
}
```

**注意**: 如需强制重新生成所有图标，请使用 Gradle 内置选项：
```bash
./gradlew generateSymbolCraftIcons --rerun-tasks
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
   # 清理 SymbolCraft 缓存
   ./gradlew cleanSymbolCraftCache

   # 或者清理整个 build 目录（包括缓存）
   ./gradlew clean

   # 强制重新生成所有图标
   ./gradlew generateSymbolCraftIcons --rerun-tasks
   ```

   注意：缓存文件默认存储在 `build/symbolcraft-cache/` 目录，运行 `./gradlew clean` 时会自动清理。

3. **图标未找到**
   ```
   ⚠️ Failed to download: icon-name-W400Outlined (Icon not found in Material Symbols)
   ```
   检查图标名称是否在 [Material Symbols Demo](https://marella.github.io/material-symbols/demo/) 中存在

4. **配置缓存问题**
   如果遇到配置缓存相关错误，可以暂时禁用：
   ```bash
   ./gradlew generateSymbolCraftIcons --no-configuration-cache
   ```

5. **生成文件在 Git 中显示为新文件**
   将生成目录添加到 `.gitignore`（根据你的配置调整包名）：
   ```gitignore
   **/icons/
   **/__Icons.kt
   ```

### 调试选项

```bash
# 详细日志
./gradlew generateSymbolCraftIcons --info

# 堆栈跟踪
./gradlew generateSymbolCraftIcons --stacktrace
```

## 🏗 架构设计

### 核心组件

- **SymbolCraftPlugin** - 主插件类，注册任务并连接扩展
- **SymbolCraftExtension** - DSL 配置接口，包含 MaterialSymbolsBuilder 和 ExternalIconBuilder
- **GenerateSymbolsTask** - 核心生成任务，支持并行下载和配置重试逻辑
- **NamingConfig** - 图标命名转换配置
- **IconNameTransformer** - 灵活的命名规则转换器
- **SvgDownloader** - 智能 SVG 下载器，支持 7 天缓存和重试机制
- **Svg2ComposeConverter** - SVG 转 Compose 转换器，使用 svg-to-compose 库
- **IconConfig** - 图标库配置基类（MaterialSymbolsConfig、ExternalIconConfig）
- **SymbolWeight/SymbolVariant/SymbolFill** - Material Symbols 样式枚举

### 数据流

```
配置 → 图标解析 → 样式解析 → 并行下载和重试 → SVG 转换 → 
命名转换 → 确定性处理 → 生成代码 → 可选预览生成
```

## 🎮 示例应用

项目包含一个完整的 Kotlin Multiplatform 示例应用，演示 SymbolCraft 的使用：

### 示例应用特性

- **多平台**: 支持 Android、iOS 和 Desktop (JVM)
- **生成图标**: 使用 SymbolCraft 从内置 Material Symbols、外部 SVG 源和本地 SVG 文件生成图标
- **预览支持**: 包含所有图标的生成 Compose 预览
- **真实使用**: 展示实际实现模式

### 运行示例

```bash
# 进入示例目录
cd example

# 生成配置的图标
./gradlew generateSymbolCraftIcons

# 运行 Android 应用
./gradlew :composeApp:assembleDebug

# 运行 Desktop 应用
./gradlew :composeApp:run

# iOS 应用需要在 Xcode 中打开 iosApp/iosApp.xcodeproj
```

### 示例配置

示例应用演示了各种配置选项：

```kotlin
kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDir("src/commonMain/generated/symbols")
        }
    }
}

symbolCraft {
    packageName.set("io.github.kingsword09.example")
    outputDirectory.set("src/commonMain/generated/symbols")
    generatePreview.set(true)

    // 图标命名配置
    naming {
        pascalCase()  // 使用 PascalCase 命名规则
    }

    // Material Symbols 图标 - 使用便捷方法
    materialSymbol("search") {
        standardWeights() // 添加 400, 500, 700 权重
    }

    materialSymbol("home") {
        weights(400, 500, variant = SymbolVariant.ROUNDED)
        bothFills(weight = 400) // 添加填充和未填充两种
    }

    materialSymbol("person") {
        allVariants(weight = SymbolWeight.W500) // 所有变体（outlined, rounded, sharp）
    }

    // 传统样式配置
    materialSymbol("settings") {
        style(weight = 400, variant = SymbolVariant.OUTLINED)
        style(weight = 500, variant = SymbolVariant.ROUNDED, fill = SymbolFill.FILLED)
    }

    // 来自 MDI 的外部图标
    externalIcons(*listOf("abacus", "ab-testing").toTypedArray(), libraryName = "mdi") {
        urlTemplate = "https://esm.sh/@mdi/svg@latest/svg/{name}.svg"
    }

    // 带样式变体的外部图标
    externalIcons(*listOf("home", "search", "person", "settings", "arrow_back").toTypedArray(), libraryName = "official") {
        urlTemplate = "https://esm.sh/@material-symbols/svg-400@latest/rounded/{name}{fill}.svg"
        styleParam("fill") {
            values("", "-fill")  // unfilled, filled 变体
        }
    }

    // 本地 SVG 文件
    localIcons("local-test") {
        directory = project.relativePath("src/commonMain/composeResources/files")
        include("**/*.svg")
    }

    // Simple Icons
    externalIcons("github", libraryName = "simple-icons") {
        urlTemplate = "https://simpleicons.org/icons/{name}.svg"
    }
}
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
cd example
./gradlew generateSymbolCraftIcons
./gradlew :composeApp:assembleDebug
```

### 开发工作流

1. 在 `src/main/kotlin/` 中修改插件源代码
2. 构建并本地发布：`./gradlew publishToMavenLocal`
3. 使用示例应用测试变更：`cd example && ./gradlew generateSymbolCraftIcons`
4. 运行测试：`./gradlew test`
5. 提交 pull request

## 🙏 致谢

- [Material Symbols](https://fonts.google.com/icons) - Google 图标库
- [marella/material-symbols](https://github.com/marella/material-symbols) - 便捷的图标浏览和搜索工具
- [DevSrSouza/svg-to-compose](https://github.com/DevSrSouza/svg-to-compose) - 优秀的 SVG 转 Compose 转换库
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Android 和多平台现代 UI 工具包
- 图标库提供商：Bootstrap Icons、Heroicons、Feather Icons、Material Design Icons 等

## 📄 许可证

Apache 2.0 License - 详见 [LICENSE](LICENSE) 文件
