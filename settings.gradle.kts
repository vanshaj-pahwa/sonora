pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
        // oss.sonatype.org (legacy OSSRH) removed — Sonatype shut it down; its flaky
        // 504s disabled the whole repo set and blocked resolution fallbacks.
        maven("https://jogamp.org/deployment/maven")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        // compottie's Compose-1.12 snapshot builds (skiko 0.148.2) are published to the
        // Maven Central Portal snapshot repo below. The legacy OSSRH repo
        // (oss.sonatype.org) was removed — Sonatype shut it down and its flaky 504s
        // disabled the repo set, blocking fallback to this one.
        maven("https://central.sonatype.com/repository/maven-snapshots/")
        maven("https://jogamp.org/deployment/maven")
        maven(url = "https://raw.githubusercontent.com/bravepipeproject/maven-repo/master/repository")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Core modules live in the `core` git submodule INSIDE this repo.
// We intentionally resolve ONLY the in-repo submodule and no longer probe a
// sibling `../core` outside Sonora: another project (FPT Play `core`) shares
// the same folder name one level up, and the old co-development lookup bound to
// it by mistake, breaking configuration with ":common ... does not exist".
val coreDir = File(rootDir, "core")
val serviceDir = File(rootDir, "core/service")
val mediaDir = File(rootDir, "core/media")

rootProject.name = "Sonora"
include(
    ":app",
    ":composeApp",
    ":common",
    ":data",
    ":domain",
    ":ktor-ext",
    ":kotlin-ytmusic-scraper",
    ":spotify",
    ":ai-service",
    ":lyrics-service",
    ":media-jvm",
    ":media-jvm-ui",
    ":media3",
    ":media3-ui",
    ":cast",
    ":kizzy",
)

// core modules
project(":common").projectDir = File(coreDir, "common")
project(":data").projectDir = File(coreDir, "data")
project(":domain").projectDir = File(coreDir, "domain")

// service modules
project(":ktor-ext").projectDir = File(serviceDir, "ktor-ext")
project(":ai-service").projectDir = File(serviceDir, "ai-service")
project(":lyrics-service").projectDir = File(serviceDir, "lyrics-service")
project(":kotlin-ytmusic-scraper").projectDir = File(serviceDir, "kotlin-ytmusic-scraper")
project(":spotify").projectDir = File(serviceDir, "spotify")
project(":kizzy").projectDir = File(serviceDir, "kizzy")

// media modules
project(":media-jvm").projectDir = File(mediaDir, "media-jvm")
project(":media-jvm-ui").projectDir = File(mediaDir, "media-jvm-ui")
project(":media3").projectDir = File(mediaDir, "media3")
project(":media3-ui").projectDir = File(mediaDir, "media3-ui")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")