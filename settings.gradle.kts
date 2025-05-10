rootProject.name = "Kronos"

pluginManagement {
    val kotlin_version: String by settings
    val ktor_version: String by settings
    plugins {
        kotlin("jvm") version kotlin_version
        id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
        id("io.ktor.plugin") version ktor_version
    }
}