import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
    kotlin("kapt") version "1.3.31"
    `java-gradle-plugin`
}

group = "flavor.pie"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("kludge") {
            id = "flavor.pie.kludge"
            implementationClass = "flavor.pie.kludgec.KludgeGradlePlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("gradle-plugin-api"))
    val autoService = create("com.google.auto.service:auto-service:1.0-rc4")
    compileOnly(autoService)
    kapt(autoService)
    compileOnly(kotlin("compiler-embeddable"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
