import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
}

group = "me.harry"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect:1.3.41"))
    implementation(kotlin("script-runtime"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("com.beust:klaxon:5.5")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("org.apache.poi:poi:4.0.0")
    implementation ("org.apache.poi:poi-ooxml:4.0.0")
    implementation ("com.fasterxml:aalto-xml:1.0.0")
    implementation ("org.apache.poi:poi-scratchpad:3.8-beta3")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("org.apache.commons:commons-lang3:3.6")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.1")
    testImplementation("io.mockk:mockk:1.9")
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}