plugins {
    application
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

group = "io.github.skydynamic"
version = "1.0-SNAPSHOT"

dependencies {
    // Test
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)

    // Logger
    implementation(libs.slf4j.reload4j)

    // Gson
    implementation(libs.gson)

    // Ktor Server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.status.pages)
    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization.jvm)

    implementation(libs.kotlinx.serialization.json)
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClass.set("MaimaiProberKotlinKt")
}

kotlin {
    jvmToolchain(8)
}