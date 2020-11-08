plugins {
    kotlin("jvm") version "1.4.10"
}

group = "io.github.royratcliffe"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test-junit"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "latest.release")
    implementation("it.unibo.tuprolog", "dsl-solve-jvm", "latest.release")
    implementation("it.unibo.tuprolog", "parser-theory", "latest.release")
}
