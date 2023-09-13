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

    implementation("com.github.SuperCraftAlex:ktlib:2ddcb142fb")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}