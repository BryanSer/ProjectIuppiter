import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "com.github.bryanser"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    api("org.ktorm:ktorm-core:3.6.0")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:4.0.3")
    compileOnly("com.github.bryanser:BrAPI:Kt-1.1.1")
    implementation("org.ktorm:ktorm-support-mysql:3.6.0")


}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}