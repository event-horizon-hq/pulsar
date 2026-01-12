package com.eventhorizon.pulsar.client.test

import com.eventhorizon.pulsar.client.PulsarClient
import com.eventhorizon.pulsar.client.packet.handler.PacketHandlerRegistry
import com.eventhorizon.pulsar.client.packet.processor.scope.PulsarNetworkScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

fun main() {
    try {
        System.setProperty("SERVER_ID", "vIDAYtGy")

        val pulsarClient = PulsarClient(
            endpointUrl = "http://localhost:8080",
            secretAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjIwODM2MDI2NzYsImlhdCI6MTc2ODA2OTg3Nn0.F21Enh-rFU85Quiosii2qwCSNI6HlBKA3tyJMK1iniY",
            redisURI = "localhost:6379",
            debugMode = true
        )

        pulsarClient.start()

        PacketHandlerRegistry.register(TestPacketHandler())

        PulsarNetworkScope.launch {
            delay(2.seconds)

            pulsarClient.packets.broadcast(TestPacket(UUID.randomUUID()))
        }

        System.clearProperty("SERVER_ID")
    } catch (e: Exception) {
        e.printStackTrace()
        System.clearProperty("SERVER_ID")
    }
}