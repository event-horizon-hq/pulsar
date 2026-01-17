plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}


dependencies {
    implementation("com.google.guava:guava:33.5.0-jre")

    implementation("io.prometheus:prometheus-metrics-core:1.4.3")
    implementation("io.prometheus:prometheus-metrics-instrumentation-jvm:1.4.3")
    implementation("io.prometheus:prometheus-metrics-exporter-httpserver:1.4.3")

    implementation("tools.jackson.core:jackson-core:3.0.3")
    implementation("tools.jackson.core:jackson-databind:3.0.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:3.0-rc5")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}