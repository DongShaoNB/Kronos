val kotlin_version: String by project
val ktor_version: String by project
val koin_version: String by project
val exposed_version: String by project
val logback_version: String by project

// Plugins version follow settings.gradle.kts
plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.ktor.plugin")
}

group = "cc.dsnb"
version = "1.0.0"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

ktor {
    fatJar {
        archiveFileName.set("${project.name}-${version}.jar")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    // Ktor
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-rate-limit:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("jakarta.mail:jakarta.mail-api:2.1.3")
    implementation("org.eclipse.angus:jakarta.mail:2.0.3")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    // Koin
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    // Database
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.mysql:mysql-connector-j:9.2.0")
    // Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    // Lettuce
    implementation("io.lettuce:lettuce-core:6.6.0.RELEASE")
    // Coroutines
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.10.2")
    // Other
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}
