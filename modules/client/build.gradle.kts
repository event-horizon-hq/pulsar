plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:7.1.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")

    implementation("io.prometheus:prometheus-metrics-core:1.4.3")
    implementation("io.prometheus:prometheus-metrics-instrumentation-jvm:1.4.3")
    implementation("io.prometheus:prometheus-metrics-exporter-httpserver:1.4.3")

    implementation("tools.jackson.core:jackson-core:3.0.3")
    implementation("tools.jackson.core:jackson-databind:3.0.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:3.0-rc5")

    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("com.squareup.okhttp3:okhttp:5.3.0")

    implementation(project(":modules:core"))

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}
