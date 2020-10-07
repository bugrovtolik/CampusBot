plugins {
    application
    kotlin("jvm") version "1.4.10"
    id("com.heroku.sdk.heroku-gradle") version "2.0.0"
}

group = "me.abuhrov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.elbekD:kt-telegram-bot:1.3.5")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20200922-1.30.10")
    implementation("com.google.auth:google-auth-library-oauth2-http:0.21.1")
}

heroku {
    isIncludeBuildDir = false
    jdkVersion = "15"
}

application {
    mainClassName = "Main"
}

tasks {
    withType<Jar> {
        manifest { attributes(mapOf("Main-Class" to application.mainClassName)) }
    }
    register("stage") {
        dependsOn("build")
    }
}
