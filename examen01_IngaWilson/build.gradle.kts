plugins {
    kotlin("jvm") version "2.0.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("MainKt") // Ajusta al nombre de tu archivo principal
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(15)
}