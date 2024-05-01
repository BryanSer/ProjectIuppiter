import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}
group = "com.github.bryanser"
version = "1.0-SNAPSHOT"


dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    // https://mvnrepository.com/artifact/com.github.bryanser/BrAPI
    compileOnly("com.github.bryanser:BrAPI:Kt-1.1.1")
    compileOnly(project(":common:database"))
    compileOnly(project(":common:coroutines"))
}

tasks {
    shadowJar {
        exclude("kotlin/**")
    }
}
tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
