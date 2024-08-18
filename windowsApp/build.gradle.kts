import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)

    java
}

group = "io.github.skydynamic"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":common"))
    implementation(libs.jna)
    implementation("net.java.dev.jna:jna-platform:5.14.0")

    implementation(libs.coil.compose)
    implementation(compose.desktop.currentOs)
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

compose.desktop {
    application {
        mainClass = "io.github.skydynamic.maiprober.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MaiProberWindowsApp"
            packageVersion = "1.0.0"
        }
    }
}