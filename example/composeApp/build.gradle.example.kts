import io.github.kingsword09.symbolcraft.model.*
import io.github.kingsword09.symbolcraft.converter.NamingConvention

plugins {
    alias(libs.plugins.symbolCraft)
}

symbolCraft {
    packageName.set("io.github.kingsword09.example")
    
    // ========== 命名转换器配置 ==========
    
    // 方式 1: 使用默认 PascalCase（无需配置）
    // home-icon → HomeIcon
    // arrow_left → ArrowLeft
    
    // 方式 2: 添加后缀
    namingConvention.set(NamingConvention.PASCAL_CASE)
    namingSuffix.set("Icon")
    // home → HomeIcon
    // arrow-left → ArrowLeftIcon
    
    // 方式 3: 使用 camelCase
    namingConvention.set(NamingConvention.CAMEL_CASE)
    // home-icon → homeIcon
    // arrow_left → arrowLeft
    
    // 方式 4: 移除 Android 前缀
    namingConvention.set(NamingConvention.PASCAL_CASE)
    namingRemovePrefix.set("ic_")
    // ic_home → Home
    // ic_arrow_left → ArrowLeft
    
    // 方式 5: 移除尺寸后缀
    namingConvention.set(NamingConvention.PASCAL_CASE)
    namingRemoveSuffix.set("_24dp")
    // home_24dp → Home
    // arrow_left_24dp → ArrowLeft
    
    // 方式 6: 组合使用
    namingConvention.set(NamingConvention.PASCAL_CASE)
    namingRemovePrefix.set("ic_")
    namingRemoveSuffix.set("_24dp")
    namingSuffix.set("Icon")
    // ic_home_24dp → HomeIcon
    // ic_arrow_left_24dp → ArrowLeftIcon
    
    // 方式 7: snake_case
    namingConvention.set(NamingConvention.SNAKE_CASE)
    // HomeIcon → home_icon
    // arrow-left → arrow_left
    
    // 方式 8: SCREAMING_SNAKE_CASE
    namingConvention.set(NamingConvention.SCREAMING_SNAKE)
    // home-icon → HOME_ICON
    
    // 方式 9: kebab-case
    namingConvention.set(NamingConvention.KEBAB_CASE)
    // home_icon → home-icon
    
    // 方式 10: lowercase
    namingConvention.set(NamingConvention.LOWER_CASE)
    // HomeIcon → homeicon
    
    // 方式 11: UPPERCASE
    namingConvention.set(NamingConvention.UPPER_CASE)
    // home-icon → HOMEICON
    
    // ========== Material Symbols 示例 ==========
    materialSymbol("home") {
        weights(400, 500)
    }
    // 生成: Home400OutlinedUnfilled.kt, Home500OutlinedUnfilled.kt
    
    // ========== 外部图标示例 ==========
    externalIcons("arrow-left", "house", libraryName = "bootstrap") {
        urlTemplate = "{cdn}/bootstrap-icons/{name}.svg"
    }
    // 生成: ArrowLeft.kt, House.kt
    // 或如果配置了后缀: ArrowLeftIcon.kt, HouseIcon.kt
}
