# SymbolCraft - 开发指南

## 项目概述

**SymbolCraft** 是一个用于 Kotlin Multiplatform 项目的 Gradle 插件，支持从多个图标库（Material Symbols、Bootstrap Icons、Heroicons 等）按需生成图标。

- **版本**: v0.4.0
- **架构**: Monorepo (多模块单仓库)
- **状态**: ✅ 已发布到 Gradle Plugin Portal 和 Maven Central
- **语言**: Kotlin 2.0.0
- **最低 Gradle 版本**: 8.7+
- **仓库**: https://github.com/kingsword09/SymbolCraft

### 核心特性

- 🚀 **多图标库支持** - Material Symbols、Bootstrap Icons、Heroicons、自定义 URL 模板
- 💾 **智能缓存** - 7天有效期的 SVG 缓存，支持相对/绝对路径
- ⚡ **并行下载** - Kotlin 协程并行下载，支持可配置的重试机制
- 🎯 **确定性构建** - Git 友好的确定性代码生成
- 🏷️ **灵活命名** - 支持多种命名规则（PascalCase、camelCase、snake_case 等）
- 👀 **Compose 预览** - 自动生成 @Preview 函数
- 🗂️ **本地资源** - 支持项目内 SVG 文件转换

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.0.0 | 核心语言 |
| Gradle | 8.7+ | 构建系统 |
| Kotlin Coroutines | 1.8.1 | 并行下载 |
| Ktor Client | 2.3.12 | HTTP 客户端 |
| Kotlinx Serialization | 1.7.1 | JSON 序列化 |
| Compose Multiplatform | 1.6.11 | UI 框架（runtime 模块） |
| Android Gradle Plugin | 8.5.2 | Android 构建 |
| svg-to-compose | 0.1.0 | SVG 转换库(io.github.kingsword09 fork) |

---

## 项目结构 (Monorepo)

```
SymbolCraft/                                    # 根项目
├── build.gradle.kts                            # 根构建配置（统一版本管理）
├── settings.gradle.kts                         # 子模块配置
├── gradle.properties                           # 全局 Gradle 配置
├── gradle/
│   └── libs.versions.toml                      # 版本目录（统一依赖管理）
│
├── symbolcraft-plugin/                         # Gradle 插件模块
│   ├── build.gradle.kts                        # 插件构建配置
│   ├── README.md                               # 插件模块文档
│   └── src/main/kotlin/io/github/kingsword09/symbolcraft/
│       ├── plugin/                             # Gradle 插件核心
│       │   ├── SymbolCraftPlugin.kt            # 插件入口，注册任务
│       │   ├── SymbolCraftExtension.kt         # DSL 配置接口
│       │   └── NamingConfig.kt                 # 命名配置
│       │
│       ├── tasks/                              # Gradle 任务
│       │   ├── GenerateSymbolsTask.kt          # 核心生成任务 (@CacheableTask)
│       │   ├── CleanSymbolsCacheTask.kt        # 清理缓存任务
│       │   ├── CleanSymbolsIconsTask.kt        # 清理生成文件任务
│       │   └── ValidateSymbolsConfigTask.kt    # 配置验证任务
│       │
│       ├── download/                           # 下载模块
│       │   └── SvgDownloader.kt                # 智能 SVG 下载器（协程并行 + 重试）
│       │
│       ├── converter/                          # 转换模块
│       │   ├── Svg2ComposeConverter.kt         # SVG 到 Compose 转换器
│       │   └── IconNameTransformer.kt          # 图标命名转换器
│       │
│       ├── model/                              # 数据模型
│       │   └── IconConfig.kt                   # 图标配置接口和实现
│       │
│       └── utils/                              # 工具类
│           └── PathUtils.kt                    # 路径工具
│
├── symbolcraft-runtime/                        # 运行时库模块（规划中）
│   ├── build.gradle.kts                        # Kotlin Multiplatform 配置
│   ├── README.md                               # 模块文档
│   ├── AGENTS.md                               # 运行时模块开发指南
│   └── src/
│       ├── commonMain/kotlin/                  # 通用代码
│       │   └── io/github/kingsword09/symbolcraft/runtime/
│       │       ├── MaterialSymbols.kt          # 主访问入口
│       │       ├── IconLoader.kt               # 图标加载器
│       │       ├── IconCache.kt                # 缓存管理
│       │       └── LazyImageVector.kt          # 懒加载支持
│       ├── androidMain/kotlin/                 # Android 特定代码
│       ├── jvmMain/kotlin/                     # JVM 特定代码
│       ├── iosMain/kotlin/                     # iOS 特定代码
│       └── commonTest/kotlin/                  # 测试代码
│
├── symbolcraft-material-symbols/               # 预生成图标库模块（规划中）
│   ├── build.gradle.kts                        # Kotlin Multiplatform 配置
│   ├── README.md                               # 模块文档
│   ├── AGENTS.md                               # 图标库模块开发指南
│   └── src/
│       ├── commonMain/kotlin/                  # 预生成的图标代码
│       │   └── io/github/kingsword09/symbolcraft/icons/
│       │       ├── outlined/                   # Outlined 样式
│       │       ├── rounded/                    # Rounded 样式
│       │       └── sharp/                      # Sharp 样式
│       └── commonTest/kotlin/                  # 测试代码
│
├── example/                                    # 示例项目（Compose Multiplatform）
│   ├── composeApp/                             # 主应用
│   │   ├── src/
│   │   │   ├── androidMain/                   # Android 平台代码
│   │   │   ├── iosMain/                       # iOS 平台代码
│   │   │   ├── jvmMain/                       # Desktop 平台代码
│   │   │   └── commonMain/                    # 通用代码
│   │   │       └── kotlin/generated/symbols/  # 生成的图标
│   │   └── build.gradle.kts                    # 使用 SymbolCraft 插件
│   └── iosApp/                                 # iOS 应用
│
├── README.md                                   # 英文文档
├── README_ZH.md                                # 中文文档
└── AGENTS.md                                   # 本文件（开发指南）
```

---

## 核心组件说明

### 1. **SymbolCraftPlugin** (插件入口)
**位置**: `symbolcraft-plugin/src/main/kotlin/.../plugin/SymbolCraftPlugin.kt`

**职责**:
- 注册 `symbolCraft` DSL 扩展
- 注册 Gradle 任务：
  - `generateSymbolCraftIcons` - 生成所有配置的图标
  - `cleanSymbolCraftCache` - 清理 SVG 缓存
  - `cleanSymbolCraftIcons` - 清理生成的图标文件
  - `validateSymbolCraftConfig` - 验证配置
- 自动添加任务依赖：在 Kotlin 编译之前生成图标

**关键代码**:
```kotlin
class SymbolCraftPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("symbolCraft", SymbolCraftExtension::class.java)

        val generateTask = project.tasks.register("generateSymbolCraftIcons", GenerateSymbolsTask::class.java) {
            // 配置任务...
        }

        // 自动添加到 Kotlin 编译任务的依赖
        project.afterEvaluate {
            project.tasks.configureEach { task ->
                if (task.name.contains("compileKotlin", ignoreCase = true)) {
                    task.dependsOn(generateTask)
                }
            }
        }
    }
}
```

---

### 2. **SymbolCraftExtension** (DSL 配置)
**位置**: `symbolcraft-plugin/src/main/kotlin/.../plugin/SymbolCraftExtension.kt`

**职责**:
- 提供用户友好的 DSL API
- 管理多图标库的配置（Material Symbols、外部图标库、本地图标）
- 提供便捷配置方法：
  - `materialSymbol()` / `materialSymbols()` - 配置 Material Symbols 图标
  - `externalIcon()` / `externalIcons()` - 配置外部图标库图标
  - `localIcons()` - 配置本地 SVG 文件
  - `standardWeights()` - 标准权重（400, 500, 700）
  - `allVariants()` - 所有变体（outlined, rounded, sharp）
  - `bothFills()` - 填充和未填充
  - `naming {}` - 配置命名规则

**配置选项**:
```kotlin
abstract class SymbolCraftExtension {
    abstract val packageName: Property<String>              // 包名
    abstract val outputDirectory: Property<String>          // 输出目录
    abstract val cacheEnabled: Property<Boolean>            // 缓存开关
    abstract val cacheDirectory: Property<String>           // 缓存目录
    abstract val generatePreview: Property<Boolean>         // 生成预览
    abstract val maxRetries: Property<Int>                  // 最大重试次数
    abstract val retryDelayMs: Property<Long>               // 重试延迟

    val namingConfig: NamingConfig                          // 命名配置
}
```

---

### 3. **GenerateSymbolsTask** (核心生成任务)
**位置**: `symbolcraft-plugin/src/main/kotlin/.../tasks/GenerateSymbolsTask.kt`

**职责**:
- 解析用户配置（Material Symbols + 外部图标库 + 本地图标）
- 并行下载 SVG 文件（使用 Kotlin 协程）
- 应用命名转换规则
- 调用转换器生成 Compose ImageVector 代码
- 管理缓存和增量构建
- 清理未使用的缓存文件（相对路径缓存）

**特性**:
- `@CacheableTask` - 支持 Gradle 任务缓存
- 配置缓存兼容 - 使用 Provider API，避免访问 Project 对象
- 智能缓存清理 - 相对路径启用，绝对路径跳过
- 可配置重试 - maxRetries 和 retryDelayMs

**关键流程**:
```
配置解析 → 清理旧文件 → 并行下载 SVG → 命名转换 → 转换为 Compose →
清理未使用缓存 → 生成统计
```

---

## 开发工作流

### 本地开发测试

#### 1. 修改插件代码
```bash
# 编辑 symbolcraft-plugin/src/main/kotlin/ 下的源文件
vim symbolcraft-plugin/src/main/kotlin/io/github/kingsword09/symbolcraft/tasks/GenerateSymbolsTask.kt
```

#### 2. 发布到本地 Maven
```bash
# 发布所有模块
./gradlew publishAllToMavenLocal

# 或仅发布插件模块
./gradlew :symbolcraft-plugin:publishToMavenLocal
```

#### 3. 在示例项目中测试
```bash
cd example
./gradlew generateSymbolCraftIcons --info
./gradlew :composeApp:run  # Desktop
```

#### 4. 清理和重新构建
```bash
./gradlew clean build
```

---

## 构建和发布流程

### 1. 本地构建
```bash
./gradlew build                         # 构建所有模块
./gradlew :symbolcraft-plugin:build    # 仅构建插件
./gradlew :symbolcraft-plugin:test     # 插件模块测试
```

### 2. 本地发布（测试用）

#### 发布所有模块到本地 Maven
```bash
./gradlew publishAllToMavenLocal        # 统一发布所有模块
```

#### 发布单个模块
```bash
./gradlew :symbolcraft-plugin:publishToMavenLocal
./gradlew :symbolcraft-runtime:publishToMavenLocal
./gradlew :symbolcraft-material-symbols:publishToMavenLocal
```

### 3. 发布到 Maven Central

#### 发布所有模块（推荐）
```bash
./gradlew publishAll                    # 统一发布所有模块
```

#### 发布单个模块
```bash
./gradlew :symbolcraft-plugin:publishToMavenCentral
./gradlew :symbolcraft-runtime:publishToMavenCentral
./gradlew :symbolcraft-material-symbols:publishToMavenCentral
```

### 4. 发布到 Gradle Plugin Portal
```bash
./gradlew :symbolcraft-plugin:publishPlugins  # 仅插件模块
```

### 配置要求

#### gradle.properties 或环境变量
```properties
# 签名配置
SIGNING_KEY=<GPG 签名密钥>
SIGNING_PASSWORD=<签名密码>

# Maven Central 配置
mavenCentralUsername=<用户名>
mavenCentralPassword=<密码>

# Gradle Plugin Portal 配置（仅插件模块需要）
gradle.publish.key=<API Key>
gradle.publish.secret=<API Secret>
```

#### gradle.properties 重要配置
```properties
# Gradle 优化
org.gradle.jvmargs=-Xmx2g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true

# Kotlin 配置
kotlin.code.style=official
kotlin.mpp.stability.nowarn=true
kotlin.mpp.androidSourceSetLayoutVersion=2
kotlin.mpp.androidGradlePluginCompatibility.nowarn=true
kotlin.apple.xcodeCompatibility.nowarn=true

# AndroidX 支持
android.useAndroidX=true
android.enableJetifier=false

# Dokka 配置
org.jetbrains.dokka.experimental.gradle.pluginMode=V2Enabled
```

### 版本管理

所有模块使用统一版本号，在根项目的 `build.gradle.kts` 中配置：

```kotlin
allprojects {
    group = "io.github.kingsword09"
    version = "0.4.0"  // 统一版本
}
```

---

## 缓存机制详解

### 缓存架构

1. **SVG 下载缓存** (`build/symbolcraft-cache/svg-cache/`)
   - 有效期：7 天
   - 包含：SVG 文件 + JSON 元数据
   - 元数据字段：`timestamp`, `url`, `hash`
   - 支持多图标库缓存隔离（通过 libraryId）

2. **Gradle 任务缓存**
   - 基于配置哈希值检测变更
   - `@CacheableTask` 注解支持

3. **配置缓存**
   - 使用 Provider API
   - 避免任务执行时访问 Project

### 缓存路径支持

**相对路径（默认）**:
```kotlin
cacheDirectory.set("symbolcraft-cache")  // → build/symbolcraft-cache/
```
- ✅ 自动清理未使用的缓存
- ✅ 项目隔离
- ✅ `./gradlew clean` 自动清理

**绝对路径（共享缓存）**:
```kotlin
// Unix/Linux/macOS
cacheDirectory.set("/var/tmp/symbolcraft")  // → /var/tmp/symbolcraft/
// Windows
cacheDirectory.set("""C:\Temp\SymbolCraft""")
```
- ✅ 跨项目共享
- ⚠️ 跳过自动清理（防止冲突）

---

## 测试现状

### 当前状态
✅ **部分单元测试** - 已有测试:
- `IconNameTransformerTest` - 命名转换测试
- `GenerateSymbolsTaskTest` - 任务测试（基础）
- `LocalIconsBuilderTest` - 本地图标构建器测试

### 测试覆盖目标
- [ ] 核心组件测试覆盖率 > 80%
- [ ] 集成测试
- [ ] 性能基准测试

---

## 待办事项和改进方向

### 🔴 高优先级

#### 1. **补全单元测试**
- [ ] `SvgDownloader` 测试 - 下载逻辑、缓存、重试机制
- [ ] `Svg2ComposeConverter` 测试 - SVG 转换、代码生成
- [ ] `IconConfig` 实现类测试
- [ ] 集成测试

#### 2. **实现 symbolcraft-runtime 模块**
**状态**: 🚧 规划中

**核心功能**:
- 懒加载图标支持
- 内存缓存（LRU）
- `MaterialSymbols` API（类似 `androidx.compose.material.icons.Icons`）
- 多平台支持（Android、iOS、JVM、JS）

**实施计划**:
```kotlin
// 用法示例
import io.github.kingsword09.symbolcraft.runtime.MaterialSymbols

@Composable
fun MyScreen() {
    Icon(
        imageVector = MaterialSymbols.Outlined.W400.Home,
        contentDescription = "Home"
    )
}
```

#### 3. **实现 symbolcraft-material-symbols 模块**
**状态**: 🚧 规划中

**核心功能**:
- 预生成所有 Material Symbols 图标
- 按权重/样式拆分模块
- 配合 Tree Shaking 优化包体积

---

### 🟡 中优先级

#### 4. **Tree Shaking 功能** ⭐ 新功能
**状态**: 📋 设计阶段

**目标**: 只生成实际使用的图标，避免生成大量未使用的图标

**技术方案**:

**方案 1: 静态代码扫描（推荐）**
```kotlin
symbolCraft {
    treeShaking {
        enabled.set(true)

        // 扫描范围
        scanDirectories.addAll(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin"
        )

        // 扫描策略
        strategy.set(ScanStrategy.USAGE_BASED)  // 或 IMPORT_BASED

        // 白名单
        alwaysInclude.addAll("home", "search", "settings")
    }
}
```

**实现步骤**:
1. **代码扫描器** - 分析 Kotlin 文件中的 import 和图标使用
2. **引用图构建** - 构建符号引用关系图
3. **使用追踪** - 识别实际使用的图标
4. **过滤生成** - 只生成被引用的图标

**处理 import * 的策略**:
```kotlin
// Case 1: 具名导入 - 直接识别
import com.app.symbols.icons.HomeW400Outlined  ✅

// Case 2: 通配符导入 - 需要追踪实际使用
import com.app.symbols.icons.*
// 分析代码中的 Icon(HomeW400Outlined, ...) 识别使用

// Case 3: Icons 对象访问 - 解析属性访问
import com.app.symbols.Icons
Icon(Icons.HomeW400Outlined, ...)  ✅
```

**核心组件**:
```kotlin
// 图标使用分析器
class IconUsageAnalyzer(
    private val scanDirs: Set<File>,
    private val packageName: String
) {
    fun analyze(): Set<String> {
        // 1. 解析 import 语句
        // 2. 分析代码中的图标引用
        // 3. 构建引用图
        // 4. 返回使用的图标集合
    }
}

// 分析任务
@CacheableTask
abstract class AnalyzeIconUsageTask : DefaultTask() {
    @get:InputFiles
    abstract val scanDirectories: SetProperty<File>

    @get:OutputFile
    abstract val analysisResultFile: RegularFileProperty

    @TaskAction
    fun analyze() {
        val analyzer = IconUsageAnalyzer(...)
        val result = analyzer.analyze()
        // 保存分析结果
    }
}
```

**性能优化**:
- 增量分析（只分析变更的文件）
- 缓存分析结果
- 并行扫描多个文件

**参考**: Webpack/Rollup 的 Tree Shaking 原理

---

#### 5. **性能监控**
- [ ] 生成时间统计
- [ ] 下载速度统计
- [ ] 缓存命中率报告

**示例输出**:
```
📊 Generation Report:
   ⏱️ Total time: 3.2s
   ⬇️ Download: 1.5s (avg 245KB/s)
   🔄 Conversion: 1.7s
   💾 Cache hit rate: 66.7% (8/12)
   📦 Generated: 12 icons, 245KB total
```

#### 6. **错误处理增强**
- [ ] 详细的错误分类
- [ ] 错误恢复策略
- [ ] 更友好的错误提示

```kotlin
sealed class SymbolCraftError {
    data class NetworkError(val url: String, val cause: Throwable)
    data class CacheError(val path: String, val cause: Throwable)
    data class ConversionError(val iconName: String, val cause: Throwable)
    data class ConfigurationError(val message: String)
}
```

---

### 🟢 低优先级

#### 7. **IntelliJ IDEA 插件**
**功能**:
- 可视化配置向导
- 图标搜索和预览
- 代码辅助（自动补全、预览 Inlay Hints）
- 快捷操作（右键菜单生成图标）

#### 8. **CLI 工具**
```bash
symbolcraft search "home"           # 搜索图标
symbolcraft add home --weight 400   # 添加图标到配置
symbolcraft init                    # 交互式初始化
```

#### 9. **高级特性**
- [ ] 图标动态变体生成
- [ ] 多主题支持
- [ ] 图标使用分析报告

---

## 依赖管理

### 核心依赖

```kotlin
// symbolcraft-plugin 依赖
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-cio:2.3.12")
    implementation("io.github.kingsword09:svg-to-compose:0.1.0")

    compileOnly("org.gradle:gradle-api")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin")
}

// symbolcraft-runtime 依赖（规划）
dependencies {
    implementation("org.jetbrains.compose.runtime:runtime")
    implementation("org.jetbrains.compose.ui:ui")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
}
```

### 版本更新策略

- 定期检查依赖更新：`./gradlew dependencyUpdates`
- 测试新版本兼容性
- 保持 Kotlin 和 Gradle 版本同步

---

## 常见开发任务

### 添加新的 Gradle 任务

1. 在 `symbolcraft-plugin/.../plugin/SymbolCraftPlugin.kt` 中注册任务
2. 在 `tasks/` 目录创建任务类继承 `DefaultTask`
3. 使用 `@TaskAction` 注解标记执行方法
4. 配置任务的输入/输出以支持增量构建

### 添加新的配置选项

1. 在 `SymbolCraftExtension.kt` 中添加 `Property<T>`
2. 在 `GenerateSymbolsTask.kt` 中读取配置
3. 更新配置哈希（`getConfigHash()`）
4. 更新所有文档（README.md、README_ZH.md、AGENTS.md）

### 添加新的图标库支持

1. 在 `model/IconConfig.kt` 中创建新的 `IconConfig` 实现
2. 实现必需的方法：`buildUrl()`、`getCacheKey()`、`getSignature()`
3. 在 `SymbolCraftExtension.kt` 中添加相应的 DSL 方法
4. 更新文档和示例

### 修改 SVG 下载逻辑

编辑 `symbolcraft-plugin/src/main/kotlin/.../download/SvgDownloader.kt`：
- 修改 CDN URL
- 调整缓存策略
- 增强错误处理

### 修改代码生成

编辑 `symbolcraft-plugin/src/main/kotlin/.../converter/Svg2ComposeConverter.kt`：
- 调整输出格式
- 修改预览生成
- 自定义文件命名

---

## 调试技巧

### 启用详细日志
```bash
./gradlew generateSymbolCraftIcons --info       # 信息级别
./gradlew generateSymbolCraftIcons --debug      # 调试级别
./gradlew generateSymbolCraftIcons --stacktrace # 堆栈跟踪
```

### 禁用配置缓存（调试用）
```bash
./gradlew generateSymbolCraftIcons --no-configuration-cache
```

### 强制重新运行任务
```bash
./gradlew generateSymbolCraftIcons --rerun-tasks
```

### 查看任务依赖
```bash
./gradlew generateSymbolCraftIcons --dry-run
```

### 查看生成的文件
```bash
# 查看生成的 Kotlin 文件
find . -path "*/generated/symbols/*" -name "*.kt"

# 查看缓存状态
du -sh build/symbolcraft-cache/
```

### 调试子模块
```bash
# 列出所有项目
./gradlew projects

# 查看特定模块的任务
./gradlew :symbolcraft-plugin:tasks
./gradlew :symbolcraft-runtime:tasks

# 构建特定模块
./gradlew :symbolcraft-plugin:build --info
```

---

## Git 工作流

### 分支策略
- `main` - 稳定发布分支
- `develop` - 开发分支（如有）
- `feature/*` - 功能分支
- `fix/*` - 修复分支
- `refactor/*` - 重构分支

### 提交规范（建议）
```
<type>(<scope>): <subject>

类型（type）:
- feat: 新功能
- fix: 修复 bug
- docs: 文档更新
- style: 代码格式
- refactor: 重构
- test: 测试
- chore: 构建/工具
- perf: 性能优化

作用域（scope）:
- plugin: 插件模块
- runtime: 运行时模块
- material-symbols: 图标库模块
- example: 示例项目

示例:
feat(plugin): add tree shaking support
fix(plugin): resolve cache path issues on Windows
docs(readme): update installation guide
refactor(plugin): migrate to multi-module architecture
```

---

## 贡献者指南

### 准备工作

1. Fork 仓库到你的 GitHub 账户
2. Clone 到本地：
   ```bash
   git clone https://github.com/YOUR_USERNAME/SymbolCraft.git
   cd SymbolCraft
   ```

3. 配置上游仓库：
   ```bash
   git remote add upstream https://github.com/kingsword09/SymbolCraft.git
   ```

### 开发流程

1. 创建功能分支
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. 进行开发和测试
   ```bash
   ./gradlew build
   ./gradlew publishAllToMavenLocal
   cd example && ./gradlew generateSymbolCraftIcons
   ```

3. 提交更改
   ```bash
   git add .
   git commit -m "feat(plugin): add your feature description"
   ```

4. 推送并创建 Pull Request
   ```bash
   git push origin feature/your-feature-name
   ```

### Pull Request 检查清单

- [ ] 代码遵循 Kotlin 编码规范
- [ ] 添加/更新相关文档
- [ ] 添加/更新测试（如果有）
- [ ] 本地测试通过
- [ ] 示例项目可正常运行
- [ ] PR 描述清晰
- [ ] 提交信息遵循规范

---

## 资源链接

### 官方资源
- **GitHub 仓库**: https://github.com/kingsword09/SymbolCraft
- **Gradle Plugin Portal**: https://plugins.gradle.org/plugin/io.github.kingsword09.symbolcraft
- **Maven Central**: https://central.sonatype.com/artifact/io.github.kingsword09/symbolcraft

### 相关工具
- **Material Symbols 浏览器**: https://marella.github.io/material-symbols/demo/
- **Material Symbols 官方**: https://fonts.google.com/icons
- **svg-to-compose 库**: https://github.com/DevSrSouza/svg-to-compose

### 文档
- **用户文档（英文）**: [README.md](README.md)
- **用户文档（中文）**: [README_ZH.md](README_ZH.md)
- **开发文档**: [AGENTS.md](AGENTS.md)（本文件）
- **插件模块文档**: [symbolcraft-plugin/README.md](symbolcraft-plugin/README.md)
- **运行时模块文档**: [symbolcraft-runtime/README.md](symbolcraft-runtime/README.md)
- **图标库模块文档**: [symbolcraft-material-symbols/README.md](symbolcraft-material-symbols/README.md)

---

## 联系方式

- **维护者**: [@kingsword09](https://github.com/kingsword09)
- **Email**: kingsword09@gmail.com
- **问题反馈**: [GitHub Issues](https://github.com/kingsword09/SymbolCraft/issues)

---

## 更新日志

### v0.4.0 (规划中)
- 🏗️ **架构重构**: 迁移到 Monorepo 多模块架构
  - `symbolcraft-plugin`: Gradle 插件
  - `symbolcraft-runtime`: 运行时库（规划中）
  - `symbolcraft-material-symbols`: 预生成图标库（规划中）
- 🌳 **Tree Shaking**: 静态代码分析，只生成使用的图标（规划中）
- 📊 **性能监控**: 生成时间、下载速度、缓存命中率统计（规划中）
- 🔧 **错误处理增强**: 详细错误分类和恢复机制（规划中）

### v0.3.2 (当前)
- 🔧 修复构建问题
- 📚 文档改进

### v0.3.1
- 🛡️ **安全强化**: 阻止外部 SVG 中的 XXE 与路径遍历攻击，新增内容类型与尺寸校验
- ♻️ **任务拆分**: `GenerateSymbolsTask` 拆分为更小的步骤
- 📚 **文档增强**: 增补关键常量和默认值的设计

### v0.3.0
- 🔄 **多变体外部图标**: `styleParam { values(...) }` 支持笛卡尔积组合
- ⚡ **指数退避重试**: SVG 下载器支持指数退避重试策略
- 🔗 **官方 CDN**: Material Symbols 默认切换到 Google Fonts 官方 CDN
- ⚙️ **配置缓存修复**: 解决 Gradle 配置缓存序列化问题
- 🏷️ **命名转换重构**: 重写 IconNameTransformer

### v0.2.1
- 🔥 **重大重构**: 插件重命名为 SymbolCraft
- 🎉 **多图标库支持**: Material Symbols + Bootstrap Icons + Heroicons + 自定义 URL
- 🏷️ **灵活命名**: 支持多种命名规则
- ⚡ **配置重试**: 添加 maxRetries 和 retryDelayMs 配置
- 📚 **Dokka V2**: 完整的 API 文档生成支持

---

**最后更新**: 2025-10-19
**文档版本**: 3.0.0 (Monorepo 架构)
