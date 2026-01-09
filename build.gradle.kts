import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.1"
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "com.gradleup.shadow")

    repositories {
        mavenCentral()
    }


    kotlin {
        jvmToolchain(25)
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<ShadowJar> {
        destinationDirectory.set(File("$rootDir/allJars"))
    }
}