import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
    kotlin("kapt") version "1.3.31"
}

group = "flavor.pie"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    val autoService = create("com.google.auto.service:auto-service:1.0-rc4")
    compileOnly(autoService)
    kapt(autoService)
    compileOnly(kotlin("compiler-embeddable"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
