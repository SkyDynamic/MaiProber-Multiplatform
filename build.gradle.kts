plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false

    kotlin("jvm") version "2.0.0" apply false
    kotlin("plugin.serialization") version "1.8.21" apply false
}