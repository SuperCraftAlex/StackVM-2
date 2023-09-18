plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "me.alex_s168"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.github.SuperCraftAlex:ktlib:1e47e3902a")
    implementation("com.github.ajalt.mordant:mordant:2.1.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("me.alex_s168.stackvm2.MainKt")
}