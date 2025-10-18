# SymbolCraft Material Symbols

Pre-generated Material Symbols icons for Compose Multiplatform.

## Status

🚧 **Under Development** - This module is being prepared for future implementation.

## Planned Features

- **Pre-generated Icons**: All Material Symbols icons pre-generated and packaged
- **Multiple Variants**: Outlined, Rounded, and Sharp styles
- **Variable Weights**: 100-700 font weights
- **Fill States**: Both filled and unfilled versions
- **Lazy Loading**: Icons loaded on-demand via runtime module
- **Tree-shakable**: Only used icons included in final build

## Usage (Future)

```kotlin
import io.github.kingsword09.symbolcraft.materialsymbols.MaterialSymbols

@Composable
fun MyScreen() {
    Icon(
        imageVector = MaterialSymbols.Outlined.W400.Home,
        contentDescription = "Home"
    )
    
    Icon(
        imageVector = MaterialSymbols.Rounded.W500.Search,
        contentDescription = "Search"
    )
}
```

## Icon Organization

```
symbolcraft-material-symbols/
├── src/
│   └── commonMain/kotlin/
│       └── io/github/kingsword09/symbolcraft/materialsymbols/
│           ├── MaterialSymbols.kt          # Main access object
│           ├── outlined/
│           │   ├── w100/                   # Weight 100
│           │   ├── w200/
│           │   ├── w300/
│           │   ├── w400/                   # Default weight
│           │   ├── w500/
│           │   ├── w600/
│           │   └── w700/
│           ├── rounded/
│           │   └── ...
│           └── sharp/
│               └── ...
└── build.gradle.kts
```

## Module Structure

This module will contain thousands of pre-generated icon files, organized by:
1. **Variant** (Outlined, Rounded, Sharp)
2. **Weight** (100, 200, 300, 400, 500, 600, 700)
3. **Fill** (0 = unfilled, 1 = filled)

Each icon is a Kotlin file containing a `lazy` delegated `ImageVector` property.

## Development

This module will be implemented after:
1. Runtime module is complete
2. Icon generation strategy is finalized
3. Build optimization for large file sets is tested

## Dependencies

- `symbolcraft-runtime` - For lazy loading and caching infrastructure
- `compose.ui` - For ImageVector rendering
