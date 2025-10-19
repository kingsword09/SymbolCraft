# SymbolCraft Runtime - 开发指南

## 模块概述

**symbolcraft-runtime** 是 SymbolCraft 的运行时库模块，提供图标懒加载、缓存管理和便捷的 API 访问。

- **版本**: v0.4.0
- **类型**: Kotlin Multiplatform Library
- **状态**: 🚧 计划中（未实现）
- **语言**: Kotlin 2.0.0
- **平台支持**: Android、iOS、JVM、JS

---

## 核心特性（规划）

- 💾 **懒加载图标** - 按需加载图标以最小化内存占用
- 🚀 **LRU 缓存** - 高效的内存图标缓存管理
- 🎯 **MaterialSymbols API** - 类似 `androidx.compose.material.icons.Icons` 的流畅 API
- 📊 **性能监控** - 缓存命中率和加载统计
- 🌐 **多平台支持** - Android、iOS、JVM、JS 全平台覆盖
- 🔧 **可扩展设计** - 支持自定义图标提供者

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.0.0 | 核心语言 |
| Compose Multiplatform | 1.6.11 | UI 框架 |
| Kotlin Coroutines | 1.8.1 | 异步加载 |
| Compose Runtime | - | 响应式状态管理 |

---

## 模块结构

```
symbolcraft-runtime/
├── build.gradle.kts
├── README.md
├── AGENTS.md                          # 本文件（开发指南）
└── src/
    ├── commonMain/kotlin/io/github/kingsword09/symbolcraft/runtime/
    │   ├── MaterialSymbols.kt          # 主访问入口
    │   ├── IconLoader.kt               # 图标加载器
    │   ├── IconCache.kt                # 缓存管理
    │   ├── IconProvider.kt             # 图标提供者接口
    │   ├── LazyImageVector.kt          # 懒加载包装器
    │   ├── WeightGroup.kt              # 权重组
    │   ├── IconSpec.kt                 # 图标规格
    │   └── exceptions/
    │       └── IconNotFoundException.kt
    │
    ├── androidMain/kotlin/
    │   └── PlatformIconLoader.android.kt
    │
    ├── jvmMain/kotlin/
    │   └── PlatformIconLoader.jvm.kt
    │
    ├── iosMain/kotlin/
    │   └── PlatformIconLoader.ios.kt
    │
    └── commonTest/kotlin/
        ├── MaterialSymbolsTest.kt
        ├── IconCacheTest.kt
        └── IconLoaderTest.kt
```

---

## 核心组件设计

### 1. MaterialSymbols (主访问入口)

**位置**: `commonMain/kotlin/MaterialSymbols.kt`

**设计目标**: 提供类似 `androidx.compose.material.icons.Icons` 的 API 体验

**用法示例**:
```kotlin
// 方式1: 直接属性访问
Icon(MaterialSymbols.Outlined.W400.Home, "Home")

// 方式2: 索引访问
Icon(MaterialSymbols.Outlined.W400["search"], "Search")

// 方式3: 动态获取
Icon(
    MaterialSymbols.get(
        name = "settings",
        weight = SymbolWeight.W500,
        variant = SymbolVariant.ROUNDED
    ),
    "Settings"
)

// 方式4: 填充版本
Icon(MaterialSymbols.Rounded.W500.filled("favorite"), "Favorite")
```

**API 结构**:
```kotlin
object MaterialSymbols {
    object Outlined {
        object W100 : WeightGroup(SymbolWeight.W100, SymbolVariant.OUTLINED)
        object W200 : WeightGroup(SymbolWeight.W200, SymbolVariant.OUTLINED)
        object W300 : WeightGroup(SymbolWeight.W300, SymbolVariant.OUTLINED)
        object W400 : WeightGroup(SymbolWeight.W400, SymbolVariant.OUTLINED)
        object W500 : WeightGroup(SymbolWeight.W500, SymbolVariant.OUTLINED)
        object W600 : WeightGroup(SymbolWeight.W600, SymbolVariant.OUTLINED)
        object W700 : WeightGroup(SymbolWeight.W700, SymbolVariant.OUTLINED)
    }

    object Rounded { /* 同上 */ }
    object Sharp { /* 同上 */ }

    @Composable
    fun get(
        name: String,
        weight: SymbolWeight = SymbolWeight.W400,
        variant: SymbolVariant = SymbolVariant.OUTLINED,
        fill: Boolean = false
    ): ImageVector
}
```

### 2. IconLoader (图标加载器)

**位置**: `commonMain/kotlin/IconLoader.kt`

**职责**:
- 从预生成的类中加载图标
- 管理内存缓存
- 处理加载失败

**核心方法**:
```kotlin
object IconLoader {
    fun load(spec: IconSpec): ImageVector
    suspend fun preload(specs: List<IconSpec>)
    fun clearCache()
    fun getCacheStats(): CacheStats
}
```

**加载流程**:
```
1. 检查缓存 → 2. 从 Provider 加载 → 3. 写入缓存 → 4. 返回 ImageVector
```

### 3. IconCache (缓存管理)

**位置**: `commonMain/kotlin/IconCache.kt`

**特性**:
- LRU（Least Recently Used）策略
- 线程安全
- 缓存命中率统计

**API**:
```kotlin
class IconCache(maxSize: Int = 100) {
    fun get(spec: IconSpec): ImageVector?
    fun put(spec: IconSpec, icon: ImageVector)
    fun clear()
    fun getStats(): CacheStats
}

data class CacheStats(
    val size: Int,
    val hits: Long,
    val misses: Long,
    val hitRate: Double
)
```

### 4. IconProvider (图标提供者)

**位置**: `commonMain/kotlin/IconProvider.kt`

**设计**:
```kotlin
interface IconProvider {
    fun load(spec: IconSpec): ImageVector?

    companion object {
        val Default: IconProvider = ReflectionIconProvider()
    }
}

class ReflectionIconProvider : IconProvider {
    override fun load(spec: IconSpec): ImageVector? {
        val className = spec.toClassName()
        // 使用反射加载预生成的图标类
    }
}
```

### 5. LazyImageVector (懒加载包装器)

**位置**: `commonMain/kotlin/LazyImageVector.kt`

**用法**:
```kotlin
sealed class IconLoadState {
    object Loading : IconLoadState()
    data class Success(val icon: ImageVector) : IconLoadState()
    data class Error(val error: Throwable) : IconLoadState()
}

@Composable
fun rememberLazyIcon(spec: IconSpec): State<IconLoadState>

@Composable
fun rememberPreloadedIcons(specs: List<IconSpec>): State<Map<IconSpec, ImageVector>>
```

### 6. IconSpec (图标规格)

**位置**: `commonMain/kotlin/IconSpec.kt`

**设计**:
```kotlin
data class IconSpec(
    val name: String,
    val weight: SymbolWeight,
    val variant: SymbolVariant,
    val fill: Boolean
) {
    fun toClassName(): String {
        // 生成完全限定的图标类名
        // 例如: io.github.kingsword09.symbolcraft.icons.outlined.w400.HomeW400Outlined
    }
}

enum class SymbolWeight(val value: Int) {
    W100(100), W200(200), W300(300), W400(400),
    W500(500), W600(600), W700(700)
}

enum class SymbolVariant(val suffix: String) {
    OUTLINED("Outlined"),
    ROUNDED("Rounded"),
    SHARP("Sharp")
}
```

---

## 实现计划

### 阶段 1: 基础架构 (1周)

**任务**:
- [x] 创建模块结构
- [ ] 实现 `IconSpec` 数据类
- [ ] 实现 `IconCache` 基础功能
- [ ] 实现 `IconProvider` 接口
- [ ] 单元测试

**验收标准**:
- 缓存可以正常存取
- IconSpec 可以生成正确的类名
- 所有测试通过

### 阶段 2: 加载器实现 (1周)

**任务**:
- [ ] 实现 `IconLoader` 核心逻辑
- [ ] 实现 `ReflectionIconProvider`
- [ ] 集成缓存机制
- [ ] 错误处理和日志
- [ ] 性能测试

**验收标准**:
- 可以成功加载预生成的图标
- 缓存命中率 > 80%
- 加载时间 < 5ms（缓存命中）

### 阶段 3: MaterialSymbols API (1周)

**任务**:
- [ ] 实现 `MaterialSymbols` 对象
- [ ] 实现 `WeightGroup` 委托
- [ ] 实现便捷扩展函数
- [ ] API 文档
- [ ] 使用示例

**验收标准**:
- API 使用体验接近 material-icons-extended
- IDE 自动补全工作正常
- 文档完整

### 阶段 4: 懒加载支持 (3天)

**任务**:
- [ ] 实现 `LazyImageVector`
- [ ] 实现 `rememberLazyIcon`
- [ ] 实现 `rememberPreloadedIcons`
- [ ] Compose 集成测试

**验收标准**:
- 异步加载不阻塞 UI
- 加载状态正确反映
- 支持预加载

### 阶段 5: 多平台支持 (1周)

**任务**:
- [ ] Android 平台适配
- [ ] iOS 平台适配
- [ ] JVM Desktop 适配
- [ ] JS 平台适配（可选）
- [ ] 平台特定优化

**验收标准**:
- 所有平台编译通过
- 所有平台测试通过
- 性能符合预期

### 阶段 6: 文档和示例 (3天)

**任务**:
- [ ] 完善 API 文档
- [ ] 编写使用指南
- [ ] 创建示例代码
- [ ] 迁移指南（从 symbolcraft-plugin）

**验收标准**:
- 文档覆盖所有公开 API
- 示例代码可运行
- 新用户可以快速上手

---

## 开发工作流

### 本地开发测试

#### 1. 构建模块
```bash
./gradlew :symbolcraft-runtime:build
```

#### 2. 运行测试
```bash
./gradlew :symbolcraft-runtime:allTests
```

#### 3. 发布到本地 Maven
```bash
./gradlew :symbolcraft-runtime:publishToMavenLocal
```

#### 4. 在示例项目中测试
```bash
cd example
# 添加依赖
# implementation("io.github.kingsword09:symbolcraft-runtime:0.4.0-SNAPSHOT")
./gradlew :composeApp:run
```

---

## 测试策略

### 单元测试

**IconCacheTest.kt**:
```kotlin
class IconCacheTest {
    @Test
    fun testCacheHitRate()

    @Test
    fun testLRUEviction()

    @Test
    fun testThreadSafety()
}
```

**IconLoaderTest.kt**:
```kotlin
class IconLoaderTest {
    @Test
    fun testLoadExistingIcon()

    @Test
    fun testLoadNonExistentIcon()

    @Test
    fun testCacheIntegration()

    @Test
    fun testPreload()
}
```

**MaterialSymbolsTest.kt**:
```kotlin
class MaterialSymbolsTest {
    @Test
    fun testIconSpecClassName()

    @Test
    fun testWeightGroupAccess()

    @Test
    fun testDynamicGet()
}
```

### 集成测试

**性能测试**:
- 加载时间（首次 vs 缓存）
- 内存占用
- 缓存命中率

**多平台测试**:
- Android 设备测试
- iOS 模拟器测试
- Desktop 测试

---

## 性能优化建议

### 1. 缓存大小调整
```kotlin
// 自定义缓存大小
val customCache = IconCache(maxSize = 200) // 增加到 200 个
```

### 2. 预加载关键图标
```kotlin
@Composable
fun App() {
    // 应用启动时预加载常用图标
    LaunchedEffect(Unit) {
        IconLoader.preload(
            listOf(
                IconSpec("home", SymbolWeight.W400, SymbolVariant.OUTLINED, false),
                IconSpec("search", SymbolWeight.W400, SymbolVariant.OUTLINED, false),
                // ...
            )
        )
    }
}
```

### 3. 监控缓存性能
```kotlin
@Composable
fun CacheMonitor() {
    val stats = remember { IconLoader.getCacheStats() }
    Text("Cache: ${stats.size} icons, Hit rate: ${stats.hitRate * 100}%")
}
```

---

## 依赖关系

### 对外依赖
```kotlin
commonMain.dependencies {
    // Compose 核心依赖
    implementation(compose.runtime)
    implementation(compose.ui)

    // Kotlin 协程（用于异步加载）
    implementation(libs.kotlinx.coroutines.core)
}
```

### 被依赖
- `symbolcraft-material-symbols` - 使用 runtime 提供的 MaterialSymbols API
- 用户应用 - 直接依赖 runtime 或通过 material-symbols 间接依赖

---

## 发布清单

### 发布前检查
- [ ] 所有单元测试通过
- [ ] 所有平台编译通过
- [ ] API 文档完整
- [ ] 性能测试达标
- [ ] 示例代码可运行
- [ ] 版本号更新
- [ ] CHANGELOG 更新

### 发布命令
```bash
# 发布到 Maven Central
./gradlew :symbolcraft-runtime:publishToMavenCentral

# 或使用统一发布
./gradlew publishAll
```

---

## 常见问题

### Q: 为什么使用反射加载图标？
A: 反射允许我们在运行时动态加载预生成的图标类，无需手动导入所有图标。配合 Tree Shaking，可以在编译时移除未使用的图标。

### Q: LRU 缓存大小如何选择？
A: 默认 100 个图标约占用 ~2-5MB 内存。可根据应用特点调整：
- 图标较少的应用：50-100
- 图标较多的应用：100-200
- 内存受限设备：30-50

### Q: 如何处理动态图标名称？
A: 使用 `MaterialSymbols.get()` 方法动态获取：
```kotlin
val iconName = viewModel.currentIcon // 来自网络或数据库
Icon(MaterialSymbols.get(iconName), "Dynamic Icon")
```

---

## 贡献指南

### 开发环境设置
1. Clone 仓库
2. 打开 Android Studio / IntelliJ IDEA
3. 导入 SymbolCraft 项目
4. 同步 Gradle
5. 运行测试确认环境正常

### 代码规范
- 遵循 Kotlin 官方编码规范
- 所有公开 API 必须有 KDoc 注释
- 新功能必须有对应的单元测试
- 提交前运行 `./gradlew :symbolcraft-runtime:check`

### 提交规范
```
feat(runtime): add lazy loading support
fix(runtime): resolve cache thread safety issue
docs(runtime): update API documentation
test(runtime): add IconLoader integration tests
```

---

## 资源链接

### 内部文档
- [根项目 AGENTS.md](../AGENTS.md)
- [Plugin AGENTS.md](../symbolcraft-plugin/AGENTS.md)
- [README.md](README.md)

### 外部资源
- [Kotlin Multiplatform Guide](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Material Design Icons](https://fonts.google.com/icons)

---

## 联系方式

- **维护者**: [@kingsword09](https://github.com/kingsword09)
- **Email**: kingsword09@gmail.com
- **问题反馈**: [GitHub Issues](https://github.com/kingsword09/SymbolCraft/issues)

---

**最后更新**: 2025-10-19
**文档版本**: 1.0.0 (Runtime Module - Planning Stage)
**状态**: 🚧 未实现 - 规划阶段
