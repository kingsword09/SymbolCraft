package io.github.kingsword09.example

import io.github.kingsword09.symbolcraft.converter.NameTransformerFactory
import io.github.kingsword09.symbolcraft.converter.NamingConvention

/**
 * 命名转换器使用示例
 * 
 * 这些示例展示了如何在代码中使用不同的命名转换器。
 * 注意：Gradle 插件会自动使用 PascalCase，这些示例主要用于学习和自定义场景。
 */
object TransformerExamples {

    /**
     * 示例 1: 基本的 PascalCase 转换
     */
    fun basicPascalCase() {
        val transformer = NameTransformerFactory.pascalCase()
        
        println("=== PascalCase 转换 ===")
        println("arrow-left → ${transformer.transform("arrow-left")}")      // ArrowLeft
        println("user_circle → ${transformer.transform("user_circle")}")    // UserCircle
        println("HomeIcon.svg → ${transformer.transform("HomeIcon.svg")}")  // HomeIcon
    }

    /**
     * 示例 2: 带后缀的 PascalCase
     */
    fun pascalCaseWithSuffix() {
        val transformer = NameTransformerFactory.pascalCase(suffix = "Icon")
        
        println("\n=== PascalCase + Icon 后缀 ===")
        println("home → ${transformer.transform("home")}")                  // HomeIcon
        println("arrow-left → ${transformer.transform("arrow-left")}")      // ArrowLeftIcon
        println("user_circle → ${transformer.transform("user_circle")}")    // UserCircleIcon
    }

    /**
     * 示例 3: camelCase 转换
     */
    fun camelCaseTransform() {
        val transformer = NameTransformerFactory.camelCase()
        
        println("\n=== camelCase 转换 ===")
        println("arrow-left → ${transformer.transform("arrow-left")}")      // arrowLeft
        println("user_circle → ${transformer.transform("user_circle")}")    // userCircle
        println("HomeIcon → ${transformer.transform("HomeIcon")}")          // homeIcon
    }

    /**
     * 示例 4: snake_case 和 SCREAMING_SNAKE_CASE
     */
    fun snakeCaseTransforms() {
        val lowerSnake = NameTransformerFactory.snakeCase(uppercase = false)
        val upperSnake = NameTransformerFactory.snakeCase(uppercase = true)
        
        println("\n=== snake_case 转换 ===")
        println("arrow-left → ${lowerSnake.transform("arrow-left")}")      // arrow_left
        println("HomeIcon → ${lowerSnake.transform("HomeIcon")}")          // home_icon
        
        println("\n=== SCREAMING_SNAKE_CASE 转换 ===")
        println("arrow-left → ${upperSnake.transform("arrow-left")}")      // ARROW_LEFT
        println("HomeIcon → ${upperSnake.transform("HomeIcon")}")          // HOME_ICON
    }

    /**
     * 示例 5: Android 图标前缀处理
     */
    fun androidIconPrefix() {
        val transformer = NameTransformerFactory.fromConvention(
            convention = NamingConvention.PASCAL_CASE,
            removePrefix = "ic_"
        )
        
        println("\n=== 移除 Android ic_ 前缀 ===")
        println("ic_home → ${transformer.transform("ic_home")}")                    // Home
        println("ic_arrow_left → ${transformer.transform("ic_arrow_left")}")        // ArrowLeft
        println("ic_user_circle → ${transformer.transform("ic_user_circle")}")      // UserCircle
    }

    /**
     * 示例 6: 移除尺寸后缀
     */
    fun removeSizeSuffix() {
        val transformer = NameTransformerFactory.fromConvention(
            convention = NamingConvention.PASCAL_CASE,
            removeSuffix = "_24dp"
        )
        
        println("\n=== 移除尺寸后缀 ===")
        println("home_24dp → ${transformer.transform("home_24dp")}")                // Home
        println("arrow_left_24dp → ${transformer.transform("arrow_left_24dp")}")    // ArrowLeft
    }

    /**
     * 示例 7: 组合使用 - 移除前缀和后缀
     */
    fun combinedPrefixSuffixRemoval() {
        val transformer = NameTransformerFactory.fromConvention(
            convention = NamingConvention.PASCAL_CASE,
            removePrefix = "ic_",
            removeSuffix = "_24dp"
        )
        
        println("\n=== 移除前缀和后缀 ===")
        println("ic_home_24dp → ${transformer.transform("ic_home_24dp")}")                      // Home
        println("ic_arrow_left_24dp → ${transformer.transform("ic_arrow_left_24dp")}")          // ArrowLeft
    }

    /**
     * 示例 8: 自定义 Lambda 转换器
     */
    fun customLambdaTransformer() {
        // 简单的大写转换
        val upperTransformer = NameTransformerFactory.custom { fileName ->
            fileName.removeSuffix(".svg").uppercase()
        }
        
        println("\n=== 自定义 Lambda - 全大写 ===")
        println("home.svg → ${upperTransformer.transform("home.svg")}")                // HOME
        println("arrow-left → ${upperTransformer.transform("arrow-left")}")            // ARROW-LEFT
        
        // 条件转换
        val conditionalTransformer = NameTransformerFactory.custom { fileName ->
            when {
                fileName.startsWith("ic_") -> {
                    // Android 图标：移除前缀，转为 PascalCase
                    fileName.removePrefix("ic_")
                        .split("_")
                        .joinToString("") { it.replaceFirstChar { c -> c.titlecase() } }
                }
                fileName.endsWith("_24dp") -> {
                    // 带尺寸的图标：移除后缀，转为 PascalCase
                    fileName.removeSuffix("_24dp")
                        .split("_", "-")
                        .joinToString("") { it.replaceFirstChar { c -> c.titlecase() } }
                }
                else -> {
                    // 默认：PascalCase
                    fileName.split("_", "-")
                        .joinToString("") { it.replaceFirstChar { c -> c.titlecase() } }
                }
            }
        }
        
        println("\n=== 自定义 Lambda - 条件转换 ===")
        println("ic_home → ${conditionalTransformer.transform("ic_home")}")                    // Home
        println("arrow_left_24dp → ${conditionalTransformer.transform("arrow_left_24dp")}")    // ArrowLeft
        println("user-circle → ${conditionalTransformer.transform("user-circle")}")            // UserCircle
    }

    /**
     * 示例 9: Font Awesome 样式处理
     */
    fun fontAwesomeStyle() {
        val transformer = NameTransformerFactory.custom { fileName ->
            val cleaned = fileName.removeSuffix(".svg").removePrefix("fa-")
            val parts = cleaned.split("-")
            
            // 检测样式前缀 (s=solid, r=regular, l=light)
            val style = when (parts.firstOrNull()) {
                "s" -> "Solid"
                "r" -> "Regular"
                "l" -> "Light"
                else -> ""
            }
            
            val iconParts = if (style.isNotEmpty() && parts.first().length == 1) {
                parts.drop(1)
            } else {
                parts
            }
            
            val iconName = iconParts.joinToString("") { 
                it.replaceFirstChar { c -> c.titlecase() } 
            }
            
            "$iconName$style"
        }
        
        println("\n=== Font Awesome 样式处理 ===")
        println("fa-s-home → ${transformer.transform("fa-s-home")}")              // HomeSolid
        println("fa-r-user → ${transformer.transform("fa-r-user")}")              // UserRegular
        println("fa-l-heart → ${transformer.transform("fa-l-heart")}")            // HeartLight
        println("fa-arrow-left → ${transformer.transform("fa-arrow-left")}")      // ArrowLeft
    }

    /**
     * 示例 10: Material Symbols 格式（保持兼容）
     */
    fun materialSymbolsFormat() {
        val transformer = NameTransformerFactory.pascalCase()
        
        println("\n=== Material Symbols 格式 ===")
        println("home_400_outlined_unfilled → ${transformer.transform("home_400_outlined_unfilled")}")    // Home400OutlinedUnfilled
        println("search_500_rounded_filled → ${transformer.transform("search_500_rounded_filled")}")      // Search500RoundedFilled
    }

    /**
     * 运行所有示例
     */
    fun runAllExamples() {
        basicPascalCase()
        pascalCaseWithSuffix()
        camelCaseTransform()
        snakeCaseTransforms()
        androidIconPrefix()
        removeSizeSuffix()
        combinedPrefixSuffixRemoval()
        customLambdaTransformer()
        fontAwesomeStyle()
        materialSymbolsFormat()
        
        println("\n=== 所有示例运行完成 ===")
    }
}

/**
 * 主函数 - 运行所有示例
 */
fun main() {
    TransformerExamples.runAllExamples()
}
