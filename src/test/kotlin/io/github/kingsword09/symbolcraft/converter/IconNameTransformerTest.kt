package io.github.kingsword09.symbolcraft.converter

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/** Unit tests for IconNameTransformer implementations */
class IconNameTransformerTest {

    // ========== ConventionNameTransformer - PascalCase ==========

    @Test
    fun `ConventionNameTransformer - PascalCase kebab input`() {
        val transformer = ConventionNameTransformer(NamingConvention.PASCAL_CASE)

        assertEquals("ArrowLeft", transformer.transform("arrow-left"))
        assertEquals("ChevronRight", transformer.transform("chevron-right"))
        assertEquals("UserCircle", transformer.transform("user-circle"))
    }

    @Test
    fun `ConventionNameTransformer - PascalCase snake input`() {
        val transformer = ConventionNameTransformer(NamingConvention.PASCAL_CASE)

        assertEquals("ArrowLeft", transformer.transform("arrow_left"))
        assertEquals("Home", transformer.transform("home"))
    }

    @Test
    fun `ConventionNameTransformer - PascalCase with suffix`() {
        val transformer =
            ConventionNameTransformer(convention = NamingConvention.PASCAL_CASE, suffix = "Icon")

        assertEquals("HomeIcon", transformer.transform("home"))
        assertEquals("ArrowLeftIcon", transformer.transform("arrow-left"))
    }

    @Test
    fun `ConventionNameTransformer - PascalCase with prefix`() {
        val transformer =
            ConventionNameTransformer(convention = NamingConvention.PASCAL_CASE, prefix = "Ic")

        assertEquals("IcHome", transformer.transform("home"))
        assertEquals("IcArrowLeft", transformer.transform("arrow-left"))
    }

    @Test
    fun `ConventionNameTransformer - PascalCase remove prefix`() {
        val transformer =
            ConventionNameTransformer(
                convention = NamingConvention.PASCAL_CASE,
                removePrefix = "ic_",
            )

        assertEquals("Home", transformer.transform("ic_home"))
        assertEquals("ArrowLeft", transformer.transform("ic_arrow_left"))
    }

    @Test
    fun `ConventionNameTransformer - PascalCase remove suffix`() {
        val transformer =
            ConventionNameTransformer(
                convention = NamingConvention.PASCAL_CASE,
                removeSuffix = "_24dp",
            )

        assertEquals("Home", transformer.transform("home_24dp"))
        assertEquals("ArrowLeft", transformer.transform("arrow_left_24dp"))
    }

    @Test
    fun `ConventionNameTransformer - PascalCase svg extension`() {
        val transformer = ConventionNameTransformer(NamingConvention.PASCAL_CASE)

        assertEquals("ArrowLeft", transformer.transform("arrow-left.svg"))
    }

    // ========== ConventionNameTransformer - camelCase ==========

    @Test
    fun `ConventionNameTransformer - camelCase`() {
        val transformer = ConventionNameTransformer(NamingConvention.CAMEL_CASE)

        assertEquals("arrowLeft", transformer.transform("arrow-left"))
        assertEquals("home", transformer.transform("home"))
        assertEquals("userCircle", transformer.transform("user-circle"))
    }

    // ========== ConventionNameTransformer - snake_case ==========

    @Test
    fun `ConventionNameTransformer - snake_case`() {
        val transformer = ConventionNameTransformer(NamingConvention.SNAKE_CASE)

        assertEquals("arrow_left", transformer.transform("arrow-left"))
        assertEquals("arrow_left", transformer.transform("ArrowLeft"))
        assertEquals("home", transformer.transform("home"))
    }

    @Test
    fun `ConventionNameTransformer - SCREAMING_SNAKE_CASE`() {
        val transformer = ConventionNameTransformer(NamingConvention.SCREAMING_SNAKE)

        assertEquals("ARROW_LEFT", transformer.transform("arrow-left"))
        assertEquals("ARROW_LEFT", transformer.transform("ArrowLeft"))
        assertEquals("HOME", transformer.transform("home"))
    }

    // ========== ConventionNameTransformer - kebab-case ==========

    @Test
    fun `ConventionNameTransformer - kebab-case`() {
        val transformer = ConventionNameTransformer(NamingConvention.KEBAB_CASE)

        assertEquals("arrow-left", transformer.transform("arrow_left"))
        assertEquals("arrow-left", transformer.transform("ArrowLeft"))
        assertEquals("home", transformer.transform("home"))
    }

    // ========== ConventionNameTransformer - lowercase ==========

    @Test
    fun `ConventionNameTransformer - lowercase`() {
        val transformer = ConventionNameTransformer(NamingConvention.LOWER_CASE)

        assertEquals("arrowleft", transformer.transform("arrow-left"))
        assertEquals("arrowleft", transformer.transform("ArrowLeft"))
        assertEquals("home", transformer.transform("home"))
    }

    // ========== ConventionNameTransformer - UPPERCASE ==========

    @Test
    fun `ConventionNameTransformer - UPPERCASE`() {
        val transformer = ConventionNameTransformer(NamingConvention.UPPER_CASE)

        assertEquals("ARROWLEFT", transformer.transform("arrow-left"))
        assertEquals("ARROWLEFT", transformer.transform("ArrowLeft"))
        assertEquals("HOME", transformer.transform("home"))
    }

    // ========== Custom Transformer (Anonymous Object) ==========

    @Test
    fun `Custom transformer - custom logic`() {
        val transformer =
            object : IconNameTransformer() {
                override fun transform(fileName: String): String {
                    return when {
                        fileName.startsWith("ic_") ->
                            fileName.removePrefix("ic_").split("_").joinToString("") {
                                it.replaceFirstChar { c -> c.titlecase() }
                            }
                        else -> fileName.replaceFirstChar { it.titlecase() }
                    }
                }
            }

        assertEquals("Home", transformer.transform("ic_home"))
        assertEquals("UserCircle", transformer.transform("ic_user_circle"))
        assertEquals("CustomIcon", transformer.transform("customIcon"))
    }

    @Test
    fun `Custom transformer - uppercase`() {
        val transformer =
            object : IconNameTransformer() {
                override fun transform(fileName: String): String {
                    return fileName.uppercase()
                }
            }

        assertEquals("HOME", transformer.transform("home"))
        assertEquals("ARROW-LEFT", transformer.transform("arrow-left"))
    }

    @Test
    fun `Custom transformer - getSignature default`() {
        val transformer =
            object : IconNameTransformer() {
                override fun transform(fileName: String): String = fileName.uppercase()
            }

        // Default signature should use class name
        assertTrue(transformer.getSignature().contains("IconNameTransformerTest"))
    }

    @Test
    fun `Custom transformer - getSignature override`() {
        val transformer =
            object : IconNameTransformer() {
                override fun transform(fileName: String): String = fileName.uppercase()

                override fun getSignature(): String = "MyCustomUppercaseTransformer"
            }

        assertEquals("MyCustomUppercaseTransformer", transformer.getSignature())
    }

    // ========== NameTransformerFactory ==========

    @Test
    fun `NameTransformerFactory - create for material-symbols`() {
        val transformer = NameTransformerFactory.create("material-symbols")

        assertTrue(transformer is ConventionNameTransformer)
        assertEquals("ArrowLeft", transformer.transform("arrow-left"))
    }

    @Test
    fun `NameTransformerFactory - create for external library`() {
        val transformer = NameTransformerFactory.create("external-bootstrap-icons")

        assertTrue(transformer is ConventionNameTransformer)
        assertEquals("ArrowLeft", transformer.transform("arrow-left"))
    }

    @Test
    fun `NameTransformerFactory - pascalCase`() {
        val transformer = NameTransformerFactory.pascalCase()
        assertEquals("ArrowLeft", transformer.transform("arrow-left"))

        val withSuffix = NameTransformerFactory.pascalCase(suffix = "Icon")
        assertEquals("ArrowLeftIcon", withSuffix.transform("arrow-left"))

        val withPrefix = NameTransformerFactory.pascalCase(prefix = "Ic")
        assertEquals("IcArrowLeft", withPrefix.transform("arrow-left"))
    }

    @Test
    fun `NameTransformerFactory - camelCase`() {
        val transformer = NameTransformerFactory.camelCase()
        assertEquals("arrowLeft", transformer.transform("arrow-left"))
    }

    @Test
    fun `NameTransformerFactory - snakeCase`() {
        val lower = NameTransformerFactory.snakeCase(uppercase = false)
        assertEquals("arrow_left", lower.transform("arrow-left"))

        val upper = NameTransformerFactory.snakeCase(uppercase = true)
        assertEquals("ARROW_LEFT", upper.transform("arrow-left"))
    }

    @Test
    fun `NameTransformerFactory - kebabCase`() {
        val transformer = NameTransformerFactory.kebabCase()
        assertEquals("arrow-left", transformer.transform("arrow_left"))
    }

    @Test
    fun `NameTransformerFactory - lowerCase`() {
        val transformer = NameTransformerFactory.lowerCase()
        assertEquals("arrowleft", transformer.transform("arrow-left"))
    }

    @Test
    fun `NameTransformerFactory - upperCase`() {
        val transformer = NameTransformerFactory.upperCase()
        assertEquals("ARROWLEFT", transformer.transform("arrow-left"))
    }

    @Test
    fun `NameTransformerFactory - fromConvention`() {
        val transformer =
            NameTransformerFactory.fromConvention(
                convention = NamingConvention.PASCAL_CASE,
                suffix = "Icon",
                removePrefix = "ic_",
            )

        assertEquals("HomeIcon", transformer.transform("ic_home"))
    }

    // ========== Edge Cases ==========

    @Test
    fun `ConventionNameTransformer - empty string`() {
        val transformer = ConventionNameTransformer(NamingConvention.PASCAL_CASE)
        assertEquals("", transformer.transform(""))
    }

    @Test
    fun `ConventionNameTransformer - single word`() {
        val transformer = ConventionNameTransformer(NamingConvention.PASCAL_CASE)
        assertEquals("Home", transformer.transform("home"))
    }

    @Test
    fun `ConventionNameTransformer - multiple delimiters`() {
        val transformer = ConventionNameTransformer(NamingConvention.PASCAL_CASE)
        assertEquals("FooBarBaz", transformer.transform("foo-bar_baz"))
    }

    @Test
    fun `ConventionNameTransformer - camelCase input to PascalCase`() {
        val transformer = ConventionNameTransformer(NamingConvention.PASCAL_CASE)
        assertEquals("ArrowLeft", transformer.transform("arrowLeft"))
    }
}
