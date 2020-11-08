plugins {
    kotlin("jvm") version "1.4.10"
    `maven-publish`
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

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/royratcliffe/logicbot")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
