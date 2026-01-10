package com.eventhorizon.pulsar.client.packet.processor

import com.eventhorizon.pulsar.core.packet.Packet
import com.eventhorizon.pulsar.core.packet.PacketMetadata
import redis.clients.jedis.BinaryJedisPubSub

abstract class BinaryPacketProcessor : BinaryJedisPubSub() {
    abstract suspend fun startListening()

    abstract suspend fun publish(metadata: PacketMetadata, packet: Packet)
}