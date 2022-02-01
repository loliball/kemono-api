import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    id("com.github.dcendents.android-maven")
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.30"
    kotlin("jvm") version "1.6.10"
    application
}

group = "com.github.WhichWho"
version = "1.7-SNAPSHOT"

repositories {
    mavenCentral()
}

//publishing {
//    publications {
//
//    }
//}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}