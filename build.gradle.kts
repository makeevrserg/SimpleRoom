import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Kotlin {
    const val version = "1.7.0"
    const val coroutines = "1.6.3"
    const val json = "1.3.3"
    const val kaml = "0.46.0"
}
plugins {
    kotlin("jvm") version "1.7.0"
}
java {
    withSourcesJar()
    withJavadocJar()
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_17
}
group = "com.makeevrserg"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Kotlin.coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Kotlin.coroutines}")
    // jdbc
    implementation("org.xerial:sqlite-jdbc:3.23.1")
    testImplementation(kotlin("test"))
}
tasks {
    test {
        useJUnitPlatform()
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    withType<JavaCompile>() {
        options.encoding = "UTF-8"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
