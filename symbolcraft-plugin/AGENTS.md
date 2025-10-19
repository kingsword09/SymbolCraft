# SymbolCraft Plugin - 开发指南

## 模块概述

**symbolcraft-plugin** 是 SymbolCraft 的核心 Gradle 插件模块，负责从多个图标库按需生成 Compose ImageVector 图标。

- **版本**: v0.4.0
- **类型**: Gradle Plugin
- **状态**: ✅ 已发布到 Gradle Plugin Portal 和 Maven Central
- **语言**: Kotlin 2.0.0
- **最低 Gradle 版本**: 8.7+

---

## 核心特性

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
| svg-to-compose | 0.1.0 | SVG 转换库 |

---

## 模块结构

```
symbolcraft-plugin/
├── build.gradle.kts                    # 插件构建配置
├── README.md                           # 用户文档（英文）
├── README_ZH.md                        # 用户文档（中文）
├── AGENTS.md                           # 本文件（开发指南）
└── src/main/kotlin/io/github/kingsword09/symbolcraft/
    ├── plugin/                         # Gradle 插件核心
    │   ├── SymbolCraftPlugin.kt        # 插件入口，注册任务
    │   ├── SymbolCraftExtension.kt     # DSL 配置接口
    │   └── NamingConfig.kt             # 命名配置
    │
    ├── tasks/                          # Gradle 任务
    │   ├── GenerateSymbolsTask.kt      # 核心生成任务 (@CacheableTask)
    │   ├── CleanSymbolsCacheTask.kt    # 清理缓存任务
    │   ├── CleanSymbolsIconsTask.kt    # 清理生成文件任务
    │   └── ValidateSymbolsConfigTask.kt # 配置验证任务
    │
    ├── download/                       # 下载模块
    │   └── SvgDownloader.kt            # 智能 SVG 下载器（协程并行 + 重试）
    │
    ├── converter/                      # 转换模块
    │   ├── Svg2ComposeConverter.kt     # SVG 到 Compose 转换器
    │   └── IconNameTransformer.kt      # 图标命名转换器
    │
    ├── model/                          # 数据模型
    │   └── IconConfig.kt               # 图标配置接口和实现
    │
    └── utils/                          # 工具类
        └── PathUtils.kt                # 路径工具
```

---

## 核心组件说明

### 1. SymbolCraftPlugin (插件入口)

**位置**: `plugin/SymbolCraftPlugin.kt:44`

**职责**:
- 注册 `symbolCraft` DSL 扩展
- 注册 Gradle 任务（生成、清理、验证）
- 自动添加任务依赖：在 Kotlin 编译之前生成图标

**关键任务**:
- `generateSymbolCraftIcons` - 生成所有配置的图标
- `cleanSymbolCraftCache` - 清理 SVG 缓存
- `cleanSymbolCraftIcons` - 清理生成的图标文件
- `validateSymbolCraftConfig` - 验证配置

### 2. SymbolCraftExtension (DSL 配置)

**位置**: `plugin/SymbolCraftExtension.kt:27`

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

**便捷方法**:
- `materialSymbol()` / `materialSymbols()` - 配置 Material Symbols 图标
- `externalIcon()` / `externalIcons()` - 配置外部图标库图标
- `localIcons()` - 配置本地 SVG 文件
- `standardWeights()` - 标准权重（400, 500, 700）
- `allVariants()` - 所有变体（outlined, rounded, sharp）
- `bothFills()` - 填充和未填充
- `naming {}` - 配置命名规则

### 3. GenerateSymbolsTask (核心生成任务)

**位置**: `tasks/GenerateSymbolsTask.kt:63`

**特性**:
- `@CacheableTask` - 支持 Gradle 任务缓存
- 配置缓存兼容 - 使用 Provider API
- 智能缓存清理 - 相对路径启用，绝对路径跳过
- 可配置重试 - maxRetries 和 retryDelayMs

**关键流程**:
```
配置解析 → 清理旧文件 → 并行下载 SVG → 命名转换 → 转换为 Compose →
清理未使用缓存 → 生成统计
```

### 4. SvgDownloader (SVG 下载器)

**位置**: `download/SvgDownloader.kt:39`

**特性**:
- 智能 7 天缓存
- 并行下载（Kotlin 协程）
- 指数退避重试
- 多图标库缓存隔离
- 支持相对/绝对路径

### 5. Svg2ComposeConverter (SVG 转换器)

**位置**: `converter/Svg2ComposeConverter.kt:24`

**职责**:
- SVG 到 ImageVector 转换
- 确定性代码生成
- 可选 @Preview 生成
- 使用 svg-to-compose 库

### 6. IconNameTransformer (命名转换器)

**位置**: `converter/IconNameTransformer.kt:10`

**支持的命名规则**:
- PascalCase (默认)
- camelCase
- snake_case
- kebab-case
- UPPER_CASE
- 自定义前缀/后缀
- 自定义转换器

---

## 开发工作流

### 本地开发测试

#### 1. 修改插件代码
```bash
# 编辑源文件
vim symbolcraft-plugin/src/main/kotlin/io/github/kingsword09/symbolcraft/tasks/GenerateSymbolsTask.kt
```

#### 2. 发布到本地 Maven
```bash
# 从根目录发布插件模块
./gradlew :symbolcraft-plugin:publishToMavenLocal

# 或使用统一发布命令
./gradlew publishAllToMavenLocal
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
./gradlew :symbolcraft-plugin:build    # 仅构建插件
./gradlew :symbolcraft-plugin:test     # 运行测试
```

### 2. 本地发布（测试用）
```bash
./gradlew :symbolcraft-plugin:publishToMavenLocal
```

### 3. 发布到 Maven Central
```bash
./gradlew :symbolcraft-plugin:publishToMavenCentral
```

### 4. 发布到 Gradle Plugin Portal
```bash
./gradlew :symbolcraft-plugin:publishPlugins
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

# Gradle Plugin Portal 配置
gradle.publish.key=<API Key>
gradle.publish.secret=<API Secret>
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
cacheDirectory.set("/var/tmp/symbolcraft")
// Windows
cacheDirectory.set("""C:\Temp\SymbolCraft""")
```
- ✅ 跨项目共享
- ⚠️ 跳过自动清理（防止冲突）

---

## 测试现状

### 当前测试
✅ **部分单元测试** - 已有测试:
- `IconNameTransformerTest` - 命名转换测试
- `GenerateSymbolsTaskTest` - 任务测试（基础）
- `LocalIconsBuilderTest` - 本地图标构建器测试

### 测试覆盖目标
- [ ] `SvgDownloader` 测试 - 下载逻辑、缓存、重试机制
- [ ] `Svg2ComposeConverter` 测试 - SVG 转换、代码生成
- [ ] `IconConfig` 实现类测试
- [ ] 集成测试
- [ ] 核心组件测试覆盖率 > 80%

---

## 待办事项和改进方向

### 🔴 高优先级

#### 1. 补全单元测试
- [ ] `SvgDownloader` 完整测试套件
- [ ] `Svg2ComposeConverter` 测试
- [ ] 集成测试框架

#### 2. Tree Shaking 功能实现
**状态**: 📋 设计阶段

**目标**: 只生成实际使用的图标，避免生成大量未使用的图标

**技术方案**: 静态代码扫描
```kotlin
symbolCraft {
    treeShaking {
        enabled.set(true)
        scanDirectories.addAll("src/commonMain/kotlin")
        strategy.set(ScanStrategy.USAGE_BASED)
        alwaysInclude.addAll("home", "search", "settings")
    }
}
```

**核心组件**:
```kotlin
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

@CacheableTask
abstract class AnalyzeIconUsageTask : DefaultTask() {
    @get:InputFiles
    abstract val scanDirectories: SetProperty<File>

    @get:OutputFile
    abstract val analysisResultFile: RegularFileProperty
}
```

### 🟡 中优先级

#### 3. 性能监控
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

#### 4. 错误处理增强
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

### 🟢 低优先级

#### 5. IntelliJ IDEA 插件
**功能**:
- 可视化配置向导
- 图标搜索和预览
- 代码辅助（自动补全、预览 Inlay Hints）
- 快捷操作（右键菜单生成图标）

#### 6. CLI 工具
```bash
symbolcraft search "home"           # 搜索图标
symbolcraft add home --weight 400   # 添加图标到配置
symbolcraft init                    # 交互式初始化
```

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
4. 更新所有文档

### 添加新的图标库支持

1. 在 `model/IconConfig.kt` 中创建新的 `IconConfig` 实现
2. 实现必需的方法：`buildUrl()`、`getCacheKey()`、`getSignature()`
3. 在 `SymbolCraftExtension.kt` 中添加相应的 DSL 方法
4. 更新文档和示例

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
   ./gradlew :symbolcraft-plugin:build
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
- **Maven Central**: https://central.sonatype.com/artifact/io.github.kingsword09/symbolcraft-plugin

### 相关工具
- **Material Symbols 浏览器**: https://marella.github.io/material-symbols/demo/
- **Material Symbols 官方**: https://fonts.google.com/icons
- **svg-to-compose 库**: https://github.com/DevSrSouza/svg-to-compose

### 文档
- **用户文档（英文）**: [README.md](README.md)
- **用户文档（中文）**: [README_ZH.md](README_ZH.md)
- **开发文档**: [AGENTS.md](AGENTS.md)（本文件）
- **根项目文档**: [../AGENTS.md](../AGENTS.md)

---

## 联系方式

- **维护者**: [@kingsword09](https://github.com/kingsword09)
- **Email**: kingsword09@gmail.com
- **问题反馈**: [GitHub Issues](https://github.com/kingsword09/SymbolCraft/issues)

---

**最后更新**: 2025-10-19
**文档版本**: 1.0.0 (Plugin Module)
