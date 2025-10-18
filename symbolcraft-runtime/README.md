# SymbolCraft Runtime

Runtime library for SymbolCraft icon system.

## Status

🚧 **Under Development** - This module is being prepared for future implementation.

## Planned Features

- **Lazy Icon Loading**: Load icons on-demand to minimize memory usage
- **Memory Caching**: LRU cache for frequently used icons
- **Multi-platform Support**: Android, iOS, JVM, and JS
- **MaterialSymbols API**: Fluent API similar to `androidx.compose.material.icons.Icons`
- **Performance Monitoring**: Cache hit rate and loading statistics

## Usage (Future)

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

## Module Structure

```
symbolcraft-runtime/
├── src/
│   ├── commonMain/kotlin/     # Platform-agnostic code
│   ├── androidMain/kotlin/    # Android-specific implementations
│   ├── jvmMain/kotlin/        # JVM-specific implementations
│   ├── iosMain/kotlin/        # iOS-specific implementations
│   └── commonTest/kotlin/     # Tests
└── build.gradle.kts
```

## Development

This module will be implemented after the project structure reorganization is complete.
