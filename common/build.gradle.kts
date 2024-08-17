plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "io.github.skydynamic"
version = "1.0-SNAPSHOT"

dependencies {
    // Test
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0-RC")

    // Logger
    implementation("org.slf4j:slf4j-reload4j:1.7.36")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Ktor Server
    implementation("io.ktor:ktor-server-core:2.3.12")
    implementation("io.ktor:ktor-server-netty:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
    implementation("io.ktor:ktor-server-status-pages:2.3.12")
    // Ktor Client
    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-cio:2.3.12")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-client-serialization-jvm:2.3.12")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
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