# SymbolCraft - Material Symbols 按需生成图标库

## 项目概述

SymbolCraft 是一个基于 **Kotlin 2.0.0** 和 **Gradle 8.0+** 的 Material Symbols 按需生成图标库系统，专为现代 Compose 项目设计。

### ✨ 核心特性

- 🎯 **按需生成**: 只生成项目中实际需要的图标和样式
- ⚡ **智能缓存**: 7天有效期的多层缓存机制，避免重复网络请求
- 🔄 **增量构建**: 基于配置变更的智能更新，支持 Gradle 任务缓存
- 🎨 **全样式支持**: 支持 Material Symbols 的所有样式参数（权重、变体、填充、光学尺寸）
- 📦 **极小包体积**: 相比 Material Icons Extended (11.3MB)，可减少 99%+ 体积
- 🏗️ **配置缓存兼容**: 完全支持 Gradle 配置缓存，提升构建性能
- 🔗 **多平台支持**: 支持 Android、Kotlin Multiplatform、JVM 等项目
- 👀 **Compose 预览**: 自动生成 Compose Preview 函数，支持 androidx 和 jetpack compose
- 🛡️ **类型安全**: 强类型 Kotlin API，IDE 自动补全支持
- 🚀 **并行下载**: 使用 Kotlin 协程并行下载 SVG 文件，大幅提升生成速度

## 技术栈

- **Kotlin**: 2.0.0
- **Gradle**: 8.0+
- **Compose**: 1.6.0+
- **协程**: Kotlinx Coroutines 1.8.1
- **网络**: Ktor Client 2.3.12
- **SVG转换**: DevSrSouza/svg-to-compose

## 项目架构

```
SymbolCraft/
├── build.gradle.kts                    # 插件构建配置
├── src/main/kotlin/
│   ├── plugin/                         # Gradle 插件核心
│   │   ├── MaterialSymbolsPlugin.kt    # 主插件类
│   │   ├── MaterialSymbolsExtension.kt # DSL 配置接口
│   │   └── GenerateSymbolsTask.kt      # 核心生成任务
│   ├── generator/                      # 代码生成器
│   │   └── PreviewGenerator.kt         # Compose 预览生成器
│   ├── converter/                      # SVG 转换器
│   │   └── Svg2ComposeConverter.kt     # SVG 到 Compose 转换
│   ├── download/                       # 下载组件
│   │   └── SvgDownloader.kt            # 智能 SVG 下载器
│   ├── util/                          # 工具类
│   │   └── PreviewDependencyDetector.kt # 预览依赖检测
│   └── model/                         # 数据模型
│       ├── SymbolStyle.kt             # 图标样式定义
│       ├── SymbolVariant.kt           # 图标变体枚举
│       └── SymbolFill.kt              # 填充状态枚举
├── example/                           # 示例项目
└── README.md
```

## 使用方法

### 1. 添加插件

在项目的 `build.gradle.kts` 中：

```kotlin
plugins {
    id("io.github.kingsword09.symbolcraft") version "0.1.0"
}
```

### 2. 配置图标

```kotlin
materialSymbols {
    // 基础配置
    packageName.set("com.yourproject.symbols")
    outputDirectory.set("src/commonMain/kotlin/generated/symbols")
    cacheEnabled.set(true)

    // 预览功能（可选）
    generatePreview.set(true)          // 启用 Compose 预览生成
    previewIconSize.set(32)            // 预览中图标大小（dp）
    previewBackgroundColor.set("#F5F5F5") // 预览背景色

    // 单个图标配置
    symbol("search") {
        style(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
        style(weight = 500, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)
    }

    // 便捷的批量配置
    symbol("home") {
        standardWeights() // 自动添加 400, 500, 700 权重
    }

    symbol("person") {
        allVariants(weight = 500) // 所有变体（outlined, rounded, sharp）
    }

    symbol("settings") {
        bothFills(weight = 400, variant = SymbolVariant.ROUNDED) // 填充和未填充
    }

    // 批量配置多个图标
    symbols("favorite", "star", "bookmark") {
        weights(400, 500, variant = SymbolVariant.OUTLINED)
    }
}
```

### 3. 生成图标

```bash
./gradlew generateMaterialSymbols
```

### 4. 在 Compose 中使用

```kotlin
import com.yourproject.symbols.MaterialSymbols
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun MyScreen() {
    Icon(
        imageVector = MaterialSymbols.SearchW400Outlined,
        contentDescription = "Search"
    )

    Icon(
        imageVector = MaterialSymbols.HomeW500RoundedFill,
        contentDescription = "Home"
    )
}
```

## 配置选项详解

### 基础配置

```kotlin
materialSymbols {
    // 必需配置
    packageName.set("com.yourproject.symbols")        // 生成代码的包名
    outputDirectory.set("src/commonMain/kotlin")      // 输出目录

    // 缓存配置
    cacheEnabled.set(true)                           // 启用缓存
    cacheDirectory.set("material-symbols-cache")     // 缓存目录名

    // 生成控制
    forceRegenerate.set(false)                       // 强制重新生成
    minifyOutput.set(true)                           // 压缩输出代码
}
```

### 预览配置

```kotlin
materialSymbols {
    // 预览功能
    generatePreview.set(true)                        // 启用预览生成
    previewIconSize.set(24)                          // 预览图标大小（dp）
    previewBackgroundColor.set("#FFFFFF")            // 预览背景色
}

// 需要的预览依赖
dependencies {
    // Android 项目
    debugImplementation("androidx.compose.ui:ui-tooling-preview:$compose_version")

    // Desktop 项目
    implementation(compose.desktop.ui.tooling.preview)
}
```

### 图标样式参数

#### 权重 (Weight)
- `300`: Light
- `400`: Regular（默认）
- `500`: Medium
- `700`: Bold

#### 变体 (Variant)
- `SymbolVariant.OUTLINED`: 线条风格（默认）
- `SymbolVariant.ROUNDED`: 圆角风格
- `SymbolVariant.SHARP`: 尖角风格

#### 填充状态 (Fill)
- `SymbolFill.UNFILLED`: 空心（默认）
- `SymbolFill.FILLED`: 实心

#### 光学尺寸 (Optical Size)
- `20`, `24`（默认）, `40`, `48`

### 便捷配置方法

```kotlin
materialSymbols {
    symbol("example") {
        // 基础方法
        style(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)

        // 批量权重
        weights(400, 500, 700, variant = SymbolVariant.ROUNDED)

        // 标准权重组合
        standardWeights(variant = SymbolVariant.OUTLINED)  // 400, 500, 700

        // 所有变体
        allVariants(weight = 400, fill = SymbolFill.UNFILLED)

        // 填充和未填充
        bothFills(weight = 500, variant = SymbolVariant.OUTLINED)
    }
}
```

## Compose 预览功能

### 启用预览

```kotlin
materialSymbols {
    generatePreview.set(true)
    previewIconSize.set(32)
    previewBackgroundColor.set("#F5F5F5")

    symbol("home") {
        standardWeights()
    }
}
```

### 自动依赖检测

插件会自动检测项目中的 Compose Preview 依赖：
- **androidx.compose**: `androidx.compose.ui:ui-tooling-preview`
- **jetbrains.compose**: `org.jetbrains.compose.ui:ui-tooling-preview`

### 生成的预览文件

启用预览后，插件会在 `{packageName}.preview` 包下生成：

```kotlin
// 单个图标预览
@Preview(name = "home - W400Outlined", showBackground = true)
@Composable
fun PreviewHomeW400Outlined() {
    MaterialTheme {
        Surface {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = MaterialSymbols.HomeW400Outlined,
                    contentDescription = "home",
                    modifier = Modifier.size(32.dp)
                )
                Text("home", fontSize = 12.sp)
                Text("W400Outlined", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

// 所有图标概览
@Preview(name = "All Material Symbols Overview", widthDp = 400, heightDp = 600)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PreviewAllMaterialSymbols() {
    MaterialTheme {
        Surface {
            FlowRow {
                // 显示所有生成的图标...
            }
        }
    }
}
```

### 多平台预览支持

- **Android 项目**: 使用 `androidx.compose.ui.tooling.preview.Preview`
- **Desktop 项目**: 使用 `androidx.compose.desktop.ui.tooling.preview.Preview` (别名为 `DesktopPreview`)
- **多平台项目**: 自动同时支持两种预览注解

## Gradle 任务

### 可用任务

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

# 使用配置缓存（推荐）
./gradlew generateMaterialSymbols --configuration-cache

# 清理缓存
./gradlew cleanSymbolsCache

# 验证配置
./gradlew validateSymbolsConfig
```

## 生成的文件结构

```
project/
├── build/material-symbols-cache/           # 临时缓存（正确位置）
│   └── temp-svgs/                          # SVG 临时文件
├── src/commonMain/kotlin/generated/
│   └── symbols/
│       ├── MaterialSymbols.kt              # 图标访问对象
│       ├── com/yourproject/symbols/materialsymbols/
│       │   ├── SearchW400Outlined.kt       # 具体图标文件
│       │   ├── HomeW500RoundedFill.kt
│       │   └── PersonW700Sharp.kt
│       └── preview/                        # 预览文件（可选）
│           └── MaterialSymbolsPreviews.kt
```

### 生成的文件命名规则

图标文件名格式：`{IconName}W{Weight}{Variant}{Fill}.kt`

例如：
- `SearchW400Outlined.kt` - Search 图标，400 权重，线条风格，未填充
- `HomeW500RoundedFill.kt` - Home 图标，500 权重，圆角风格，已填充
- `PersonW700Sharp.kt` - Person 图标，700 权重，尖角风格，未填充

## 缓存机制

### 多层缓存架构

1. **SVG 下载缓存**
   - 位置：`~/.gradle/caches/symbolcraft/svg-cache/`
   - 有效期：7天
   - 包含：SVG 文件 + 元数据（时间戳、URL、哈希值）

2. **Gradle 任务缓存**
   - 增量构建支持
   - 基于配置哈希值的变更检测
   - 支持 `@CacheableTask` 注解

3. **配置缓存**
   - 完全兼容 Gradle 配置缓存（Configuration Cache）
   - 避免任务执行时访问 Project 对象
   - 使用 Provider API 提升构建性能

### 缓存统计

生成完成后会显示缓存使用情况：
```
📦 SVG Cache: 45 files, 2.31 MB
💾 From cache: 8/12 icons
```

## 性能优化

### 并行下载
- 使用 Kotlin 协程并行下载 SVG 文件
- 支持进度跟踪和错误重试
- 智能缓存命中检测

### 确定性构建
- 移除时间戳和其他非确定性内容
- 标准化浮点数精度
- 统一导入语句排序
- 确保相同输入产生相同输出

### 错误处理
- 网络错误自动重试
- 详细的错误分类和建议
- 优雅降级到备用生成器

## Git 配置建议

### .gitignore 配置

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

1. **忽略生成文件（推荐）**
   - 将生成目录添加到 `.gitignore`
   - 在 CI/CD 中运行 `generateMaterialSymbols` 任务
   - 优点：保持仓库干净，避免合并冲突

2. **提交生成文件**
   - 生成文件提交到仓库
   - 适合需要离线构建的场景
   - 缺点：增加仓库大小，可能产生合并冲突

## 故障排除

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
   ```bash
   ./gradlew generateMaterialSymbols --no-configuration-cache
   ```

5. **生成文件在 Git 中显示为新文件**
   将生成目录添加到 `.gitignore`：
   ```gitignore
   **/generated/symbols/
   src/**/generated/
   ```

6. **预览生成失败**
   检查是否添加了 Compose Preview 依赖：
   ```kotlin
   // Android 项目
   debugImplementation("androidx.compose.ui:ui-tooling-preview:$compose_version")

   // Desktop 项目
   implementation(compose.desktop.ui.tooling.preview)
   ```

7. **预览在 IDE 中不显示**
   - 确保 IDE 支持 Compose Preview
   - 检查生成的预览文件路径是否正确
   - 重启 IDE 或刷新项目

### 调试选项

```bash
# 详细日志
./gradlew generateMaterialSymbols --info

# 堆栈跟踪
./gradlew generateMaterialSymbols --stacktrace

# 禁用配置缓存（调试用）
./gradlew generateMaterialSymbols --no-configuration-cache
```

## 高级用法

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

## 最佳实践

### 1. 合理使用缓存
- 保持 `cacheEnabled.set(true)` 以提升构建速度
- 定期运行 `cleanSymbolsCache` 清理过期缓存
- 在 CI/CD 中使用配置缓存提升构建性能

### 2. 优化包体积
- 只配置实际需要的图标和样式
- 避免配置过多不同权重和变体
- 使用 `minifyOutput.set(true)` 压缩生成代码

### 3. 团队协作
- 将生成目录添加到 `.gitignore`
- 在 CI/CD 中运行图标生成任务
- 统一图标配置规范

### 4. 预览功能
- 在开发环境中启用预览功能便于调试
- 在生产构建中可以禁用预览节省构建时间
- 使用预览功能验证图标样式是否符合预期

## 贡献指南

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
../gradlew generateMaterialSymbols
../gradlew assembleDebug
```

### 测试新功能

创建新的测试配置：
```kotlin
materialSymbols {
    // 测试配置
    generatePreview.set(true)

    symbol("test_icon") {
        style(weight = 400, variant = SymbolVariant.OUTLINED)
    }
}
```

## 技术细节

### 核心组件

- **MaterialSymbolsPlugin** - 主插件类，负责任务注册和依赖配置
- **MaterialSymbolsExtension** - DSL 配置接口，提供用户友好的配置 API
- **GenerateSymbolsTask** - 核心生成任务，支持增量构建和配置缓存
- **SvgDownloader** - 智能 SVG 下载器，支持并行下载和缓存
- **Svg2ComposeConverter** - SVG 转 Compose 转换器，生成高质量的 ImageVector
- **PreviewGenerator** - Compose 预览生成器，支持多平台预览注解
- **PreviewDependencyDetector** - 预览依赖检测器，智能检测项目中的预览能力

### 数据流

```
配置解析 → 依赖检测 → 样式解析 → 并行下载 → SVG 转换 → 确定性处理 → 生成代码 → 预览生成
```

### 配置缓存兼容性

SymbolCraft 完全支持 Gradle 配置缓存，通过以下方式实现：
- 避免在任务执行时访问 Project 对象
- 使用 Provider API 传递配置数据
- 在配置阶段完成所有依赖检测
- 序列化友好的数据结构

## 更新日志

### v0.1.0
- ✅ 基础图标生成功能
- ✅ 智能缓存机制
- ✅ 并行下载支持
- ✅ 配置缓存兼容
- ✅ Compose 预览生成
- ✅ 多平台支持
- ✅ 类型安全的 DSL
- ✅ 增量构建支持

## 许可证

Apache 2.0 License - 详见 [LICENSE](LICENSE) 文件

## 致谢

- [Material Symbols](https://fonts.google.com/icons) - Google 提供的图标资源
- [marella/material-symbols](https://github.com/marella/material-symbols) - 提供便捷的图标浏览和搜索功能
- [Material Symbols Demo](https://marella.github.io/material-symbols/demo/) - 图标查找和预览工具
- [DevSrSouza/svg-to-compose](https://github.com/DevSrSouza/svg-to-compose) - 优秀的 SVG 转 Compose 库
- [esm.sh](https://esm.sh) - 提供 CDN 服务的 Material Symbols SVG 文件
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Android 现代 UI 工具包

## 联系方式

- GitHub: [@kingsword09](https://github.com/kingsword09)
- Issues: [GitHub Issues](https://github.com/kingsword09/SymbolCraft/issues)

---

**注意**：SymbolCraft 已经过充分测试和优化，可以在生产环境中使用。具备确定性构建、智能缓存、配置缓存兼容和高性能并行处理能力。