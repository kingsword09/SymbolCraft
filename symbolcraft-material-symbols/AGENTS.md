# SymbolCraft Material Symbols - 开发指南

## 模块概述

**symbolcraft-material-symbols** 是 SymbolCraft 的预生成图标库模块，包含所有 Material Symbols 图标的预生成版本。

- **版本**: v0.4.0
- **类型**: Kotlin Multiplatform Library
- **状态**: 🚧 计划中（未实现）
- **语言**: Kotlin 2.0.0
- **平台支持**: Android、iOS、JVM、JS
- **图标数量**: ~3000+ 图标 × 7 权重 × 3 变体 × 2 填充状态

---

## 核心特性（规划）

- 📦 **预生成图标** - 所有 Material Symbols 图标预先生成并打包
- 🎨 **完整样式支持** - Outlined、Rounded、Sharp 三种变体
- ⚖️ **可变字重** - 100-700 全字重支持
- 🎯 **填充状态** - 填充和未填充版本
- 💾 **懒加载** - 通过 runtime 模块按需加载
- 🌳 **Tree-shakable** - 编译时移除未使用的图标
- 🔧 **模块化** - 按权重/变体拆分，按需引入

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.0.0 | 核心语言 |
| Compose Multiplatform | 1.6.11 | UI 框架 |
| symbolcraft-runtime | 0.4.0 | 运行时支持 |
| symbolcraft-plugin | 0.4.0 | 图标生成工具 |

---

## 模块结构

```
symbolcraft-material-symbols/
├── build.gradle.kts
├── README.md
├── AGENTS.md                          # 本文件（开发指南）
└── src/
    ├── commonMain/kotlin/io/github/kingsword09/symbolcraft/materialsymbols/
    │   ├── MaterialSymbols.kt          # 主访问入口（别名）
    │   │
    │   ├── outlined/                   # Outlined 样式
    │   │   ├── w100/                   # 权重 100
    │   │   │   ├── Home.kt
    │   │   │   ├── HomeFill.kt
    │   │   │   └── ...                 # ~3000 个图标
    │   │   ├── w200/
    │   │   ├── w300/
    │   │   ├── w400/                   # 默认权重
    │   │   ├── w500/
    │   │   ├── w600/
    │   │   └── w700/
    │   │
    │   ├── rounded/                    # Rounded 样式
    │   │   ├── w100/
    │   │   ├── w200/
    │   │   ├── w300/
    │   │   ├── w400/
    │   │   ├── w500/
    │   │   ├── w600/
    │   │   └── w700/
    │   │
    │   └── sharp/                      # Sharp 样式
    │       ├── w100/
    │       ├── w200/
    │       ├── w300/
    │       ├── w400/
    │       ├── w500/
    │       ├── w600/
    │       └── w700/
    │
    └── commonTest/kotlin/
        └── MaterialSymbolsTest.kt
```

---

## 图标组织方式

### 文件命名规则

每个图标文件遵循以下命名规则：

- **文件名格式**: `{IconName}.kt` 或 `{IconName}Fill.kt`
- **类名格式**: `{IconName}W{Weight}{Variant}` 或 `{IconName}W{Weight}{Variant}Fill`

**示例**:
```
outlined/w400/Home.kt               → HomeW400Outlined
outlined/w400/HomeFill.kt           → HomeW400OutlinedFill
rounded/w500/Search.kt              → SearchW500Rounded
rounded/w500/SearchFill.kt          → SearchW500RoundedFill
sharp/w700/Settings.kt              → SettingsW700Sharp
```

### 目录结构

```
variant (outlined/rounded/sharp)
  └── weight (w100/w200/w300/w400/w500/w600/w700)
      └── icon files (Home.kt, HomeFill.kt, ...)
```

---

## 用法示例

### 基础使用

```kotlin
import io.github.kingsword09.symbolcraft.materialsymbols.MaterialSymbols

@Composable
fun MyScreen() {
    // 方式1: 直接属性访问
    Icon(
        imageVector = MaterialSymbols.Outlined.W400.Home,
        contentDescription = "Home"
    )

    // 方式2: 索引访问
    Icon(
        imageVector = MaterialSymbols.Outlined.W400["search"],
        contentDescription = "Search"
    )

    // 方式3: 填充版本
    Icon(
        imageVector = MaterialSymbols.Rounded.W500.filled("favorite"),
        contentDescription = "Favorite"
    )
}
```

### 高级使用

```kotlin
@Composable
fun DynamicIcon(iconName: String) {
    // 动态图标名称
    Icon(
        imageVector = MaterialSymbols.get(
            name = iconName,
            weight = SymbolWeight.W400,
            variant = SymbolVariant.OUTLINED
        ),
        contentDescription = iconName
    )
}

@Composable
fun IconWithVariants() {
    val variant by remember { mutableStateOf(SymbolVariant.OUTLINED) }
    val weight by remember { mutableStateOf(SymbolWeight.W400) }

    Icon(
        imageVector = MaterialSymbols.get(
            name = "home",
            weight = weight,
            variant = variant
        ),
        contentDescription = "Home"
    )
}
```

---

## 实现计划

### 阶段 1: 图标生成策略 (1周)

**任务**:
- [ ] 设计预生成脚本
- [ ] 确定图标来源（Material Symbols CDN）
- [ ] 确定模块拆分策略（全量 vs 按权重拆分）
- [ ] 设计自动化生成流程

**技术方案**:

**方案 A: 全量单模块** (简单但体积大)
```
symbolcraft-material-symbols/
└── src/commonMain/kotlin/
    └── ~63,000 个图标文件 (~15-20MB)
```
- ✅ 简单直接
- ❌ 包体积大
- ❌ 编译时间长

**方案 B: 按权重拆分模块** (推荐)
```
symbolcraft-material-symbols-outlined-w400/  # ~3000 个图标 (~2MB)
symbolcraft-material-symbols-outlined-w500/
symbolcraft-material-symbols-rounded-w400/
...
```
- ✅ 按需引入，包体积小
- ✅ 编译快
- ⚠️ 需要多个模块管理

**方案 C: 混合方案** (最佳)
```
symbolcraft-material-symbols/           # 聚合模块（空）
symbolcraft-material-symbols-core/      # 核心常用图标 (~500个)
symbolcraft-material-symbols-full/      # 完整图标库（开发用）
symbolcraft-material-symbols-outlined-w400/  # 拆分模块（生产用）
...
```

### 阶段 2: 预生成脚本开发 (1周)

**任务**:
- [ ] 开发自动化生成脚本
- [ ] 集成 symbolcraft-plugin
- [ ] 配置并行生成
- [ ] 测试生成的图标质量

**生成脚本示例**:
```kotlin
// scripts/GenerateAllIcons.main.kts
import io.github.kingsword09.symbolcraft.plugin.*

/**
 * 生成所有 Material Symbols 图标
 * 运行: ./gradlew :symbolcraft-material-symbols:generateAllIcons
 */
val generator = IconGenerator(
    outputDir = File("symbolcraft-material-symbols/src/commonMain/kotlin"),
    packageName = "io.github.kingsword09.symbolcraft.materialsymbols"
)

// 从 Material Symbols API 获取所有图标名称
val allIcons = fetchAllIconNamesFromGoogleFonts()

// 生成配置
val weights = listOf(100, 200, 300, 400, 500, 600, 700)
val variants = listOf(
    SymbolVariant.OUTLINED,
    SymbolVariant.ROUNDED,
    SymbolVariant.SHARP
)
val fills = listOf(false, true) // unfilled, filled

// 并行生成
runBlocking {
    allIcons.chunked(100).map { chunk ->
        async(Dispatchers.IO) {
            chunk.forEach { iconName ->
                weights.forEach { weight ->
                    variants.forEach { variant ->
                        fills.forEach { fill ->
                            generator.generate(
                                name = iconName,
                                weight = weight,
                                variant = variant,
                                fill = fill
                            )
                        }
                    }
                }
            }
        }
    }.awaitAll()
}
```

### 阶段 3: 构建配置 (3天)

**任务**:
- [ ] 配置 build.gradle.kts
- [ ] 设置多平台支持
- [ ] 配置发布参数
- [ ] 优化编译性能

**build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.maven.publish)
    signing
}

kotlin {
    androidTarget()
    jvm()
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    sourceSets {
        commonMain.dependencies {
            api(project(":symbolcraft-runtime"))
            implementation(compose.runtime)
            implementation(compose.ui)
        }
    }
}

// 生成所有图标的任务
tasks.register("generateAllIcons") {
    group = "symbolcraft"
    description = "Generate all Material Symbols icons"

    doLast {
        exec {
            commandLine("kotlin", "scripts/GenerateAllIcons.main.kts")
        }
    }
}

// 在编译前自动生成图标
tasks.named("compileKotlinMetadata") {
    dependsOn("generateAllIcons")
}
```

### 阶段 4: Tree Shaking 集成 (1周)

**任务**:
- [ ] 设计 Tree Shaking 规则
- [ ] 实现编译器插件
- [ ] 集成 ProGuard/R8 规则
- [ ] 测试 Tree Shaking 效果

**ProGuard/R8 规则**:
```
# SymbolCraft Material Symbols - Tree Shaking Rules
# Auto-generated by SymbolCraft Compiler

# Keep runtime classes
-keep class io.github.kingsword09.symbolcraft.runtime.** { *; }

# Keep used icons (example)
-keep class io.github.kingsword09.symbolcraft.materialsymbols.outlined.w400.HomeW400Outlined { *; }
-keep class io.github.kingsword09.symbolcraft.materialsymbols.outlined.w400.SearchW400Outlined { *; }

# Remove unused icons
-assumenosideeffects class io.github.kingsword09.symbolcraft.materialsymbols.** {
    <init>(...);
}
```

### 阶段 5: 测试和优化 (1周)

**任务**:
- [ ] 单元测试
- [ ] 集成测试
- [ ] 性能测试（加载时间、内存占用）
- [ ] 包体积测试
- [ ] Tree Shaking 效果测试

**测试用例**:
```kotlin
class MaterialSymbolsTest {
    @Test
    fun testIconLoading() {
        val icon = MaterialSymbols.Outlined.W400.Home
        assertNotNull(icon)
        assertEquals(24.dp, icon.defaultWidth)
        assertEquals(24.dp, icon.defaultHeight)
    }

    @Test
    fun testAllWeights() {
        listOf(100, 200, 300, 400, 500, 600, 700).forEach { weight ->
            val icon = MaterialSymbols.Outlined["W$weight"]["home"]
            assertNotNull(icon)
        }
    }

    @Test
    fun testTreeShaking() {
        // 验证未使用的图标被移除
        // 需要配合构建工具测试
    }
}
```

### 阶段 6: 文档和发布 (3天)

**任务**:
- [ ] 完善 README
- [ ] 编写迁移指南
- [ ] 更新示例代码
- [ ] 发布到 Maven Central

---

## 开发工作流

### 本地开发

#### 1. 生成图标
```bash
# 生成所有图标
./gradlew :symbolcraft-material-symbols:generateAllIcons

# 或生成部分图标（用于测试）
./gradlew :symbolcraft-material-symbols:generateTestIcons
```

#### 2. 构建模块
```bash
./gradlew :symbolcraft-material-symbols:build
```

#### 3. 发布到本地 Maven
```bash
./gradlew :symbolcraft-material-symbols:publishToMavenLocal
```

#### 4. 在示例项目中测试
```bash
cd example
# 添加依赖
# implementation("io.github.kingsword09:symbolcraft-material-symbols:0.4.0-SNAPSHOT")
./gradlew :composeApp:run
```

---

## 性能考虑

### 包体积优化

**未优化**:
- 全量图标：~63,000 个文件
- 预估大小：~15-20MB

**优化后（Tree Shaking）**:
- 仅使用的图标：假设 50 个
- 预估大小：~50KB

### 编译时间优化

**策略**:
1. **增量编译** - 只重新编译修改的图标
2. **并行生成** - 使用 Kotlin 协程并行生成
3. **缓存生成结果** - 避免重复生成

### 内存占用优化

**策略**:
1. **懒加载** - 通过 runtime 模块按需加载
2. **LRU 缓存** - 限制内存中的图标数量
3. **Weak References** - 允许 GC 回收不常用图标

---

## 模块拆分策略

### 推荐拆分方案

```
symbolcraft-material-symbols/               # 聚合模块（空）
├── build.gradle.kts                         # 依赖所有子模块

symbolcraft-material-symbols-core/           # 核心常用图标
├── 500 个最常用的图标
├── 仅 W400 权重、Outlined 变体
└── ~1MB

symbolcraft-material-symbols-outlined-w400/  # 按权重拆分
symbolcraft-material-symbols-outlined-w500/
symbolcraft-material-symbols-outlined-w700/
symbolcraft-material-symbols-rounded-w400/
symbolcraft-material-symbols-rounded-w500/
symbolcraft-material-symbols-rounded-w700/
symbolcraft-material-symbols-sharp-w400/
symbolcraft-material-symbols-sharp-w500/
symbolcraft-material-symbols-sharp-w700/

symbolcraft-material-symbols-full/           # 完整库（开发用）
└── 所有图标 (~15-20MB)
```

### 用户引入方式

```kotlin
dependencies {
    // 方式1: 仅核心图标（推荐）
    implementation("io.github.kingsword09:symbolcraft-material-symbols-core:0.4.0")

    // 方式2: 按需引入特定权重/变体
    implementation("io.github.kingsword09:symbolcraft-material-symbols-outlined-w400:0.4.0")
    implementation("io.github.kingsword09:symbolcraft-material-symbols-rounded-w500:0.4.0")

    // 方式3: 完整库（开发阶段）
    debugImplementation("io.github.kingsword09:symbolcraft-material-symbols-full:0.4.0")
}
```

---

## 与 Runtime 模块的集成

### MaterialSymbols 别名

```kotlin
// symbolcraft-material-symbols/src/commonMain/kotlin/MaterialSymbols.kt
package io.github.kingsword09.symbolcraft.materialsymbols

import io.github.kingsword09.symbolcraft.runtime.MaterialSymbols as RuntimeMaterialSymbols

/**
 * MaterialSymbols 别名
 * 指向 runtime 模块的 MaterialSymbols
 */
typealias MaterialSymbols = RuntimeMaterialSymbols
```

### 图标文件示例

```kotlin
// symbolcraft-material-symbols/src/commonMain/kotlin/outlined/w400/Home.kt
package io.github.kingsword09.symbolcraft.materialsymbols.outlined.w400

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val HomeW400Outlined: ImageVector by lazy {
    ImageVector.Builder(
        name = "HomeW400Outlined",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black)
        ) {
            // SVG path data...
        }
    }.build()
}
```

---

## 依赖关系

### 对外依赖
```kotlin
commonMain.dependencies {
    api(project(":symbolcraft-runtime"))
    implementation(compose.runtime)
    implementation(compose.ui)
}
```

### 被依赖
- 用户应用 - 直接依赖此模块获取预生成的图标

---

## 发布清单

### 发布前检查
- [ ] 所有图标生成成功
- [ ] 所有平台编译通过
- [ ] Tree Shaking 测试通过
- [ ] 包体积符合预期
- [ ] 文档完整
- [ ] 示例代码可运行
- [ ] 版本号更新
- [ ] CHANGELOG 更新

### 发布命令
```bash
# 发布所有子模块到 Maven Central
./gradlew :symbolcraft-material-symbols:publishToMavenCentral
./gradlew :symbolcraft-material-symbols-core:publishToMavenCentral
./gradlew :symbolcraft-material-symbols-outlined-w400:publishToMavenCentral
# ...

# 或使用统一发布
./gradlew publishAll
```

---

## 常见问题

### Q: 为什么需要预生成图标？
A: 预生成图标提供类似 material-icons-extended 的开发体验，无需配置即可使用所有图标。配合 Tree Shaking，可以在生产构建时移除未使用的图标。

### Q: 如何选择合适的模块？
A:
- **开发阶段**: 使用 `symbolcraft-material-symbols-full` 获得完整 IDE 支持
- **生产构建**: 使用 `symbolcraft-material-symbols-core` 或按需引入特定权重/变体
- **混合方案**: `debugImplementation` 完整库，`releaseImplementation` 核心库 + Tree Shaking

### Q: Tree Shaking 如何工作？
A: 编译器插件分析代码中使用的图标，生成 ProGuard/R8 规则，在编译时移除未使用的图标类。

### Q: 包体积会有多大？
A:
- **未优化**: 15-20MB（全量图标）
- **核心库**: ~1MB（500 个常用图标）
- **Tree Shaking 后**: 取决于实际使用，通常 < 100KB

---

## 贡献指南

### 开发环境设置
1. Clone 仓库
2. 确保 Java 17+ 已安装
3. 运行 `./gradlew :symbolcraft-material-symbols:generateTestIcons` 生成测试图标
4. 运行测试确认环境正常

### 代码规范
- 遵循 Kotlin 官方编码规范
- 所有生成的图标必须通过 lint 检查
- 提交前运行 `./gradlew :symbolcraft-material-symbols:check`

### 提交规范
```
feat(material-symbols): add new icon variants
fix(material-symbols): resolve icon path issues
docs(material-symbols): update usage guide
build(material-symbols): optimize generation script
```

---

## 资源链接

### 内部文档
- [根项目 AGENTS.md](../AGENTS.md)
- [Plugin AGENTS.md](../symbolcraft-plugin/AGENTS.md)
- [Runtime AGENTS.md](../symbolcraft-runtime/AGENTS.md)
- [README.md](README.md)

### 外部资源
- [Material Symbols 官方](https://fonts.google.com/icons)
- [Material Symbols Demo](https://marella.github.io/material-symbols/demo/)
- [Material Symbols GitHub](https://github.com/marella/material-symbols)

---

## 联系方式

- **维护者**: [@kingsword09](https://github.com/kingsword09)
- **Email**: kingsword09@gmail.com
- **问题反馈**: [GitHub Issues](https://github.com/kingsword09/SymbolCraft/issues)

---

**最后更新**: 2025-10-19
**文档版本**: 1.0.0 (Material Symbols Module - Planning Stage)
**状态**: 🚧 未实现 - 规划阶段
