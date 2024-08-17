plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("dev.rikka.tools.materialthemebuilder") version "1.3.3"
}

android {
    namespace = "io.github.skydynamic.maiprober_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.skydynamic.maiprober_android"
        minSdk = 28
        targetSdk = 34
        versionCode = 1000
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    compileOnly(project(":android_stub"))
    implementation(project(":common"))
    implementation(libs.api)
    implementation(libs.provider)
    implementation(libs.hiddenapibypass)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

materialThemeBuilder {
    themes {
        create("AmazingOrange") {
            lightThemeParent = "Theme.Material3.DayNight.NoActionBar"
            darkThemeParent = "Theme.Material3.DayNight.NoActionBar"
            lightThemeFormat = "Theme.Light.%s"
            darkThemeFormat = "Theme.Dark.%s"
            secondaryColor = "#FF8A80"
            primaryColor = "#FF1744"
        }
    }
}