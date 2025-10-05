# SymbolCraft - 开发指南

## 项目概述

**SymbolCraft** 是一个用于 Kotlin Multiplatform 项目的 Gradle 插件，支持按需生成 Material Symbols 图标。

- **版本**: v0.1.2
- **状态**: ✅ 已发布到 Gradle Plugin Portal 和 Maven Central
- **语言**: Kotlin 2.0.0
- **最低 Gradle 版本**: 8.0+
- **仓库**: https://github.com/kingsword09/SymbolCraft

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.0.0 | 核心语言 |
| Gradle | 8.0+ | 构建系统 |
| Kotlin Coroutines | 1.8.1 | 并行下载 |
| Ktor Client | 2.3.12 | HTTP 客户端 |
| Kotlinx Serialization | - | JSON 序列化 |
| svg-to-compose | 0.11.1 | SVG 转换库 |

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
│   │   ├── MaterialSymbolsPlugin.kt    # 插件入口，注册任务
│   │   ├── MaterialSymbolsExtension.kt # DSL 配置接口
│   │   ├── GenerateSymbolsTask.kt      # 核心生成任务 (@CacheableTask)
│   │   ├── CleanSymbolsCacheTask.kt    # 清理缓存任务
│   │   ├── ValidateSymbolsConfigTask.kt # 配置验证任务
│   │   └── PathUtils.kt                # 路径工具类
│   │
│   ├── download/                       # 下载模块
│   │   └── SvgDownloader.kt            # 智能 SVG 下载器（协程并行）
│   │
│   ├── converter/                      # 转换模块
│   │   └── Svg2ComposeConverter.kt     # SVG 到 Compose 转换器
│   │
│   └── model/                          # 数据模型
│       └── SymbolStyle.kt              # 样式模型（包含枚举定义）
│
├── example/                            # 示例项目（Compose Multiplatform）
│   ├── composeApp/                     # 主应用
│   │   ├── src/
│   │   │   ├── androidMain/           # Android 平台代码
│   │   │   ├── iosMain/               # iOS 平台代码
│   │   │   ├── jvmMain/               # Desktop 平台代码
│   │   │   └── commonMain/            # 通用代码
│   │   │       ├── kotlin/
│   │   │       │   └── generated/symbols/  # 生成的图标
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

### 1. **MaterialSymbolsPlugin** (插件入口)
**位置**: `src/main/kotlin/.../plugin/MaterialSymbolsPlugin.kt`

**职责**:
- 注册 `materialSymbols` DSL 扩展
- 注册 Gradle 任务：
  - `generateMaterialSymbols` - 生成图标
  - `cleanSymbolsCache` - 清理缓存
  - `cleanGeneratedSymbols` - 清理生成的文件
  - `validateSymbolsConfig` - 验证配置

**关键代码**:
```kotlin
class MaterialSymbolsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("materialSymbols", MaterialSymbolsExtension::class.java)

        project.tasks.register("generateMaterialSymbols", GenerateSymbolsTask::class.java) {
            // 配置任务...
        }
    }
}
```

---

### 2. **MaterialSymbolsExtension** (DSL 配置)
**位置**: `src/main/kotlin/.../plugin/MaterialSymbolsExtension.kt`

**职责**:
- 提供用户友好的 DSL API
- 管理符号配置（symbols）
- 提供便捷配置方法：
  - `symbol()` / `symbols()` - 配置单个/多个图标
  - `standardWeights()` - 标准权重（400, 500, 700）
  - `allVariants()` - 所有变体（outlined, rounded, sharp）
  - `bothFills()` - 填充和未填充

**配置选项**:
```kotlin
abstract class MaterialSymbolsExtension {
    abstract val packageName: Property<String>              // 包名
    abstract val outputDirectory: Property<String>          // 输出目录
    abstract val cacheEnabled: Property<Boolean>            // 缓存开关
    abstract val cacheDirectory: Property<String>           // 缓存目录
    abstract val generatePreview: Property<Boolean>         // 生成预览
    abstract val forceRegenerate: Property<Boolean>         // 强制重新生成
    // ...
}
```

---

### 3. **GenerateSymbolsTask** (核心生成任务)
**位置**: `src/main/kotlin/.../plugin/GenerateSymbolsTask.kt`

**职责**:
- 解析用户配置
- 并行下载 SVG 文件（使用 Kotlin 协程）
- 调用转换器生成 Compose 代码
- 管理缓存和增量构建
- 清理未使用的缓存文件

**特性**:
- `@CacheableTask` - 支持 Gradle 任务缓存
- 配置缓存兼容 - 避免访问 Project 对象
- 智能缓存清理 - 避免冲突（共享缓存时跳过）

**关键流程**:
```
配置解析 → 清理旧文件 → 并行下载 SVG → 转换为 Compose → 清理未使用缓存 → 生成统计
```

---

### 4. **SvgDownloader** (智能下载器)
**位置**: `src/main/kotlin/.../download/SvgDownloader.kt`

**职责**:
- 从 esm.sh CDN 下载 SVG 文件
- 管理 7 天有效期的缓存
- 支持并行下载（Kotlin 协程）
- 缓存元数据管理（时间戳、URL、哈希）

**特性**:
- 缓存命中检测
- 自动过期清理
- 进度跟踪
- 错误重试

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

### 6. **SymbolStyle** (数据模型)
**位置**: `src/main/kotlin/.../model/SymbolStyle.kt`

**包含枚举**:
- `SymbolWeight` - 图标权重（W100-W700，THIN-BOLD）
- `SymbolVariant` - 图标变体（OUTLINED, ROUNDED, SHARP）
- `SymbolFill` - 填充状态（FILLED, UNFILLED）

**数据类**:
```kotlin
data class SymbolStyle(
    val weight: Int,
    val variant: SymbolVariant,
    val fill: SymbolFill,
    val opticalSize: Int = 24,
    val grade: Int = 0
)
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
   ./gradlew generateMaterialSymbols --info
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

1. **SVG 下载缓存** (`build/material-symbols-cache/svg-cache/`)
   - 有效期：7 天
   - 包含：SVG 文件 + JSON 元数据
   - 元数据字段：`timestamp`, `url`, `hash`

2. **Gradle 任务缓存**
   - 基于配置哈希值检测变更
   - `@CacheableTask` 注解支持

3. **配置缓存**
   - 使用 Provider API
   - 避免任务执行时访问 Project

### 缓存路径支持

**相对路径（默认）**:
```kotlin
cacheDirectory.set("material-symbols-cache")  // → build/material-symbols-cache/
```
- ✅ 自动清理未使用的缓存
- ✅ 项目隔离

**绝对路径（共享缓存）**:
```kotlin
cacheDirectory.set("/var/tmp/symbolcraft")  // → /var/tmp/symbolcraft/
```
- ✅ 跨项目共享
- ⚠️ 跳过自动清理（防止冲突）

---

## 测试现状

### 当前状态
❌ **无单元测试** - `src/test/` 目录不存在

### 测试计划（建议）

**高优先级**:
1. ✅ `SvgDownloaderTest` - 缓存逻辑、下载重试
2. ✅ `SymbolStyleTest` - 样式解析、枚举转换
3. ✅ `PathUtilsTest` - 路径解析（相对/绝对）
4. ✅ `MaterialSymbolsExtensionTest` - DSL 配置构建

**中优先级**:
5. ⚪ `GenerateSymbolsTaskTest` - 任务执行集成测试
6. ⚪ `Svg2ComposeConverterTest` - 转换器输出验证

**测试框架**:
- JUnit 5 (已配置 `kotlin.test`)
- Gradle TestKit (用于插件集成测试)

---

## 待办事项和改进方向

### 🔴 高优先级

1. **添加单元测试**
   - [ ] 创建 `src/test/kotlin` 目录
   - [ ] 编写核心组件测试
   - [ ] 配置 CI/CD 测试流水线

2. **改进错误处理**
   - [ ] 更详细的错误消息
   - [ ] 网络失败自动重试机制增强
   - [ ] 配置验证前置（避免运行时错误）

3. **性能监控**
   - [ ] 添加生成时间统计
   - [ ] 下载速度统计
   - [ ] 缓存命中率报告

### 🟡 中优先级

4. **功能增强**
   - [ ] 图标搜索功能（CLI）
   - [ ] 图标使用分析报告
   - [ ] 支持自定义图标源

5. **开发者体验**
   - [ ] 生成 KDoc API 文档
   - [ ] 添加视频教程/GIF 演示
   - [ ] 创建项目模板

6. **示例扩展**
   - [ ] 纯 Android 示例
   - [ ] 纯 Desktop 示例
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
    implementation("br.com.devsrsouza.compose.icons:svg-to-compose:0.11.1")

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

1. 在 `MaterialSymbolsPlugin.kt` 中注册任务
2. 创建任务类继承 `DefaultTask`
3. 使用 `@TaskAction` 注解标记执行方法

### 添加新的配置选项

1. 在 `MaterialSymbolsExtension.kt` 中添加 `Property<T>`
2. 在 `GenerateSymbolsTask.kt` 中读取配置
3. 更新文档（README.md 和 README_ZH.md）

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

---

## 调试技巧

### 启用详细日志
```bash
./gradlew generateMaterialSymbols --info       # 信息级别
./gradlew generateMaterialSymbols --debug      # 调试级别
./gradlew generateMaterialSymbols --stacktrace # 堆栈跟踪
```

### 禁用配置缓存（调试用）
```bash
./gradlew generateMaterialSymbols --no-configuration-cache
```

### 强制重新运行任务
```bash
./gradlew generateMaterialSymbols --rerun-tasks
```

### 查看任务依赖
```bash
./gradlew generateMaterialSymbols --dry-run
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
   cd example && ./gradlew generateMaterialSymbols
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

### v0.1.2 (最新)
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

**最后更新**: 2025-10-06
**文档版本**: 1.0.0
