plugins {
    kotlin("jvm") version "1.4.10"
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
