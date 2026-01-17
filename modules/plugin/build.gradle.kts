import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java

    id("com.gradleup.shadow") version "9.3.1"
}

version = "1.0.0"

repositories {
    maven {
        name = "luck-repo"
        url = uri("https://repo.lucko.me/")
        content {
            includeModule("me.lucko", "spark-api")
        }
    }
}

dependencies {
    compileOnly(fileTree("libs"))

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    compileOnly("me.lucko:spark-api:0.1-SNAPSHOT")

    implementation(project(":modules:core"))
    implementation(project(":modules:client"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("event-horizon-pulsar")
    archiveVersion.set(version.toString())

    destinationDirectory.set(File("$rootDir/allJars"))
}