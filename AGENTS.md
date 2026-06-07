# SymbolCraft - 开发指南

## 项目概述

**SymbolCraft** 是一个用于 Kotlin Multiplatform 项目的 Gradle 插件，支持从多个图标库（Material Symbols、Bootstrap Icons、Heroicons 等）按需生成图标。

- **版本**: v0.5.0
- **状态**: ✅ 已发布到 Gradle Plugin Portal 和 Maven Central
- **语言**: Kotlin 2.0.0
- **最低 Gradle 版本**: 8.0+
- **仓库**: https://github.com/kingsword09/SymbolCraft

### 核心特性

- 🚀 **多图标库支持** - Material Symbols、Bootstrap Icons、Heroicons、自定义 URL 模板
- 💾 **智能缓存** - 7天有效期的 SVG 缓存，支持相对/绝对路径
- ⚡ **并行下载** - Kotlin 协程并行下载，支持可配置的重试机制
- 🎯 **确定性构建** - Git 友好的确定性代码生成
- 🏷️ **灵活命名** - 支持多种命名规则（PascalCase、camelCase、snake_case 等）
- 👀 **Compose 预览** - 自动生成 @Preview 函数

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.0.0 | 核心语言 |
| Gradle | 8.0+ | 构建系统 |
| Kotlin Coroutines | 1.8.1 | 并行下载 |
| Ktor Client | 2.3.12 | HTTP 客户端 |
| Kotlinx Serialization | - | JSON 序列化 |
| svg-to-compose | 0.1.0 | SVG 转换库(io.github.kingsword09 fork) |

---

## 项目结构

```
SymbolCraft/
├── build.gradle.kts                    # 插件构建配置
├── gradle.properties                   # Gradle 配置
├── settings.gradle.kts                 # Gradle 设置
├── libs.versions.toml                  # 版本目录
│
├── src/main/kotlin/io/github/kingsword09/symbolcraft/
│   ├── plugin/                         # Gradle 插件核心
│   │   ├── SymbolCraftPlugin.kt        # 插件入口，注册任务
│   │   ├── SymbolCraftExtension.kt     # DSL 配置接口
│   │   └── NamingConfig.kt             # 命名配置
│   │
│   ├── tasks/                          # Gradle 任务
│   │   ├── GenerateSymbolsTask.kt      # 核心生成任务 (@CacheableTask)
│   │   ├── CleanSymbolsCacheTask.kt    # 清理缓存任务
│   │   ├── CleanSymbolsIconsTask.kt    # 清理生成文件任务
│   │   └── ValidateSymbolsConfigTask.kt # 配置验证任务
│   │
│   ├── download/                       # 下载模块
│   │   └── SvgDownloader.kt            # 智能 SVG 下载器（协程并行 + 重试）
│   │
│   ├── converter/                      # 转换模块
│   │   ├── Svg2ComposeConverter.kt     # SVG 到 Compose 转换器
│   │   └── IconNameTransformer.kt      # 图标命名转换器
│   │
│   ├── model/                          # 数据模型
│   │   └── IconConfig.kt               # 图标配置接口和实现
│   │
│   └── utils/                          # 工具类
│       └── PathUtils.kt                # 路径工具
│
├── example/                            # 示例项目（Compose Multiplatform）
│   ├── composeApp/                     # 主应用
│   │   ├── src/
│   │   │   ├── androidMain/           # Android 平台代码
│   │   │   ├── iosMain/               # iOS 平台代码
│   │   │   ├── jvmMain/               # Desktop 平台代码
│   │   │   └── commonMain/            # 通用代码
│   │   │       ├── generated/symbols/ # 生成的图标 source root
│   │   │       ├── kotlin/
│   │   │       └── composeResources/
│   │   └── build.gradle.kts            # 使用 SymbolCraft 插件
│   └── iosApp/                         # iOS 应用
│
├── README.md                           # 英文文档
├── README_ZH.md                        # 中文文档
└── AGENTS.md                           # 本文件（开发指南）
```

---

## 核心组件说明

### 1. **SymbolCraftPlugin** (插件入口)
**位置**: `src/main/kotlin/io/github/kingsword09/symbolcraft/plugin/SymbolCraftPlugin.kt`

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
**位置**: `src/main/kotlin/.../plugin/SymbolCraftExtension.kt`

**职责**:
- 提供用户友好的 DSL API
- 管理多图标库的配置（Material Symbols、外部图标库）
- 提供便捷配置方法：
  - `materialSymbol()` / `materialSymbols()` - 配置 Material Symbols 图标
  - `externalIcon()` / `externalIcons()` - 配置外部图标库图标
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
    
    // Builder 类
    // MaterialSymbolsBuilder - Material Symbols 配置
    // ExternalIconBuilder - 外部图标配置
}
```

---

### 3. **GenerateSymbolsTask** (核心生成任务)
**位置**: `src/main/kotlin/.../tasks/GenerateSymbolsTask.kt`

**职责**:
- 解析用户配置（Material Symbols + 外部图标库）
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

### 4. **SvgDownloader** (智能下载器)
**位置**: `src/main/kotlin/.../download/SvgDownloader.kt`

**职责**:
- 从多个源下载 SVG 文件（Material Symbols、外部 URL）
- 管理 7 天有效期的缓存
- 支持并行下载（Kotlin 协程）
- 缓存元数据管理（时间戳、URL、哈希）
- 配置化重试机制

**特性**:
- 缓存命中检测
- 自动过期清理
- 进度跟踪
- 可配置的错误重试（指数退缩）

---

### 5. **Svg2ComposeConverter** (SVG 转换器)
**位置**: `src/main/kotlin/.../converter/Svg2ComposeConverter.kt`

**职责**:
- 使用 `svg-to-compose` 库将 SVG 转换为 Compose ImageVector
- 生成确定性代码（移除时间戳、标准化浮点数）
- 可选生成 Compose Preview 函数
- 生成 `__MaterialSymbols.kt` 访问对象

**输出文件**:
```
{packageName}/materialsymbols/
├── SearchW400Outlined.kt       # 单个图标
├── HomeW500RoundedFill.kt
└── ...

{packageName}/__MaterialSymbols.kt  # 访问对象
```

---

### 6. **IconConfig** (图标配置接口)
**位置**: `src/main/kotlin/.../model/IconConfig.kt`

**职责**:
- 定义图标库配置的通用接口
- 支持多图标库扩展

**主要实现**:
- `MaterialSymbolsConfig` - Material Symbols 配置
  - 包含: SymbolWeight、SymbolVariant、SymbolFill 枚举
  - 使用 Google Fonts 官方 CDN
- `ExternalIconConfig` - 外部图标配置
  - 支持 URL 模板 + 样式参数
  - 支持多值参数（笛卡尔积）

**接口方法**:
```kotlin
interface IconConfig {
    val libraryId: String
    fun buildUrl(iconName: String): String
    fun getCacheKey(iconName: String): String
    fun getSignature(): String
}
```

---

### 7. **NamingConfig** (命名配置)
**位置**: `src/main/kotlin/.../plugin/NamingConfig.kt`

**职责**:
- 提供图标类名命名转换配置
- 支持预设和自定义转换器

**预设命名规则**:
- `pascalCase()` - PascalCase (默认)
- `camelCase()` - camelCase
- `snakeCase()` - snake_case / SCREAMING_SNAKE
- `kebabCase()` - kebab-case
- `lowerCase()` / `upperCase()` - 全小/大写
- `customTransformer()` - 自定义逻辑

**配置选项**:
```kotlin
abstract class NamingConfig {
    abstract val namingConvention: Property<NamingConvention>
    abstract val suffix: Property<String>
    abstract val prefix: Property<String>
    abstract val removePrefix: Property<String>
    abstract val removeSuffix: Property<String>
    abstract val transformer: Property<IconNameTransformer>
}
```

---

### 8. **IconNameTransformer** (命名转换器)
**位置**: `src/main/kotlin/.../converter/IconNameTransformer.kt`

**职责**:
- 执行具体的命名转换逻辑
- 支持多种命名约定
- 提供扩展点供用户自定义

**核心方法**:
```kotlin
abstract class IconNameTransformer {
    abstract fun transform(fileName: String): String
    open fun getSignature(): String  // 用于缓存签名
}
```

---

## 开发工作流

### 本地开发测试

1. **修改插件代码**
   ```bash
   # 编辑 src/main/kotlin/ 下的源文件
   vim src/main/kotlin/io/github/kingsword09/symbolcraft/plugin/GenerateSymbolsTask.kt
   ```

2. **发布到本地 Maven**
   ```bash
   ./gradlew publishToMavenLocal
   ```

3. **在示例项目中测试**
   ```bash
   cd example
   ./gradlew generateSymbolCraftIcons --info
   ./gradlew :composeApp:run  # Desktop
   ```

4. **清理和重新构建**
   ```bash
   ./gradlew clean build
   ```

---

## 构建和发布流程

### 1. 本地构建
```bash
./gradlew build                    # 构建插件
./gradlew test                     # 运行测试（当前无测试）
./gradlew publishToMavenLocal      # 发布到本地 Maven
```

### 2. 发布到 Gradle Plugin Portal
```bash
./gradlew publishPlugins           # 需要配置 API key
```

### 3. 发布到 Maven Central
```bash
./gradlew publishToMavenCentral    # 需要配置签名
```

**配置要求**:
- `gradle.properties` 或环境变量：
  - `SIGNING_KEY` - GPG 签名密钥
  - `SIGNING_PASSWORD` - 签名密码
  - `mavenCentralUsername` - Maven Central 用户名
  - `mavenCentralPassword` - Maven Central 密码

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
❌ **无单元测试** - `src/test/` 目录不存在

---

## 待办事项和改进方向

### 🔴 高优先级

1. **添加单元测试**
   - [ ] 创建 `src/test/kotlin` 目录
   - [ ] 编写核心组件测试（IconNameTransformer、IconConfig 等）
   - [ ] 配置 CI/CD 测试流水线
   - ✅ 已完成：IconNameTransformerTest

2. **改进错误处理**
   - ✅ 已完成：可配置的重试机制（maxRetries、retryDelayMs）
   - [ ] 更详细的错误消息和分类
   - [ ] 配置验证前置（避免运行时错误）

3. **性能监控**
   - [ ] 添加生成时间统计
   - [ ] 下载速度统计
   - [ ] 缓存命中率报告

### 🟡 中优先级

4. **功能增强**
   - ✅ 已完成：多图标库支持（Material Symbols + 外部图标库）
   - ✅ 已完成：灵活命名配置（NamingConfig）
   - [ ] 图标搜索功能（CLI）
   - [ ] 图标使用分析报告

5. **开发者体验**
   - ✅ 已完成：Dokka V2 文档配置
   - [ ] 添加更多 KDoc 注释
   - [ ] 添加视频教程/GIF 演示
   - [ ] 创建项目模板

6. **示例扩展**
   - ✅ 已完成：Compose Multiplatform 示例（Android + iOS + Desktop）
   - [ ] 纯 Android 示例
   - [ ] 最佳实践指南

### 🟢 低优先级

7. **生态工具**
   - [ ] IntelliJ IDEA 插件（可视化配置）
   - [ ] Gradle 配置生成向导
   - [ ] 图标浏览器 GUI

---

## 依赖管理

### 核心依赖

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-cio:2.3.12")
    implementation("io.github.kingsword09:svg-to-compose:0.1.0")

    compileOnly("org.gradle:gradle-api")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin")
}
```

### 版本更新策略

- 定期检查依赖更新：`./gradlew dependencyUpdates`
- 测试新版本兼容性
- 保持 Kotlin 和 Gradle 版本同步

---

## 常见开发任务

### 添加新的 Gradle 任务

1. 在 `SymbolCraftPlugin.kt` 中注册任务
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

编辑 `src/main/kotlin/.../download/SvgDownloader.kt`：
- 修改 CDN URL
- 调整缓存策略
- 增强错误处理

### 修改代码生成

编辑 `src/main/kotlin/.../converter/Svg2ComposeConverter.kt`：
- 调整输出格式
- 修改预览生成
- 自定义文件命名

### 运行代码格式化

- `./gradlew ktfmtFormat`：使用 ktfmt 对所有 Kotlin 源码进行格式化。
- `./gradlew ktfmtCheck`：验证格式是否符合 ktfmt 规则；该任务已接入 `check` 流水线。

### CI 格式检查策略

- GitHub Actions 会在 `build` 工作流的最开始执行 `./gradlew ktfmtCheck`，若格式不合规会立即失败并阻止后续任务。
- 本地开发默认不强制安装 Git hook，提交前请自行运行 `./gradlew ktfmtFormat`（自动修复）或 `./gradlew ktfmtCheck`（仅校验）以避免 CI 失败。

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

---

## Git 工作流

### 分支策略
- `main` - 稳定发布分支
- `develop` - 开发分支（如有）
- `feature/*` - 功能分支
- `fix/*` - 修复分支

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

示例:
feat(downloader): add retry mechanism for failed downloads
fix(cache): resolve path issues on Windows
docs(readme): update installation guide
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
   ./gradlew publishToMavenLocal
   cd example && ./gradlew generateSymbolCraftIcons
   ```

3. 提交更改
   ```bash
   git add .
   git commit -m "feat: add your feature description"
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

---

## 联系方式

- **维护者**: [@kingsword09](https://github.com/kingsword09)
- **Email**: kingsword09@gmail.com
- **问题反馈**: [GitHub Issues](https://github.com/kingsword09/SymbolCraft/issues)

---

## 更新日志

### v0.5.0 (最新)
- ⚠️ **破坏性变更**: 内置 `materialSymbol()` / `materialSymbols()` 生成的 filled Material Symbols 名称从 `...fill1` 改为 `...Fill`，避免把 Google Fonts URL 后缀泄漏到 Kotlin API。
- 📚 **多来源文档**: README 按图标来源补齐内置 Material Symbols、外部 CDN/npm SVG 包、多变体外部源和本地 SVG 的配置示例。
- 🧪 **示例同步**: example 重新生成 filled Material Symbols，并同步 `HomeW400OutlinedFill`、`SettingsW500RoundedFill` 等新引用。

### v0.4.0
- 👀 **Compose Preview 配置**: 新增 `previewAnnotationClass`，支持现代 Compose Multiplatform 默认 AndroidX 预览注解，也兼容旧版 JetBrains 预览注解。
- 🧹 **示例 source root 调整**: example 生成图标目录移动到 `src/commonMain/generated/symbols`，减少 IDE 对 package path 的误报。
- 🔗 **示例外部源更新**: example 的官方 Material Symbols 外部源切换到 esm.sh，并使用 `-fill` 变体配置。

### v0.3.1
- 🛡️ **安全强化**: 阻止外部 SVG 中的 XXE 与路径遍历攻击，新增内容类型与尺寸校验，并全面清理危险路径字符。
- ♻️ **任务拆分**: `GenerateSymbolsTask` 拆分为更小的步骤，日志输出更具可读性，也为后续单元测试做好铺垫。
- 📚 **文档增强**: 增补关键常量和默认值的设计，方便贡献者快速理解配置。

### v0.3.0
- 🔄 **多变体外部图标**: `styleParam { values(...) }` 支持笛卡尔积组合，一次声明即可生成多种外部图标变体。
- ⚡ **指数退避重试**: SVG 下载器支持指数退避重试策略，网络波动下更稳健。
- 🔗 **官方 CDN**: Material Symbols 默认切换到 Google Fonts 官方 CDN，保障可用性与更新速度。
- ⚙️ **配置缓存修复**: 解决 Gradle 配置缓存序列化问题，提高增量构建兼容性。
- 🏷️ **命名转换重构**: 重写 IconNameTransformer，命名配置更加灵活可靠。

### v0.2.1
- 🔥 **重大重构**: 插件重命名为 SymbolCraft（从 MaterialSymbolsPlugin）
- 🎉 **多图标库支持**: Material Symbols + Bootstrap Icons + Heroicons + 自定义 URL
- 🏷️ **灵活命名**: 支持 PascalCase、camelCase、snake_case 等多种命名规则
- ⚡ **配置重试**: 添加 maxRetries 和 retryDelayMs 配置
- 📚 **Dokka V2**: 完整的 API 文档生成支持
- 📦 **新的 DSL**: externalIcon/externalIcons 方法
- 🧹 **更新缓存**: symbolcraft-cache 目录（从 material-symbols-cache）
- 📝 **文档改进**: 更新所有 README 和开发指南

### v0.1.2
- 🎉 支持绝对路径缓存配置
- 🧹 智能缓存清理（跳过共享缓存）
- 📝 更新文档

### v0.1.1
- 🐛 修复示例预览渲染错误
- ♻️ 重构 SymbolWeight 为枚举
- 📦 支持缓存目录的绝对路径

### v0.1.0
- 🚀 首次发布
- ✅ 核心功能完成
- 📚 完整文档
- 🎨 示例项目

---

**最后更新**: 2026-06-07
**文档版本**: 2.1.0
