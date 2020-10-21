plugins {
    kotlin("jvm") version "1.4.10"
}

group = "io.github.ratcliffe"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test-junit"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.9")
    implementation("it.unibo.tuprolog", "dsl-solve-jvm", "0.13.0")
    implementation("it.unibo.tuprolog", "parser-theory", "0.13.0")
}
