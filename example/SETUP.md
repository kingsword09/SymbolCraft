# Example Project Setup Guide

## 当前配置

### 版本

| 组件 | 版本 | 说明 |
|------|------|------|
| **Kotlin** | 2.0.0 | 与根项目一致 |
| **Compose Multiplatform** | 1.8.0 | 升级以获得更多功能 |
| **Android Gradle Plugin** | 8.5.2 | 匹配根项目 |
| **Gradle** | 8.14.3 | Wrapper 版本 |
| **SymbolCraft** | 0.4.0 | 使用 mavenLocal() |

### 项目结构

```
example/                            # 独立项目（不是根项目子模块）
├── composeApp/                    # Compose Multiplatform 应用
├── iosApp/                        # iOS 应用包装器
├── gradle/                        # Gradle 配置
│   └── libs.versions.toml         # 版本目录（独立配置）
├── settings.gradle.kts            # 使用 mavenLocal() 引用插件
└── gradle.properties              # 项目配置
```

---

## 🚀 快速开始

### 1. 发布插件到本地 Maven（必须）

在根项目执行：

```bash
cd /path/to/SymbolCraft
./gradlew :symbolcraft-plugin:publishToMavenLocal
./gradlew :symbolcraft-runtime:publishToMavenLocal          # 可选
./gradlew :symbolcraft-material-symbols:publishToMavenLocal # 可选
```

### 2. 生成图标

在 example 目录：

```bash
cd example
./gradlew :composeApp:generateSymbolCraftIcons
```

### 3. 运行应用

#### Desktop (JVM) - ✅ 已测试

```bash
./gradlew :composeApp:jvmRun
```

#### Android - ⚠️ 需要更高 compileSdk

当前配置 compileSdk=34，部分依赖可能需要 35+。

修复方案：
1. 升级到 AGP 8.7+ 并设置 `compileSdk = 35`
2. 或者降低某些依赖版本

```bash
# 构建 Android（可能失败）
./gradlew :composeApp:assembleDebug
```

#### iOS - 需要 macOS + Xcode

```bash
# 打开 Xcode 项目
open iosApp/iosApp.xcodeproj
```

---

## ⚙️ 配置说明

### gradle/libs.versions.toml

Example 项目有**独立的版本配置**，不继承根项目：

```toml
[versions]
kotlin = "2.0.0"               # 与根项目一致
composeMultiplatform = "1.8.0" # 升级版本
agp = "8.5.2"                  # 与根项目一致
symbolcraft = "0.4.0"          # 使用本地发布版本
```

### settings.gradle.kts

使用 `mavenLocal()` 而不是 `includeBuild`：

```kotlin
pluginManagement {
    repositories {
        mavenLocal()  // 本地插件测试
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()  // 本地库测试
        google()
        mavenCentral()
    }
}
```

**原因**: 避免与根项目的插件版本冲突。

### gradle.properties

```properties
kotlin.mpp.androidGradlePluginCompatibility.nowarn=true
org.gradle.configuration-cache=true
org.gradle.caching=true
```

---

## 🎨 SymbolCraft 配置

在 `composeApp/build.gradle.kts` 中：

```kotlin
symbolCraft {
    outputDirectory.set("src/commonMain/kotlin/generated/symbols")
    packageName.set("io.github.kingsword09.example")
    generatePreview.set(true)
    
    naming {
        pascalCase()
    }
    
    // Material Symbols
    materialSymbol("search") {
        standardWeights()  // 400, 500, 700
    }
    
    materialSymbol("home") {
        weights(400, 500, variant = SymbolVariant.ROUNDED)
        bothFills(weight = 400)
    }
    
    // 外部图标
    externalIcons(*listOf("abacus", "ab-testing").toTypedArray(), libraryName = "mdi") {
        urlTemplate = "https://esm.sh/@mdi/svg@latest/svg/{name}.svg"
    }
}
```

---

## 🔧 已知问题

### 1. Compose Hot Reload 不兼容

**问题**: Hot Reload 1.0.0-beta07 与 Kotlin 2.0.0 Compose Compiler 不兼容

**错误信息**:
```
Unsupported plugin option: androidx.compose.compiler.plugins.kotlin:generateFunctionKeyMetaAnnotations=true
```

**临时方案**: 已在 `build.gradle.kts` 中禁用
```kotlin
// alias(libs.plugins.composeHotReload)
```

**未来解决**:
- 等待 Hot Reload 1.0.0 stable 版本
- 或升级到 Kotlin 2.1.0+

### 2. Android compileSdk 要求

某些依赖（如 androidx.activity 1.9.2）可能期望更高的 compileSdk。

**选项**:
- 保持 compileSdk = 34（当前配置）
- 或升级到 35+（需要 AGP 8.7+）

### 3. 独立项目管理

Example 不在根项目的 `settings.gradle.kts` 中，需要：
1. 独立管理依赖版本
2. 手动发布插件到 mavenLocal
3. 单独运行构建命令

---

## 📝 开发工作流

### 修改插件后测试

1. 在根项目发布更新：
```bash
cd /path/to/SymbolCraft
./gradlew :symbolcraft-plugin:publishToMavenLocal
```

2. 在 example 清理并重新生成：
```bash
cd example
./gradlew :composeApp:cleanSymbolCraftIcons
./gradlew :composeApp:generateSymbolCraftIcons
./gradlew :composeApp:jvmRun
```

### 添加新图标

1. 编辑 `composeApp/build.gradle.kts` 的 `symbolCraft {}` 块
2. 运行 `./gradlew :composeApp:generateSymbolCraftIcons`
3. 生成的图标位于：`composeApp/src/commonMain/kotlin/generated/symbols/`

### 清理

```bash
# 清理生成的图标
./gradlew :composeApp:cleanSymbolCraftIcons

# 清理 SVG 缓存
./gradlew :composeApp:cleanSymbolCraftCache

# 完全清理
./gradlew clean
```

---

## ✅ 测试状态

| 平台 | 构建 | 运行 | 说明 |
|------|------|------|------|
| **Desktop (JVM)** | ✅ | ✅ | 完全工作 |
| **Android** | ⚠️ | - | compileSdk 问题 |
| **iOS** | 🔄 | - | 需要 macOS + Xcode |
| **图标生成** | ✅ | ✅ | 26 个图标生成成功 |

---

## 🔗 相关文档

- [SymbolCraft 主文档](../README.md)
- [Example README](./README.md)
- [插件开发指南](../AGENTS.md)
- [CI/CD Workflows](../.github/workflows/README.md)

---

## 💡 提示

1. **首次使用**必须先发布插件到 mavenLocal
2. Example 项目是**独立项目**，有自己的版本配置
3. 推荐使用 **JVM target** 进行快速测试
4. Hot Reload 暂时不可用，等待兼容版本

---

**状态**: 🟢 Desktop 可用，Android 需要调整
**最后更新**: 2025-01-19
