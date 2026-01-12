plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0-RC")
    implementation("com.google.guava:guava:33.5.0-jre")

    implementation("io.prometheus:prometheus-metrics-core:1.4.3")
    implementation("io.prometheus:prometheus-metrics-instrumentation-jvm:1.4.3")
    implementation("io.prometheus:prometheus-metrics-exporter-httpserver:1.4.3")
}