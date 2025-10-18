  2. MaterialSymbols.kt (主访问入口)

  package io.github.kingsword09.symbolcraft.runtime

  import androidx.compose.runtime.Composable
  import androidx.compose.runtime.remember
  import androidx.compose.ui.graphics.vector.ImageVector

  /**
   * Material Symbols 图标访问入口
   * 
   * 提供类似 androidx.compose.material.icons.Icons 的 API 体验
   * 
   * 用法:
   * ```kotlin
   * Icon(
   *     imageVector = MaterialSymbols.Outlined.W400.Home,
   *     contentDescription = "Home"
   * )
   * ```
   */
  object MaterialSymbols {

      /**
       * Outlined 样式图标
       */
      object Outlined {
          object W100 : WeightGroup(SymbolWeight.W100, SymbolVariant.OUTLINED)
          object W200 : WeightGroup(SymbolWeight.W200, SymbolVariant.OUTLINED)
          object W300 : WeightGroup(SymbolWeight.W300, SymbolVariant.OUTLINED)
          object W400 : WeightGroup(SymbolWeight.W400, SymbolVariant.OUTLINED)
          object W500 : WeightGroup(SymbolWeight.W500, SymbolVariant.OUTLINED)
          object W600 : WeightGroup(SymbolWeight.W600, SymbolVariant.OUTLINED)
          object W700 : WeightGroup(SymbolWeight.W700, SymbolVariant.OUTLINED)
      }

      /**
       * Rounded 样式图标
       */
      object Rounded {
          object W100 : WeightGroup(SymbolWeight.W100, SymbolVariant.ROUNDED)
          object W200 : WeightGroup(SymbolWeight.W200, SymbolVariant.ROUNDED)
          object W300 : WeightGroup(SymbolWeight.W300, SymbolVariant.ROUNDED)
          object W400 : WeightGroup(SymbolWeight.W400, SymbolVariant.ROUNDED)
          object W500 : WeightGroup(SymbolWeight.W500, SymbolVariant.ROUNDED)
          object W600 : WeightGroup(SymbolWeight.W600, SymbolVariant.ROUNDED)
          object W700 : WeightGroup(SymbolWeight.W700, SymbolVariant.ROUNDED)
      }

      /**
       * Sharp 样式图标
       */
      object Sharp {
          object W100 : WeightGroup(SymbolWeight.W100, SymbolVariant.SHARP)
          object W200 : WeightGroup(SymbolWeight.W200, SymbolVariant.SHARP)
          object W300 : WeightGroup(SymbolWeight.W300, SymbolVariant.SHARP)
          object W400 : WeightGroup(SymbolWeight.W400, SymbolVariant.SHARP)
          object W500 : WeightGroup(SymbolWeight.W500, SymbolVariant.SHARP)
          object W600 : WeightGroup(SymbolWeight.W600, SymbolVariant.SHARP)
          object W700 : WeightGroup(SymbolWeight.W700, SymbolVariant.SHARP)
      }

      /**
       * 直接通过名称获取图标
       * 
       * @param name 图标名称 (如 "home")
       * @param weight 权重 (默认 400)
       * @param variant 样式 (默认 OUTLINED)
       * @param fill 是否填充 (默认 false)
       */
      @Composable
      fun get(
          name: String,
          weight: SymbolWeight = SymbolWeight.W400,
          variant: SymbolVariant = SymbolVariant.OUTLINED,
          fill: Boolean = false
      ): ImageVector {
          return remember(name, weight, variant, fill) {
              IconLoader.load(
                  IconSpec(
                      name = name,
                      weight = weight,
                      variant = variant,
                      fill = fill
                  )
              )
          }
      }
  }

  /**
   * 权重组
   * 用于实现 MaterialSymbols.Outlined.W400.Home 的链式访问
   */
  open class WeightGroup(
      private val weight: SymbolWeight,
      private val variant: SymbolVariant
  ) {

      /**
       * 动态属性委托
       * 实现 MaterialSymbols.Outlined.W400.Home 的访问方式
       */
      operator fun getValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>): ImageVector {
          val iconName = property.name.lowercase() // Home -> home
          return IconLoader.load(
              IconSpec(
                  name = iconName,
                  weight = weight,
                  variant = variant,
                  fill = false
              )
          )
      }

      /**
       * 直接通过索引访问
       * MaterialSymbols.Outlined.W400["home"]
       */
      operator fun get(name: String, fill: Boolean = false): ImageVector {
          return IconLoader.load(
              IconSpec(
                  name = name.lowercase(),
                  weight = weight,
                  variant = variant,
                  fill = fill
              )
          )
      }
  }

  /**
   * 图标规格
   */
  data class IconSpec(
      val name: String,
      val weight: SymbolWeight,
      val variant: SymbolVariant,
      val fill: Boolean
  ) {
      /**
       * 生成完全限定的图标类名
       * 例如: io.github.kingsword09.symbolcraft.icons.outlined.w400.HomeW400Outlined
       */
      fun toClassName(): String {
          val variantName = variant.name.lowercase()
          val weightValue = weight.value
          val fillSuffix = if (fill) "Fill" else ""

          val capitalizedName = name.split("_")
              .joinToString("") { it.replaceFirstChar { c -> c.uppercase() } }

          return "io.github.kingsword09.symbolcraft.icons.$variantName.w$weightValue.${capitalizedName}W${weightValue}${variant.suffix}$fillSuffix"
      }
  }

  /**
   * 权重枚举
   */
  enum class SymbolWeight(val value: Int) {
      W100(100),
      W200(200),
      W300(300),
      W400(400),
      W500(500),
      W600(600),
      W700(700)
  }

  /**
   * 样式枚举
   */
  enum class SymbolVariant(val suffix: String) {
      OUTLINED("Outlined"),
      ROUNDED("Rounded"),
      SHARP("Sharp")
  }

  3. IconLoader.kt (图标加载器)

  package io.github.kingsword09.symbolcraft.runtime

  import androidx.compose.ui.graphics.vector.ImageVector
  import kotlinx.coroutines.sync.Mutex
  import kotlinx.coroutines.sync.withLock

  /**
   * 图标加载器
   * 
   * 负责:
   * 1. 从预生成的类中加载图标
   * 2. 管理内存缓存
   * 3. 处理加载失败
   */
  object IconLoader {

      private val cache = IconCache()
      private val provider = IconProvider.Default
      private val loadMutex = Mutex()

      /**
       * 加载图标
       * 
       * @param spec 图标规格
       * @return ImageVector 实例
       * @throws IconNotFoundException 图标不存在时抛出
       */
      fun load(spec: IconSpec): ImageVector {
          // 1. 尝试从缓存读取
          cache.get(spec)?.let { return it }

          // 2. 从 provider 加载
          val icon = provider.load(spec)
              ?: throw IconNotFoundException(spec)

          // 3. 写入缓存
          cache.put(spec, icon)

          return icon
      }

      /**
       * 预加载图标 (可选优化)
       */
      suspend fun preload(specs: List<IconSpec>) {
          loadMutex.withLock {
              specs.forEach { spec ->
                  if (cache.get(spec) == null) {
                      val icon = provider.load(spec)
                      if (icon != null) {
                          cache.put(spec, icon)
                      }
                  }
              }
          }
      }

      /**
       * 清空缓存 (低内存时调用)
       */
      fun clearCache() {
          cache.clear()
      }

      /**
       * 获取缓存统计
       */
      fun getCacheStats(): CacheStats {
          return cache.getStats()
      }
  }

  /**
   * 图标提供者接口
   */
  interface IconProvider {

      /**
       * 加载图标
       * @return 如果图标存在返回 ImageVector，否则返回 null
       */
      fun load(spec: IconSpec): ImageVector?

      companion object {
          /**
           * 默认提供者
           * 通过反射/动态加载预生成的图标类
           */
          val Default: IconProvider = ReflectionIconProvider()
      }
  }

  /**
   * 反射图标提供者
   * 使用反射从预生成的类中加载图标
   */
  class ReflectionIconProvider : IconProvider {

      override fun load(spec: IconSpec): ImageVector? {
          val className = spec.toClassName()

          return try {
              // 使用 Kotlin 反射加载类
              val clazz = Class.forName(className).kotlin

              // 查找 ImageVector 属性
              val property = clazz.members
                  .filterIsInstance<kotlin.reflect.KProperty<*>>()
                  .firstOrNull { it.returnType.classifier == ImageVector::class }

              // 获取属性值 (通过伴生对象或对象实例)
              val instance = clazz.objectInstance ?: clazz.companionObjectInstance
              property?.call(instance) as? ImageVector

          } catch (e: ClassNotFoundException) {
              null // 图标不存在
          } catch (e: Exception) {
              // 记录错误但不崩溃
              println("Failed to load icon: $className - ${e.message}")
              null
          }
      }
  }

  /**
   * 图标未找到异常
   */
  class IconNotFoundException(spec: IconSpec) : Exception(
      "Icon not found: ${spec.name} (weight=${spec.weight.value}, variant=${spec.variant}, fill=${spec.fill})"
  )

  4. IconCache.kt (缓存管理)

  package io.github.kingsword09.symbolcraft.runtime

  import androidx.compose.ui.graphics.vector.ImageVector
  import kotlin.concurrent.Volatile

  /**
   * 图标缓存
   * 
   * 使用 LRU 策略管理内存缓存
   */
  class IconCache(
      private val maxSize: Int = 100 // 默认缓存 100 个图标
  ) {

      // 使用 LinkedHashMap 实现 LRU
      private val cache = object : LinkedHashMap<IconSpec, ImageVector>(
          maxSize,
          0.75f,
          true // 访问顺序
      ) {
          override fun removeEldestEntry(eldest: MutableMap.MutableEntry<IconSpec, ImageVector>?): Boolean {
              return size > maxSize
          }
      }

      @Volatile
      private var hits = 0L

      @Volatile
      private var misses = 0L

      /**
       * 获取图标
       */
      @Synchronized
      fun get(spec: IconSpec): ImageVector? {
          val icon = cache[spec]
          if (icon != null) {
              hits++
          } else {
              misses++
          }
          return icon
      }

      /**
       * 存入图标
       */
      @Synchronized
      fun put(spec: IconSpec, icon: ImageVector) {
          cache[spec] = icon
      }

      /**
       * 清空缓存
       */
      @Synchronized
      fun clear() {
          cache.clear()
          hits = 0
          misses = 0
      }

      /**
       * 获取统计信息
       */
      @Synchronized
      fun getStats(): CacheStats {
          return CacheStats(
              size = cache.size,
              hits = hits,
              misses = misses,
              hitRate = if (hits + misses > 0) hits.toDouble() / (hits + misses) else 0.0
          )
      }
  }

  /**
   * 缓存统计
   */
  data class CacheStats(
      val size: Int,
      val hits: Long,
      val misses: Long,
      val hitRate: Double
  )

  5. LazyImageVector.kt (懒加载包装器)

  package io.github.kingsword09.symbolcraft.runtime

  import androidx.compose.runtime.Composable
  import androidx.compose.runtime.State
  import androidx.compose.runtime.produceState
  import androidx.compose.ui.graphics.vector.ImageVector
  import kotlinx.coroutines.Dispatchers
  import kotlinx.coroutines.withContext

  /**
   * 懒加载 ImageVector 包装器
   * 
   * 用于异步加载图标，避免阻塞主线程
   * 
   * 用法:
   * ```kotlin
   * val homeIcon = rememberLazyIcon(
   *     spec = IconSpec("home", SymbolWeight.W400, SymbolVariant.OUTLINED, false)
   * )
   * 
   * when (homeIcon.value) {
   *     is IconLoadState.Loading -> CircularProgressIndicator()
   *     is IconLoadState.Success -> Icon(homeIcon.value.icon, "Home")
   *     is IconLoadState.Error -> Text("Failed")
   * }
   * ```
   */
  sealed class IconLoadState {
      object Loading : IconLoadState()
      data class Success(val icon: ImageVector) : IconLoadState()
      data class Error(val error: Throwable) : IconLoadState()
  }

  /**
   * 记住懒加载图标
   */
  @Composable
  fun rememberLazyIcon(spec: IconSpec): State<IconLoadState> {
      return produceState<IconLoadState>(initialValue = IconLoadState.Loading, spec) {
          value = try {
              val icon = withContext(Dispatchers.Default) {
                  IconLoader.load(spec)
              }
              IconLoadState.Success(icon)
          } catch (e: Exception) {
              IconLoadState.Error(e)
          }
      }
  }

  /**
   * 批量预加载图标
   */
  @Composable
  fun rememberPreloadedIcons(specs: List<IconSpec>): State<Map<IconSpec, ImageVector>> {
      return produceState(initialValue = emptyMap(), specs) {
          value = withContext(Dispatchers.Default) {
              specs.mapNotNull { spec ->
                  try {
                      spec to IconLoader.load(spec)
                  } catch (e: Exception) {
                      null
                  }
              }.toMap()
          }
      }
  }

  6. 扩展函数 (便利 API)

  package io.github.kingsword09.symbolcraft.runtime

  import androidx.compose.runtime.Composable
  import androidx.compose.ui.graphics.vector.ImageVector

  /**
   * MaterialSymbols 扩展函数
   */

  /**
   * 快速访问常用图标
   */
  val MaterialSymbols.Outlined.W400.Home: ImageVector
      get() = this["home"]

  val MaterialSymbols.Outlined.W400.Search: ImageVector
      get() = this["search"]

  val MaterialSymbols.Outlined.W400.Settings: ImageVector
      get() = this["settings"]

  val MaterialSymbols.Outlined.W400.Person: ImageVector
      get() = this["person"]

  val MaterialSymbols.Outlined.W400.Menu: ImageVector
      get() = this["menu"]

  val MaterialSymbols.Outlined.W400.Close: ImageVector
      get() = this["close"]

  /**
   * 便捷获取填充版本
   */
  fun WeightGroup.filled(name: String): ImageVector {
      return this[name, fill = true]
  }

  /**
   * Composable 扩展: 记住图标
   */
  @Composable
  fun MaterialSymbols.rememberIcon(
      name: String,
      weight: SymbolWeight = SymbolWeight.W400,
      variant: SymbolVariant = SymbolVariant.OUTLINED,
      fill: Boolean = false
  ): ImageVector {
      return this.get(name, weight, variant, fill)
  }

  7. 测试

  package io.github.kingsword09.symbolcraft.runtime

  import kotlin.test.Test
  import kotlin.test.assertEquals
  import kotlin.test.assertNotNull

  class MaterialSymbolsTest {

      @Test
      fun testIconSpecClassName() {
          val spec = IconSpec(
              name = "home",
              weight = SymbolWeight.W400,
              variant = SymbolVariant.OUTLINED,
              fill = false
          )

          val expected = "io.github.kingsword09.symbolcraft.icons.outlined.w400.HomeW400Outlined"
          assertEquals(expected, spec.toClassName())
      }

      @Test
      fun testIconSpecWithFill() {
          val spec = IconSpec(
              name = "search",
              weight = SymbolWeight.W500,
              variant = SymbolVariant.ROUNDED,
              fill = true
          )

          val expected = "io.github.kingsword09.symbolcraft.icons.rounded.w500.SearchW500RoundedFill"
          assertEquals(expected, spec.toClassName())
      }

      @Test
      fun testCacheHitRate() {
          val cache = IconCache(maxSize = 10)
          val spec = IconSpec("home", SymbolWeight.W400, SymbolVariant.OUTLINED, false)

          // 模拟图标对象
          val mockIcon = createMockImageVector()

          // 第一次访问 - miss
          cache.get(spec)
          var stats = cache.getStats()
          assertEquals(0, stats.hits)
          assertEquals(1, stats.misses)

          // 存入缓存
          cache.put(spec, mockIcon)

          // 第二次访问 - hit
          assertNotNull(cache.get(spec))
          stats = cache.getStats()
          assertEquals(1, stats.hits)
          assertEquals(1, stats.misses)
          assertEquals(0.5, stats.hitRate, 0.01)
      }
  }

  // 测试辅助函数
  private fun createMockImageVector(): ImageVector {
      // 创建一个简单的 mock ImageVector
      // 实际实现需要根据测试框架调整
      return ImageVector.Builder(
          name = "Test",
          defaultWidth = 24.dp,
          defaultHeight = 24.dp,
          viewportWidth = 24f,
          viewportHeight = 24f
      ).build()
  }

  🚀 使用示例

  基础使用

  import io.github.kingsword09.symbolcraft.runtime.MaterialSymbols

  @Composable
  fun MyApp() {
      // 方式1: 直接属性访问
      Icon(MaterialSymbols.Outlined.W400.Home, "Home")

      // 方式2: 索引访问
      Icon(MaterialSymbols.Outlined.W400["search"], "Search")

      // 方式3: 动态获取
      val iconName = "settings"
      Icon(
          MaterialSymbols.get(
              name = iconName,
              weight = SymbolWeight.W500,
              variant = SymbolVariant.ROUNDED
          ),
          "Settings"
      )

      // 方式4: 填充版本
      Icon(MaterialSymbols.Rounded.W500.filled("favorite"), "Favorite")
  }

  高级使用

  @Composable
  fun AdvancedUsage() {
      // 懒加载
      val homeIcon = rememberLazyIcon(
          IconSpec("home", SymbolWeight.W400, SymbolVariant.OUTLINED, false)
      )

      when (val state = homeIcon.value) {
          is IconLoadState.Loading -> CircularProgressIndicator()
          is IconLoadState.Success -> Icon(state.icon, "Home")
          is IconLoadState.Error -> Text("Failed to load")
      }

      // 批量预加载
      val icons = rememberPreloadedIcons(
          listOf(
              IconSpec("home", SymbolWeight.W400, SymbolVariant.OUTLINED, false),
              IconSpec("search", SymbolWeight.W400, SymbolVariant.OUTLINED, false),
              IconSpec("settings", SymbolWeight.W400, SymbolVariant.OUTLINED, false)
          )
      )

      // 使用预加载的图标
      icons.value.forEach { (spec, icon) ->
          Icon(icon, spec.name)
      }
  }

  📊 性能优化建议

  1. 缓存大小调整

  // 自定义缓存大小
  val customCache = IconCache(maxSize = 200) // 增加到 200 个

  2. 预加载关键图标

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

  3. 监控缓存性能

  @Composable
  fun CacheMonitor() {
      val stats = remember { IconLoader.getCacheStats() }

      Text("Cache: ${stats.size} icons, Hit rate: ${stats.hitRate * 100}%")
  }

  📝 发布检查清单

  - 所有单元测试通过
  - 支持所有目标平台 (Android, iOS, JVM, JS)
  - API 文档完整
  - 性能测试通过
  - 发布到 Maven Central