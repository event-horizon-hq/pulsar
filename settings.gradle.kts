dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}


plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}


rootProject.name = "pulsar"

include(
    "modules:core",
    "modules:client",
    "modules:plugin"
)