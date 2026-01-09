# 💫 Pulsar 

> **Warning:** Educational project only. Not intended for production use.

## Overview

**Pulsar** is a Kotlin library designed to interact with **Singularity**.
It enables communication between multiple Pulsar instances via Redis and provides a simple way to manage server lifecycles through Singularity's API.

The library focuses on **learning, experimentation, and simplicity** rather than production-ready features.

## Features

* List and access blueprints
* Create, update, and delete servers
* Send server status updates and reports
* Restart servers remotely
* Inter-instance messaging via Redis

## Quick Start

```kotlin
package com.eventhorizon.pulsar.client

val pulsarClient = PulsarClient(
    "your-singularity-endpoint.com",
    "your-secret-access-token"
)

suspend fun testPulsar() {
    val serverList = pulsarClient.server.list()
    println(serverList)
    
    val blueprintList = pulsarClient.blueprint.list()
    println(blueprintList)
    
    // Communication system WIP.
}
```

## Prerequisites

* Kotlin 2.3.30+
* [Ktor HTTP client](https://ktor.io/)
* Redis instance for inter-instance messaging

## Why Pulsar

Pulsar is a **lightweight API client for Singularity**, designed for:

* Learning server orchestration and communication
* Minimal setup without production overhead
* Simple, approachable experimentation
* Completely integration with Singularity.

## License

Educational use only.
