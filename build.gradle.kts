import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
    kotlin("kapt") version "1.3.31"
    maven
    `maven-publish`
}

group = "flavor.pie"
version = "0.1.0"

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

tasks.register<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    classifier = "sources"
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
            pom {
                name.set("KludgeCompiler")
                description.set("The compiler plugin companion to Kludge.")
                url.set("https://github.com/pie-flavor/KludgeCompiler")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/pie-flavor/KludgeCompiler/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("pie_flavor")
                        name.set("Adam Spofford")
                        email.set("aspofford.as@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/pie-flavor/KludgeCompiler.git")
                    developerConnection.set("scm:git:ssh://github.com/pie-flavor/KludgeCompiler.git")
                    url.set("https://github.com/pie-flavor/KludgeCompiler/")
                }
            }
        }
        repositories {
            maven {
                val spongePublishingUri: String by project
                val spongePublishingUsername: String by project
                val spongePublishingPassword: String by project
                url = uri(spongePublishingUri)
                credentials {
                    username = spongePublishingUsername
                    password = spongePublishingPassword
                }
            }
        }
    }
}
