import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Versions {
    const val KOTLIN_TEST = "3.4.2"
    const val ARROW = "0.10.4"
}

plugins {
    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
    kotlin("kapt") version "1.3.61"
}

group = "pl.folsoft"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("io.arrow-kt:arrow-core:${Versions.ARROW}")
    implementation("io.arrow-kt:arrow-syntax:${Versions.ARROW}")
    kapt("io.arrow-kt:arrow-meta:${Versions.ARROW}")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:${Versions.KOTLIN_TEST}")
    testImplementation("io.kotlintest:kotlintest-extensions-spring:${Versions.KOTLIN_TEST}")
    testImplementation("io.kotlintest:kotlintest-assertions-arrow:${Versions.KOTLIN_TEST}")
    testImplementation("io.kotlintest:kotlintest-assertions-json:${Versions.KOTLIN_TEST}")
    testImplementation("io.mockk:mockk:1.9.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
