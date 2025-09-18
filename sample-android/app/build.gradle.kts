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

    // configure a few icons to preview with new API
    symbol("search") {
        style(weight = 400, variant = io.github.kingsword09.symbolcraft.model.SymbolVariant.OUTLINED, fill = io.github.kingsword09.symbolcraft.model.SymbolFill.UNFILLED)
        style(weight = 500, variant = io.github.kingsword09.symbolcraft.model.SymbolVariant.OUTLINED, fill = io.github.kingsword09.symbolcraft.model.SymbolFill.FILLED)
    }
    symbol("person") {
        style(weight = 400, variant = io.github.kingsword09.symbolcraft.model.SymbolVariant.OUTLINED, fill = io.github.kingsword09.symbolcraft.model.SymbolFill.UNFILLED)
    }
    symbol("home") {
        style(weight = 400, variant = io.github.kingsword09.symbolcraft.model.SymbolVariant.ROUNDED, fill = io.github.kingsword09.symbolcraft.model.SymbolFill.UNFILLED)
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
