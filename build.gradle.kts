plugins {
    application
    kotlin("jvm") version "1.4.20"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "me.abuhrov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.elbekD:kt-telegram-bot:1.3.6")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20200922-1.30.10")
    implementation("com.google.auth:google-auth-library-oauth2-http:0.21.1")
}

application {
    mainClassName = "MainKt"
}

tasks {
    withType<Jar> {
        manifest { attributes(mapOf("Main-Class" to application.mainClassName)) }
    }
    register("stage") {
        dependsOn("build")
    }
}
