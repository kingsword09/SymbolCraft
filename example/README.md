# SymbolCraft Example Application

This is a complete Kotlin Multiplatform example application demonstrating the usage of the **SymbolCraft** Gradle plugin.

## Overview

This example showcases:
- **Multi-platform support**: Android, iOS, and Desktop (JVM)
- **Icon generation**: Using SymbolCraft to generate icons from multiple sources
- **Material Symbols**: Various weights, variants, and fill states
- **External icon libraries**: MDI (Material Design Icons) integration
- **Compose Preview**: Generated preview functions for all icons
- **Modern configuration**: Using the latest SymbolCraft DSL features

## Project Structure

```
example/
├── composeApp/                    # Shared Compose Multiplatform app
│   ├── src/
│   │   ├── commonMain/           # Common code for all platforms
│   │   │   └── kotlin/
│   │   │       ├── generated/    # Generated icons (gitignored)
│   │   │       │   └── symbols/  # SymbolCraft output
│   │   │       └── App.kt        # Main app composable
│   │   ├── androidMain/          # Android-specific code
│   │   ├── iosMain/              # iOS-specific code
│   │   └── jvmMain/              # Desktop-specific code
│   └── build.gradle.kts          # SymbolCraft configuration
└── iosApp/                        # iOS app wrapper
```

## SymbolCraft Configuration

The example demonstrates various configuration options in `composeApp/build.gradle.kts`:

```kotlin
symbolCraft {
    // Output directory for generated icons
    outputDirectory.set("src/commonMain/kotlin/generated/symbols")
    packageName.set("io.github.kingsword09.example")
    generatePreview.set(true)

    // Icon naming configuration
    naming {
        pascalCase()  // Use PascalCase convention
    }

    // Material Symbols examples
    materialSymbol("search") {
        standardWeights() // Adds 400, 500, 700 weights
    }

    materialSymbol("home") {
        weights(400, 500, variant = SymbolVariant.ROUNDED)
        bothFills(weight = 400) // Both filled and unfilled
    }

    materialSymbol("person") {
        allVariants(weight = SymbolWeight.W500) // All variants
    }

    materialSymbol("settings") {
        style(weight = 400, variant = SymbolVariant.OUTLINED)
        style(weight = 500, variant = SymbolVariant.ROUNDED, fill = SymbolFill.FILLED)
    }

    // External icons from MDI
    externalIcons(*listOf("abacus", "ab-testing").toTypedArray(), libraryName = "mdi") {
        urlTemplate = "https://esm.sh/@mdi/svg@latest/svg/{name}.svg"
    }

    // External icons with style variants
    externalIcons(*listOf("home", "search", "person", "settings", "arrow_back").toTypedArray(), 
                  libraryName = "official") {
        urlTemplate = "https://rawcdn.githack.com/google/material-design-icons/master/symbols/web/{name}/materialsymbolsrounded/{name}{fill}_24px.svg?min=1"
        styleParam("fill") {
            values("", "_fill1")  // unfilled, filled variants
        }
    }
}
```

## Getting Started

> **Important**: This example project is **independent** from the root project. You must publish the plugin to `mavenLocal` first. See [SETUP.md](SETUP.md) for details.

### Step 0: Publish Plugin (Required)

From the root SymbolCraft directory:

```bash
cd ..
./gradlew :symbolcraft-plugin:publishToMavenLocal
cd example
```

### Step 1: Generate Icons

Before building the app, generate the icons:

```bash
./gradlew generateSymbolCraftIcons
```

This will:
- Download SVG files from configured sources
- Convert them to Compose ImageVectors
- Generate Kotlin files in `composeApp/src/commonMain/kotlin/generated/symbols/`

### Step 2: Build and Run

#### Android

```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Or run directly on connected device/emulator
./gradlew :composeApp:installDebug
```

You can also open the project in Android Studio and run from there.

#### Desktop (JVM)

```bash
./gradlew :composeApp:run
```

#### iOS

1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Select a simulator or device
3. Click Run (⌘R)

Alternatively, from the terminal:
```bash
# Open in Xcode
open iosApp/iosApp.xcodeproj
```

## Platform-Specific Notes

### Android
- **Min SDK**: 24
- **Target SDK**: 35
- **Compile SDK**: 35

### iOS
- **Deployment Target**: iOS 15.0+
- **Requires**: Xcode 14.0 or later
- **Architecture**: arm64 (device), arm64 simulator

### Desktop
- **JVM Target**: 17
- **Supported OS**: Windows, macOS, Linux

## Development Tasks

### Common Gradle Tasks

```bash
# Generate icons
./gradlew generateSymbolCraftIcons

# Clean generated icons
./gradlew cleanSymbolCraftIcons

# Clean icon cache
./gradlew cleanSymbolCraftCache

# Validate configuration
./gradlew validateSymbolCraftConfig

# Clean everything
./gradlew clean

# Build all platforms
./gradlew build
```

### Troubleshooting

**Problem**: Icons not found after generation  
**Solution**: Run `./gradlew clean` then `./gradlew generateSymbolCraftIcons`

**Problem**: Build fails with missing imports  
**Solution**: Ensure icons are generated before building: `./gradlew generateSymbolCraftIcons`

**Problem**: iOS build fails  
**Solution**: Run `./gradlew clean` and regenerate the iOS framework

## Using Generated Icons

Generated icons can be used in Compose like this:

```kotlin
import io.github.kingsword09.example.icons.materialsymbols.Icons
import io.github.kingsword09.example.icons.materialsymbols.icons.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun MyScreen() {
    // Direct import
    Icon(
        imageVector = SearchW400Outlined,
        contentDescription = "Search"
    )

    // Via Icons accessor object
    Icon(
        imageVector = Icons.HomeW500Rounded,
        contentDescription = "Home"
    )

    // External icons
    Icon(
        imageVector = Icons.AbacusMdi,
        contentDescription = "Abacus"
    )
}
```

## Preview Support

The example enables preview generation with `generatePreview.set(true)`. You can view icon previews:

1. Open generated icon files in Android Studio/IntelliJ IDEA
2. Look for `@Preview` annotated functions
3. Click the "Preview" panel on the right side
4. View rendered icons directly in the IDE

## Known Issues

- **Compose Hot Reload**: Not compatible with Kotlin 2.0.0 + Compose Compiler. Currently disabled. See [SETUP.md](SETUP.md#known-issues) for details.
- **Android Build**: May require compileSdk 35+ for some dependencies. Desktop (JVM) build works perfectly.

## Learn More

- [Setup Guide](SETUP.md) - Detailed configuration and troubleshooting
- [SymbolCraft Documentation](../README.md)
- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Material Symbols](https://fonts.google.com/icons)

## License

This example is part of the SymbolCraft project and is licensed under Apache 2.0.