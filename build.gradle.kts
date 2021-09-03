plugins {
    kotlin("jvm") version "1.5.30"
    application
}

application {
    mainClass.set("sims.michael.hexcrawl.HexCrawl")
}

group = "sims.michael.hexcrawl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxHeapSize = "8g"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("dk.ilios.asciihexgrid:asciihexgrid")
    implementation("com.github.ajalt.clikt:clikt:3.2.0")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.2")
}
