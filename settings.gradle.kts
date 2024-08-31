pluginManagement {
    repositories {
        maven {
            name = "Aliyun Gradle-plugin"
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
        maven {
            name = "Jitpack"
            url = uri("https://jitpack.io")
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "maimaiDX-prober"
include(":androidApp")
include(":common")
include(":android_stub")
include(":windowsApp")
include("skikoLayout")
