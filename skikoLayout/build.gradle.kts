plugins {
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
}

group = "top.colter.skiko"
version = "0.0.2"

dependencies {
    api(libs.skiko.awt)
    implementation(libs.kotlinx.coroutines.core)
    implementation(kotlin("reflect"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    explicitApi()
    jvmToolchain(8)
}