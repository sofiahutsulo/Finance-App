pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.kotlin.android") version "1.9.20"
        id("org.jetbrains.kotlin.jvm") version "1.9.20"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"

        id("com.android.application") version "8.2.0"
        id("com.google.dagger.hilt.android") version "2.48"
        id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FinanceManager"
include(":app")
include(":server")
