import io.github.kingsword09.symbolcraft.model.SymbolFill
import io.github.kingsword09.symbolcraft.model.SymbolVariant
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.symbolCraft)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm()
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "io.github.kingsword09.example"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.kingsword09.example"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

materialSymbols {
    // where to output generated vectors
    outputDirectory.set("src/commonMain/kotlin/generated/symbols")
    packageName.set("io.github.kingsword09.example")

    // 测试：启用预览生成但没有依赖
    generatePreview.set(true)

    // 使用新的便捷方法
    symbol("search") {
        standardWeights() // 添加 400, 500, 700 权重的 outlined 样式
    }

    symbol("home") {
        weights(400, 500, variant = SymbolVariant.ROUNDED) // 指定变体
        bothFills(weight = 400) // 同时添加填充和未填充
    }

    symbol("person") {
        allVariants(weight = 500) // 所有变体（outlined, rounded, sharp）
    }

    // 传统方式仍然支持
    symbol("settings") {
        style(weight = 400, variant = SymbolVariant.OUTLINED)
        style(weight = 500, variant = SymbolVariant.ROUNDED, fill = SymbolFill.FILLED)
    }
}

compose.desktop {
    application {
        mainClass = "io.github.kingsword09.example.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.kingsword09.example"
            packageVersion = "1.0.0"
        }
    }
}
