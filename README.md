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

### Initializing Pulsar Client

```kotlin
val pulsarClient = PulsarClient(
    endpointUrl = "https://your-singularity-endpoint.com",
    secretAccessToken = "your-secret-access-token",
    redisURI = "redis://localhost:6379"
)

fun main() = runBlocking {
    pulsarClient.start() // Initializes PacketClient and server context
    println("Pulsar Client started!")
}
```

### Sending Packets

#### To a specific server

```kotlin
val targetServerDiscriminator = "server-123"
val gameModePacket = GameModePacket(
    targetName = "Player123",
    gameModeId = 1 // Example: Survival mode
)

pulsarClient.packets.publishToTarget(targetServerDiscriminator, gameModePacket)
```

#### To all servers in a blueprint

```kotlin
val blueprintId = "tower-dungeon"
pulsarClient.packets.publishToBlueprint(blueprintId, gameModePacket)
```

#### Broadcast to all servers

```kotlin
pulsarClient.packets.broadcastPacket(gameModePacket)
```

### Registering Handlers for Incoming Packets

```kotlin
class GameModePacketHandler : PacketHandler<GameModePacket>() {
    override fun onSendPacket(packet: GameModePacket) {
        println("Sent packet from ${packet.metadata.sender} to ${packet.metadata.targetValue}")
    }

    override fun onReceivePacket(packet: GameModePacket) {
        println("Received packet from ${packet.metadata.sender} for ${packet.targetName}")
        val player = getPlayer(packet.targetName)
        player?.gameMode = GameMode.fromId(packet.gameModeId)
    }
}

// Register the handler
PacketHandlerRegistry.register(GameModePacketHandler())
```
> **Note:** The server needs to have the **SERVER_ID** environment variable; on servers created by Singularity, it is automatically defined during creation. 

## Prerequisites

* Kotlin 2.3.30+
* [Ktor HTTP client](https://ktor.io/)
* Redis instance for inter-instance messaging

## Why Pulsar

Pulsar is a **lightweight API client for Singularity**, designed for:

* Learning server orchestration and communication
* Minimal setup without production overhead
* Simple, approachable experimentation
* Full integration with Singularity

## License

Educational use only.
