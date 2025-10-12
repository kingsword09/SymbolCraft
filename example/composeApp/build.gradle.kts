import io.github.kingsword09.symbolcraft.model.*
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
            implementation(compose.uiTooling)
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

symbolCraft {
    // Output directory for generated icons
    outputDirectory.set("src/commonMain/kotlin/generated/symbols")
    packageName.set("io.github.kingsword09.example")

    // Enable preview generation (optional)
    generatePreview.set(true)

    // Material Symbols examples
    materialSymbol("search") {
        standardWeights() // Adds 400, 500, 700 weights with outlined variant
    }

    materialSymbol("home") {
        weights(400, 500, variant = SymbolVariant.ROUNDED) // Specify variant
        bothFills(weight = 400) // Both filled and unfilled
    }

    materialSymbol("person") {
        allVariants(weight = SymbolWeight.W500) // All variants (outlined, rounded, sharp)
    }

    // Traditional style method still supported
    materialSymbol("settings") {
        style(weight = 400, variant = SymbolVariant.OUTLINED)
        style(weight = 500, variant = SymbolVariant.ROUNDED, fill = SymbolFill.FILLED)
    }

    // External icons with URL template
    externalIcon("ab-testing", libraryName = "mdi") {
        urlTemplate = "{cdn}/@mdi/svg@latest/svg/{name}.svg"
    }

    // Generic method for custom IconConfig implementations
    // iconConfig("custom", MyCustomIconConfig())
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
