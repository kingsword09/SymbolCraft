# SymbolCraft

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.kingsword09.symbolcraft?label=Gradle%20Plugin)](https://plugins.gradle.org/plugin/io.github.kingsword09.symbolcraft)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kingsword09/symbolcraft?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.kingsword09/symbolcraft)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg?logo=kotlin)](http://kotlinlang.org)

> **📌 注意**: 你正在查看 **v0.4.0 开发分支**，包含 Monorepo 架构规划。
>
> - **稳定版本 (v0.3.2)**: 切换到 [`main`](https://github.com/kingsword09/SymbolCraft/tree/main) 分支
> - **开发版本 (v0.4.0)**: 此分支包含计划中的功能和架构变更，尚未实现
>
> 生产环境请使用 main 分支的稳定版本。

**为 Compose Multiplatform 从多个图标库按需生成图标**

[English](README.md) | [插件文档](symbolcraft-plugin/README_ZH.md)

---

## 🏗️ 项目架构 (Monorepo)

SymbolCraft 采用 **Monorepo** 架构组织，包含多个模块：

```
SymbolCraft/
├── symbolcraft-plugin/              # Gradle 插件 - 按需生成图标
├── symbolcraft-runtime/             # 运行时库 - 图标加载和缓存 (🚧 即将推出)
├── symbolcraft-material-symbols/    # 预生成的 Material Symbols 图标库 (🚧 即将推出)
└── example/                         # Compose Multiplatform 示例应用
```

### 模块概览

| 模块 | 类型 | 状态 | 描述 |
|------|------|------|------|
| **symbolcraft-plugin** | Gradle 插件 | ✅ 已发布 | 从多个图标库按需生成图标 |
| **symbolcraft-runtime** | KMP 库 | 🚧 计划中 | 提供懒加载和缓存的运行时支持 |
| **symbolcraft-material-symbols** | KMP 库 | 🚧 计划中 | 预生成的 Material Symbols 图标 |

---

## 📦 symbolcraft-plugin

**当前版本**: `0.4.0`

一个 Gradle 插件，可从多个图标库按需生成 Compose ImageVector 图标。

### ✨ 核心特性

- 🚀 **多图标库支持** - Material Symbols、Bootstrap Icons、Heroicons、自定义 URL
- 💾 **智能缓存** - 7 天有效期的 SVG 缓存，支持可配置路径
- ⚡ **并行下载** - 使用 Kotlin 协程并行下载，支持重试机制
- 🎯 **确定性构建** - Git 友好的确定性代码生成
- 🏷️ **灵活命名** - 支持 PascalCase、camelCase、snake_case 等多种命名规则
- 👀 **Compose 预览** - 自动生成 @Preview 函数

### 📖 文档

详细的插件文档、使用示例和 API 参考，请查看：
- **[插件 README](symbolcraft-plugin/README_ZH.md)** - 完整中文文档
- **[Plugin README](symbolcraft-plugin/README.md)** - English documentation

### 快速开始

```kotlin
// build.gradle.kts
plugins {
    id("io.github.kingsword09.symbolcraft") version "0.4.0"
}

symbolCraft {
    packageName.set("com.example.icons")
    
    materialSymbol {
        names = listOf("home", "search", "settings")
        weights = listOf(400, 500)
        variants = listOf("outlined", "rounded")
        fills = listOf(0, 1)
    }
}
```

---

## 🧩 symbolcraft-runtime (即将推出)

**状态**: 🚧 开发中

一个 Kotlin Multiplatform 库，提供图标加载和缓存的运行时支持。

### 计划功能

- **懒加载图标** - 按需加载图标以最小化内存占用
- **LRU 缓存** - 高效的内存图标缓存
- **多平台支持** - 支持 Android、iOS、JVM、JS
- **MaterialSymbols API** - 类似 `androidx.compose.material.icons` 的流畅 API

### 未来用法

```kotlin
import io.github.kingsword09.symbolcraft.runtime.MaterialSymbols

@Composable
fun MyScreen() {
    Icon(
        imageVector = MaterialSymbols.Outlined.W400.Home,
        contentDescription = "首页"
    )
}
```

---

## 📚 symbolcraft-material-symbols (即将推出)

**状态**: 🚧 开发中

一个包含预生成 Material Symbols 图标的 Kotlin Multiplatform 库。

### 计划功能

- **完整图标集** - 所有 Material Symbols 图标
- **多种变体** - Outlined、Rounded、Sharp 样式
- **可变字重** - 100-700 字重支持
- **填充状态** - 填充和未填充版本
- **Tree-shakable** - 仅将使用的图标包含在最终构建中

---

## 🔮 v0.4.0 即将推出的功能

SymbolCraft v0.4.0 将引入多个令人兴奋的新功能：

### 🌳 Tree Shaking (计划中)

通过静态代码分析，只生成代码中实际使用的图标。

```kotlin
symbolCraft {
    treeShaking {
        enabled.set(true)
        scanDirectories.addAll("src/commonMain/kotlin", "src/androidMain/kotlin")
        strategy.set(ScanStrategy.USAGE_BASED)
        alwaysInclude.addAll("home", "search", "settings")
    }
}
```

**优势**:
- 缩短代码生成时间
- 减少仓库体积
- 只编译需要的内容
- 自动依赖追踪

### 📊 性能监控 (计划中)

详细的图标生成统计信息：

```
📊 Generation Report:
   ⏱️ Total time: 3.2s
   ⬇️ Download: 1.5s (avg 245KB/s)
   🔄 Conversion: 1.7s
   💾 Cache hit rate: 66.7% (8/12)
   📦 Generated: 12 icons, 245KB total
```

### 🚨 增强的错误处理 (计划中)

更好的错误分类和恢复机制：
- 网络错误的重试策略
- 缓存损坏检测和自动修复
- 转换失败的详细诊断
- 配置验证的实用建议

---

## 🚀 快速开始

### 环境要求

- **Gradle**: 8.7+
- **Kotlin**: 2.0.0+
- **Compose Multiplatform**: 1.6.0+

### 安装

将插件添加到你的项目：

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

// build.gradle.kts
plugins {
    id("io.github.kingsword09.symbolcraft") version "0.4.0"
}
```

详细的设置和配置说明，请参阅 [插件文档](symbolcraft-plugin/README_ZH.md)。

---

## 🛠️ 开发

### 构建所有模块

```bash
./gradlew clean build -x test
```

### 构建单个模块

```bash
./gradlew :symbolcraft-plugin:build
./gradlew :symbolcraft-runtime:build
./gradlew :symbolcraft-material-symbols:build
```

### 发布到本地 Maven

```bash
./gradlew publishAllToMavenLocal
```

### 项目结构

```
SymbolCraft/
├── build.gradle.kts                 # 根构建配置
├── settings.gradle.kts              # 子模块配置
├── gradle/libs.versions.toml        # 版本目录
│
├── symbolcraft-plugin/              # Gradle 插件模块
│   ├── README.md                    # 插件文档（英文）
│   ├── README_ZH.md                 # 插件文档（中文）
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│
├── symbolcraft-runtime/             # 运行时库模块
│   ├── README.md
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/
│       ├── androidMain/
│       ├── jvmMain/
│       └── iosMain/
│
├── symbolcraft-material-symbols/    # 预生成图标模块
│   ├── README.md
│   ├── build.gradle.kts
│   └── src/commonMain/
│
└── example/                         # 示例应用
    └── composeApp/
```

---

## 📄 开源协议

```
Copyright 2025 kingsword09

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 🔗 链接

- **文档**: [插件 README](symbolcraft-plugin/README_ZH.md)
- **Gradle Plugin Portal**: https://plugins.gradle.org/plugin/io.github.kingsword09.symbolcraft
- **Maven Central**: https://central.sonatype.com/artifact/io.github.kingsword09/symbolcraft
- **GitHub**: https://github.com/kingsword09/SymbolCraft
- **问题反馈**: https://github.com/kingsword09/SymbolCraft/issues

---

## 🤝 贡献

欢迎贡献！请随时提交 Pull Request。

---

**用 ❤️ 制作 by [kingsword09](https://github.com/kingsword09)**
