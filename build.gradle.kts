val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("io.ktor.plugin") version "3.2.3"
}

group = "dev.artisra"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}



dependencies {
    // KTOR
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // SERIALIZATION
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.server.config.yaml)

    // LOGGING
    implementation(libs.logback.classic)

    // EXPOSED & DATABASE
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.mysql.connector.java)

    // OTHER
    implementation(libs.jbcrypt)

    // TESTING
    testImplementation("com.zaxxer:HikariCP:5.1.0")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}