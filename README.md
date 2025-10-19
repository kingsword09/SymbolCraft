# SymbolCraft

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.kingsword09.symbolcraft?label=Gradle%20Plugin)](https://plugins.gradle.org/plugin/io.github.kingsword09.symbolcraft)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kingsword09/symbolcraft?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.kingsword09/symbolcraft)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg?logo=kotlin)](http://kotlinlang.org)

> **📌 Note**: You are viewing the **v0.4.0 development branch** with Monorepo architecture planning.
>
> - **Stable Version (v0.3.2)**: Switch to the [`main`](https://github.com/kingsword09/SymbolCraft/tree/main) branch
> - **Development Version (v0.4.0)**: This branch contains planned features and architectural changes that are not yet implemented
>
> For production use, please use the stable version from the main branch.

**Generate icons on-demand from multiple libraries for Compose Multiplatform**

[中文文档](README_ZH.md) | [Plugin Documentation](symbolcraft-plugin/README.md)

---

## 🏗️ Project Architecture (Monorepo)

SymbolCraft is organized as a **Monorepo** with multiple modules:

```
SymbolCraft/
├── symbolcraft-plugin/              # Gradle Plugin for on-demand icon generation
├── symbolcraft-runtime/             # Runtime library for icon loading and caching (🚧 Coming Soon)
├── symbolcraft-material-symbols/    # Pre-generated Material Symbols library (🚧 Coming Soon)
└── example/                         # Example Compose Multiplatform application
```

### Module Overview

| Module | Type | Status | Description |
|--------|------|--------|-------------|
| **symbolcraft-plugin** | Gradle Plugin | ✅ Released | Generate icons on-demand from multiple libraries |
| **symbolcraft-runtime** | KMP Library | 🚧 Planned | Runtime support for lazy loading and caching |
| **symbolcraft-material-symbols** | KMP Library | 🚧 Planned | Pre-generated Material Symbols icons |

---

## 📦 symbolcraft-plugin

**Current Version**: `0.4.0`

A Gradle plugin that generates Compose ImageVector icons on-demand from multiple icon libraries.

### ✨ Features

- 🚀 **Multiple Icon Libraries** - Material Symbols, Bootstrap Icons, Heroicons, custom URLs
- 💾 **Smart Caching** - 7-day SVG cache with configurable paths
- ⚡ **Parallel Downloads** - Kotlin coroutines with retry mechanism
- 🎯 **Deterministic Builds** - Git-friendly deterministic code generation
- 🏷️ **Flexible Naming** - PascalCase, camelCase, snake_case, and more
- 👀 **Compose Preview** - Auto-generate @Preview functions

### 📖 Documentation

For detailed plugin documentation, usage examples, and API reference, see:
- **[Plugin README](symbolcraft-plugin/README.md)** - Full documentation
- **[Plugin README (中文)](symbolcraft-plugin/README_ZH.md)** - 中文文档

### Quick Start

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

## 🧩 symbolcraft-runtime (Coming Soon)

**Status**: 🚧 Under Development

A Kotlin Multiplatform library providing runtime support for icon loading and caching.

### Planned Features

- **Lazy Icon Loading** - Load icons on-demand to minimize memory
- **LRU Caching** - Memory-efficient icon caching
- **Multi-platform** - Android, iOS, JVM, JS support
- **MaterialSymbols API** - Fluent API similar to `androidx.compose.material.icons`

### Future Usage

```kotlin
import io.github.kingsword09.symbolcraft.runtime.MaterialSymbols

@Composable
fun MyScreen() {
    Icon(
        imageVector = MaterialSymbols.Outlined.W400.Home,
        contentDescription = "Home"
    )
}
```

---

## 📚 symbolcraft-material-symbols (Coming Soon)

**Status**: 🚧 Under Development

A Kotlin Multiplatform library with pre-generated Material Symbols icons.

### Planned Features

- **Complete Icon Set** - All Material Symbols icons
- **Multiple Variants** - Outlined, Rounded, Sharp styles
- **Variable Weights** - 100-700 font weights
- **Fill States** - Both filled and unfilled versions
- **Tree-shakable** - Only used icons included in final build

---

## 🔮 Upcoming in v0.4.0

SymbolCraft v0.4.0 will introduce several exciting features:

### 🌳 Tree Shaking (Planned)

Only generate icons that are actually used in your codebase through static code analysis.

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

**Benefits**:
- Smaller code generation time
- Reduced repository size
- Only compile what you need
- Automatic dependency tracking

### 📊 Performance Monitoring (Planned)

Detailed statistics about icon generation:

```
📊 Generation Report:
   ⏱️ Total time: 3.2s
   ⬇️ Download: 1.5s (avg 245KB/s)
   🔄 Conversion: 1.7s
   💾 Cache hit rate: 66.7% (8/12)
   📦 Generated: 12 icons, 245KB total
```

### 🚨 Enhanced Error Handling (Planned)

Better error categorization and recovery:
- Network errors with retry strategies
- Cache corruption detection and auto-repair
- Conversion failures with detailed diagnostics
- Configuration validation with helpful suggestions

---

## 🚀 Getting Started

### Requirements

- **Gradle**: 8.7+
- **Kotlin**: 2.0.0+
- **Compose Multiplatform**: 1.6.0+

### Installation

Add the plugin to your project:

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

For detailed setup and configuration, see the [Plugin Documentation](symbolcraft-plugin/README.md).

---

## 🛠️ Development

### Build All Modules

```bash
./gradlew clean build -x test
```

### Build Individual Module

```bash
./gradlew :symbolcraft-plugin:build
./gradlew :symbolcraft-runtime:build
./gradlew :symbolcraft-material-symbols:build
```

### Publish to Local Maven

```bash
./gradlew publishAllToMavenLocal
```

### Project Structure

```
SymbolCraft/
├── build.gradle.kts                 # Root build configuration
├── settings.gradle.kts              # Submodule configuration
├── gradle/libs.versions.toml        # Version catalog
│
├── symbolcraft-plugin/              # Gradle Plugin module
│   ├── README.md                    # Plugin documentation
│   ├── README_ZH.md                 # Plugin documentation (Chinese)
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│
├── symbolcraft-runtime/             # Runtime library module
│   ├── README.md
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/
│       ├── androidMain/
│       ├── jvmMain/
│       └── iosMain/
│
├── symbolcraft-material-symbols/    # Pre-generated icons module
│   ├── README.md
│   ├── build.gradle.kts
│   └── src/commonMain/
│
└── example/                         # Example app
    └── composeApp/
```

---

## 📄 License

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

## 🔗 Links

- **Documentation**: [Plugin README](symbolcraft-plugin/README.md)
- **Gradle Plugin Portal**: https://plugins.gradle.org/plugin/io.github.kingsword09.symbolcraft
- **Maven Central**: https://central.sonatype.com/artifact/io.github.kingsword09/symbolcraft
- **GitHub**: https://github.com/kingsword09/SymbolCraft
- **Issues**: https://github.com/kingsword09/SymbolCraft/issues

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

**Made with ❤️ by [kingsword09](https://github.com/kingsword09)**
