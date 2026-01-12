plugins {
    kotlin("jvm") version "2.3.0"
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.3.0")
    implementation("io.ktor:ktor-client-okhttp:3.3.0")
    implementation("io.ktor:ktor-client-logging:3.3.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.0")

    implementation("redis.clients:jedis:7.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")

    implementation("io.prometheus:prometheus-metrics-core:1.4.3")
    implementation("io.prometheus:prometheus-metrics-instrumentation-jvm:1.4.3")
    implementation("io.prometheus:prometheus-metrics-exporter-httpserver:1.4.3")

    implementation(project(":modules:core"))
}