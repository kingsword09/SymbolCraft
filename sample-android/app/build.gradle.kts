import io.github.kingsword09.symbolcraft.model.SymbolVariant
import io.github.kingsword09.symbolcraft.model.SymbolFill

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("io.github.kingsword09.symbolcraft")
}

android {
    namespace = "io.github.kingsword09.symbolcraft.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.kingsword09.symbolcraft.sample"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    // Compose compiler is managed by 'org.jetbrains.kotlin.plugin.compose'

    // Align Java/Kotlin toolchains to 17 to avoid target mismatch
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Kotlin JVM toolchain for the module
kotlin {
    jvmToolchain(17)
}

materialSymbols {
    // where to output generated vectors
    outputDirectory.set("src/main/kotlin/generated/symbols")
    packageName.set("io.github.kingsword09.symbolcraft.symbols")

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

// Note: svg-to-compose is now applied and configured internally by SymbolCraft.

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.08.00"))
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // Runtime SVG rendering using Coil
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-svg:2.7.0")
}
