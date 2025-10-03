# SymbolCraft 🎨

> **Language**: [English](README.md) | [中文](README_ZH.md)

A powerful Gradle plugin for generating Material Symbols icons on-demand in Kotlin Multiplatform projects, featuring intelligent caching, deterministic builds, and high-performance parallel generation.

## ✨ Features

- 🚀 **On-demand generation** - Generate only the icons you actually use, reducing 99%+ bundle size compared to Material Icons Extended (11.3MB)
- 💾 **Smart caching** - 7-day SVG file cache with intelligent invalidation to avoid redundant network requests
- ⚡ **Parallel downloads** - Use Kotlin coroutines for parallel SVG downloads, dramatically improving generation speed
- 🎯 **Deterministic builds** - Ensure completely consistent code generation every time, Git-friendly and cache-friendly
- 🎨 **Full style support** - Support all Material Symbols styles (weight, variant, fill state)
- 🔧 **Smart DSL** - Convenient batch configuration methods and preset styles
- 📱 **High-quality output** - Use DevSrSouza/svg-to-compose library to generate authentic SVG path data
- 🔄 **Incremental builds** - Gradle task caching support, only regenerate changed icons
- 🏗️ **Configuration cache compatible** - Fully supports Gradle configuration cache for improved build performance
- 🔗 **Multi-platform support** - Support Android, Kotlin Multiplatform, JVM projects
- 👀 **Compose Preview** - Auto-generate Compose Preview functions, support both androidx and jetpack compose

## 📦 Installation

### 1. Add plugin to your project

In your `libs.versions.toml` file:

```toml
[plugins]
symbolCraft = { id = "io.github.kingsword09.symbolcraft", version = "x.x.x" }
```

In your `build.gradle.kts` file:

```kotlin
plugins {
    alias(libs.plugins.symbolCraft)
}
```

### 2. Configure the plugin

```kotlin
materialSymbols {
    // Basic configuration
    packageName.set("com.app.symbols")
    outputDirectory.set("src/commonMain/kotlin")  // Support multiplatform projects
    cacheEnabled.set(true)

    // Preview generation configuration (optional)
    generatePreview.set(true)          // Enable preview generation

    // Individual icon configuration
    symbol("search") {
        style(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
        style(weight = 500, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)
    }

    // Convenient batch configuration methods
    symbol("home") {
        standardWeights() // Auto-add 400, 500, 700 weights
    }

    symbol("person") {
        allVariants(weight = 400) // Add all variants (outlined, rounded, sharp)
    }

    symbol("settings") {
        bothFills(weight = 500, variant = SymbolVariant.ROUNDED) // Add both filled and unfilled
    }

    // Batch configure multiple icons
    symbols("favorite", "star", "bookmark") {
        weights(400, 500, variant = SymbolVariant.OUTLINED)
    }
}
```

## 🎯 Usage

### 1. Generate icons

Run the following command to generate configured icons:

```bash
./gradlew generateMaterialSymbols
```

The generation process will show detailed progress:
```
🎨 Generating Material Symbols...
📊 Symbols to generate: 12 icons
⬇️ Downloading SVG files...
   Progress: 5/12
   Progress: 10/12
   Progress: 12/12
✅ Download completed:
   📁 Total: 12
   ✅ Success: 12
   ❌ Failed: 0
   💾 From cache: 8
🔄 Converting SVGs to Compose ImageVectors...
✅ Successfully converted 12 icons
📦 SVG Cache: 45 files, 2.31 MB
```

### 2. Use in Compose

Generated icons can be used directly in your Compose code:

```kotlin
import com.yourcompany.app.symbols.MaterialSymbols
import com.yourcompany.app.symbols.materialsymbols.SearchW400Outlined
import com.yourcompany.app.symbols.materialsymbols.HomeW400Rounded
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun MyScreen() {
    // Method 1: Direct import usage
    Icon(
        imageVector = SearchW400Outlined,
        contentDescription = "Search"
    )

    // Method 2: Use through MaterialSymbols object
    Icon(
        imageVector = MaterialSymbols.SearchW400Outlined,
        contentDescription = "Search"
    )

    Icon(
        imageVector = MaterialSymbols.HomeW400Rounded,
        contentDescription = "Home"
    )
}
```

## 👀 Compose Preview Features

### Enable preview generation

```kotlin
materialSymbols {
    // Enable preview functionality
    generatePreview.set(true)

    // Optional: Custom preview settings
    previewIconSize.set(32)                    // Icon size (dp, default 24)
    previewBackgroundColor.set("#F5F5F5")      // Background color (default #FFFFFF)

    // Configure icons...
    symbol("home") {
        standardWeights()
    }
}
```

### Generated preview files

```kotlin
@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = MaterialSymbols.HomeW400Outlined, contentDescription = "")
    }
}
```

### View previews in IDE

After generation, you can view previews in Android Studio or IntelliJ IDEA's Preview panel:

1. Open the generated preview file: `{packageName}/preview/MaterialSymbolsPreviews.kt`
2. Click the "Preview" panel on the right side of the IDE
3. View individual icon previews or all icons overview

### Multi-platform preview support

- **Android projects**: Use `androidx.compose.ui.tooling.preview.Preview`
- **Desktop projects**: Use `androidx.compose.desktop.ui.tooling.preview.Preview`
- **Multiplatform projects**: Automatically support both preview annotations

## 📋 Configuration Options

### Basic configuration

```kotlin
materialSymbols {
    // Generated Kotlin package name
    packageName.set("com.yourcompany.app.symbols")

    // Output directory (supports multiplatform projects)
    outputDirectory.set("src/commonMain/kotlin")

    // Cache configuration
    cacheEnabled.set(true)
    cacheDirectory.set("material-symbols-cache")

    // Preview configuration
    generatePreview.set(false)          // Whether to generate Compose previews
    previewIconSize.set(24)             // Icon size in preview (dp)
    previewBackgroundColor.set("#FFFFFF") // Preview background color

    // Other options
    forceRegenerate.set(false)  // Force regenerate all icons
    minifyOutput.set(true)      // Minify output code
}
```

### Icon style parameters

- **weight**: Icon line thickness (300-700)
  - 300: Light
  - 400: Regular (default)
  - 500: Medium
  - 700: Bold

- **variant**: Icon style
  - `SymbolVariant.OUTLINED`: Line style (default)
  - `SymbolVariant.ROUNDED`: Rounded style
  - `SymbolVariant.SHARP`: Sharp style

- **fill**: Fill state
  - `SymbolFill.UNFILLED`: Outline (default)
  - `SymbolFill.FILLED`: Solid

### Convenient configuration methods

```kotlin
materialSymbols {
    symbol("example") {
        // Basic method
        style(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)

        // Batch weight configuration
        weights(400, 500, 700, variant = SymbolVariant.ROUNDED)

        // Material Design standard weights
        standardWeights(variant = SymbolVariant.OUTLINED)  // Add 400, 500, 700

        // All variants (outlined, rounded, sharp)
        allVariants(weight = 400, fill = SymbolFill.UNFILLED)

        // Add both filled and unfilled versions
        bothFills(weight = 500, variant = SymbolVariant.OUTLINED)
    }
}
```

### Generated file naming convention

Icon file name format: `{IconName}W{Weight}{Variant}{Fill}.kt`

Examples:
- `SearchW400Outlined.kt` - Search icon, 400 weight, outlined style, unfilled
- `HomeW500RoundedFill.kt` - Home icon, 500 weight, rounded style, filled
- `PersonW700Sharp.kt` - Person icon, 700 weight, sharp style, unfilled

## 🛠 Gradle Tasks

The plugin provides the following Gradle tasks:

| Task | Description |
|------|-------------|
| `generateMaterialSymbols` | Generate configured Material Symbols icons |
| `cleanSymbolsCache` | Clean cached SVG files |
| `validateSymbolsConfig` | Validate icon configuration validity |

### Task examples

```bash
# Generate icons (incremental build)
./gradlew generateMaterialSymbols

# Force regenerate all icons
./gradlew generateMaterialSymbols --rerun-tasks

# Clean cache
./gradlew cleanSymbolsCache

# Validate configuration
./gradlew validateSymbolsConfig
```

## 🗂 Project Structure

After using the plugin, your project structure might look like this:

```
your-project/
├── build.gradle.kts
├── .gitignore                              # Recommend adding generated files to ignore list
├── src/
│   └── commonMain/                         # Multiplatform project support
│       └── kotlin/
│           ├── com/app/
│           │   └── MainActivity.kt
│           └── generated/                  # Generated code directory
│               └── symbols/                # Icons package
│                   ├── MaterialSymbols.kt  # Icon access object
│                   ├── com/app/symbols/materialsymbols/
│                   │   ├── SearchW400Outlined.kt
│                   │   ├── HomeW500RoundedFill.kt
│                   │   └── PersonW700Sharp.kt
└── material-symbols-cache/                 # Temporary cache directory
    └── temp-svgs/                          # SVG temporary files
```

## 📁 Git Configuration Recommendations

### .gitignore Configuration

To avoid generated files showing as new files in Git, recommend adding the generation directory to `.gitignore`:

```gitignore
# SymbolCraft generated files
**/generated/symbols/
src/**/generated/
material-symbols-cache/

# Or more specific ignoring
src/commonMain/kotlin/generated/
src/main/kotlin/generated/
```

### Generated File Management Strategy

There are two strategies for handling generated files:

1. **Ignore generated files (recommended)**
   - Add generation directory to `.gitignore`
   - Run `generateMaterialSymbols` task in CI/CD
   - Advantages: Keep repository clean, avoid merge conflicts

2. **Commit generated files**
   - Commit generated files to repository
   - Suitable for scenarios requiring offline builds
   - Disadvantages: Increase repository size, may cause merge conflicts

## 🔄 Caching Mechanism

### Multi-layer cache architecture

1. **SVG download cache**
   - Default location: `build/material-symbols-cache/svg-cache/`
   - Validity: 7 days
   - Contains: SVG files + metadata (timestamp, URL, hash)
   - Auto-cleanup: Unused cache files are automatically removed when configuration changes
   - Path support: Both relative (to build directory) and absolute paths

2. **Gradle task cache**
   - Incremental build support
   - Change detection based on configuration hash
   - Support `@CacheableTask` annotation

### Cache path configuration

**Relative path (default):**
```kotlin
materialSymbols {
    cacheDirectory.set("material-symbols-cache")  // → build/material-symbols-cache/
    // Auto-cleanup: ✅ Enabled (project-local cache)
}
```

**Absolute path (for shared/global cache):**
```kotlin
materialSymbols {
    // Unix/Linux/macOS
    cacheDirectory.set("/var/tmp/symbolcraft")

    // Windows
    cacheDirectory.set("C:\\Temp\\SymbolCraft")

    // Network share (Windows UNC)
    cacheDirectory.set("\\\\server\\share\\symbolcraft-cache")

    // Auto-cleanup: ❌ Disabled (to prevent conflicts across projects)
}
```

### Shared cache considerations

When using absolute paths for shared caching across multiple projects:
- ✅ Cache is shared, reducing redundant downloads and saving space
- ✅ Faster builds when switching between projects
- ⚠️ **Automatic cleanup is disabled** to prevent cache conflicts
- 💡 Manual cleanup may be needed for old files

**Output when using shared cache:**
```
ℹ️  Cache cleanup skipped: Using shared cache outside build directory
   Cache location: /var/tmp/symbolcraft
   Shared caches are preserved to avoid conflicts across projects
```

**Manual cleanup (if needed):**
```bash
# Clean old files (older than 30 days)
find /var/tmp/symbolcraft -type f -mtime +30 -delete

# Or clean entire shared cache
rm -rf /var/tmp/symbolcraft
```

### Cache statistics

After generation completes, cache usage will be displayed:
```
📦 SVG Cache: 45 files, 2.31 MB
💾 From cache: 8/12 icons
🧹 Cleaned 3 unused cache files
```

## 🚀 Performance Optimization

### Parallel downloads

- Use Kotlin coroutines for parallel SVG downloads
- Support progress tracking and error retry
- Smart cache hit detection

### Deterministic builds

- Remove timestamps and other non-deterministic content
- Standardize floating-point precision
- Unified import statement ordering
- Ensure same input produces same output

### Configuration cache support

- Fully compatible with Gradle Configuration Cache
- Avoid accessing Project objects during task execution
- Use Provider API to improve build performance
- Support `--configuration-cache` parameter

### Error handling

- Automatic retry for network errors
- Detailed error classification and suggestions
- Graceful degradation to backup generators

## 📝 Advanced Configuration

### Icon search and selection

Use [Material Symbols Demo](https://marella.github.io/material-symbols/demo/) to:
- 🔍 Search and browse all available icons
- 👀 Preview different styles (Outlined, Rounded, Sharp)
- 📋 Copy icon names for configuration
- 🎨 View effects of different weights and fill states

### Batch configure icons

```kotlin
materialSymbols {
    // Basic icon set
    val basicIcons = listOf("home", "search", "person", "settings")
    basicIcons.forEach { icon ->
        symbol(icon) {
            standardWeights()
        }
    }

    // Navigation icon set
    val navIcons = listOf("arrow_back", "arrow_forward", "menu", "close")
    symbols(*navIcons.toTypedArray()) {
        weights(400, 500)
        bothFills(weight = 400)
    }
}
```

### Custom cache configuration

```kotlin
materialSymbols {
    // Disable cache (not recommended)
    cacheEnabled.set(false)

    // Custom cache directory (relative to build directory)
    cacheDirectory.set("custom-cache")  // → build/custom-cache/

    // Or use absolute path for shared cache across projects
    cacheDirectory.set("/var/tmp/symbolcraft")  // → /var/tmp/symbolcraft/

    // Force regenerate
    forceRegenerate.set(true)
}
```

## 🔍 Troubleshooting

### Common issues

1. **Network issues**
   ```
   ❌ Generation failed: Network issue
   💡 Network issue detected. Check internet connection and try again.
   ```

2. **Cache issues**
   ```bash
   # Clean SymbolCraft cache
   ./gradlew cleanSymbolsCache

   # Or clean entire build directory (including cache)
   ./gradlew clean

   # Force regenerate all icons
   ./gradlew generateMaterialSymbols --rerun-tasks
   ```

   Note: Starting from v0.1.2, cache files are stored in `build/material-symbols-cache/` by default and are automatically cleaned when running `./gradlew clean`.

3. **Icon not found**
   ```
   ⚠️ Failed to download: icon-name-W400Outlined (Icon not found in Material Symbols)
   ```
   Check if the icon name exists in [Material Symbols Demo](https://marella.github.io/material-symbols/demo/)

4. **Configuration cache issues**
   If you encounter configuration cache related errors, you can temporarily disable it:
   ```bash
   ./gradlew generateMaterialSymbols --no-configuration-cache
   ```

5. **Generated files showing as new files in Git**
   Add generation directory to `.gitignore`:
   ```gitignore
   **/generated/symbols/
   src/**/generated/
   ```

### Debug options

```bash
# Verbose logging
./gradlew generateMaterialSymbols --info

# Stack trace
./gradlew generateMaterialSymbols --stacktrace
```

## 🏗 Architecture Design

### Core components

- **MaterialSymbolsPlugin** - Main plugin class
- **MaterialSymbolsExtension** - DSL configuration interface
- **GenerateSymbolsTask** - Core generation task
- **SvgDownloader** - Smart SVG downloader
- **Svg2ComposeConverter** - SVG to Compose converter
- **PreviewGenerator** - Compose preview generator

### Data flow

```
Configuration → Style parsing → Parallel download → SVG conversion → Deterministic processing → Generate code → Preview generation
```

## 🎮 Example Application

The project includes a complete Kotlin Multiplatform example application that demonstrates SymbolCraft usage:

### Example app features

- **Multi-platform**: Supports Android, iOS, and Desktop (JVM)
- **Generated icons**: Uses SymbolCraft to generate Material Symbols icons
- **Preview support**: Includes generated Compose previews for all icons
- **Real-world usage**: Shows practical implementation patterns

### Running the example

```bash
# Navigate to example directory
cd example

# Generate Material Symbols icons
./gradlew generateMaterialSymbols

# Run Android app
./gradlew :composeApp:assembleDebug

# Run Desktop app
./gradlew :composeApp:run

# For iOS, open iosApp/iosApp.xcodeproj in Xcode
```

### Example configuration

The example app demonstrates various configuration options:

```kotlin
materialSymbols {
    packageName.set("io.github.kingsword09.example")
    outputDirectory.set("src/commonMain/kotlin")
    generatePreview.set(true)

    symbol("home") {
        standardWeights()
        style(400, SymbolVariant.ROUNDED)
        style(400, SymbolVariant.OUTLINED, SymbolFill.FILLED)
    }

    symbol("search") {
        standardWeights(SymbolVariant.OUTLINED)
    }

    symbol("person") {
        allVariants(500)
    }

    symbol("settings") {
        style(400, SymbolVariant.OUTLINED)
        bothFills(500, SymbolVariant.ROUNDED)
    }
}
```

## 🤝 Contributing

Issues and Pull Requests are welcome!

### Development environment setup

1. Clone the repository:
```bash
git clone https://github.com/kingsword09/SymbolCraft.git
cd SymbolCraft
```

2. Build the plugin:
```bash
./gradlew build
```

3. Publish to local Maven repository for testing:
```bash
./gradlew publishToMavenLocal
```

4. Run example application:
```bash
cd example
./gradlew generateMaterialSymbols
./gradlew :composeApp:assembleDebug
```

### Development workflow

1. Make changes to plugin source code in `src/main/kotlin/`
2. Build and publish locally: `./gradlew publishToMavenLocal`
3. Test changes using the example app: `cd example && ./gradlew generateMaterialSymbols`
4. Run tests: `./gradlew test`
5. Submit pull request

## 🙏 Acknowledgments

- [Material Symbols](https://fonts.google.com/icons) - Icon resources provided by Google
- [marella/material-symbols](https://github.com/marella/material-symbols) - Convenient icon browsing and search functionality
- [Material Symbols Demo](https://marella.github.io/material-symbols/demo/) - Icon search and preview tool
- [DevSrSouza/svg-to-compose](https://github.com/DevSrSouza/svg-to-compose) - Excellent SVG to Compose library
- [esm.sh](https://esm.sh) - CDN service for Material Symbols SVG files
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Android modern UI toolkit

## 📄 License

Apache 2.0 License - See [LICENSE](LICENSE) file for details