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
    implementation(libs.jna.platform)

    implementation(compose.components.resources)
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(libs.coil.compose)
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