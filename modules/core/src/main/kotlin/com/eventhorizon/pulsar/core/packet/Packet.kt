package com.eventhorizon.pulsar.core.packet

import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferInput
import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferOutput
import com.eventhorizon.pulsar.core.packet.codec.PacketCodec
import com.eventhorizon.pulsar.core.server.Server
import java.util.UUID

enum class PacketTargetKind {
    ALL,
    BLUEPRINT,
    DIRECT
}

data class PacketMetadata(
    val sender: String,
    val targetKind: PacketTargetKind,
    val targetValue: String,

    val timestamp: Long = System.currentTimeMillis(),
    val id: UUID = UUID.randomUUID()
) {
    fun isValidTarget(server: Server): Boolean {
        if (sender == server.discriminator) return false

        return when (targetKind) {
            PacketTargetKind.ALL -> true
            PacketTargetKind.DIRECT -> targetValue == server.discriminator
            PacketTargetKind.BLUEPRINT -> targetValue == server.blueprint.id
        }
    }
}

abstract class Packet : PacketCodec {
    lateinit var metadata: PacketMetadata

    fun writeMetadata(buffer: PacketByteBufferOutput, data: PacketMetadata) {
        buffer.writeUUID(data.id)
        buffer.writeString(data.sender)
        buffer.writeEnum(metadata.targetKind)
        buffer.writeString(metadata.targetValue)
        buffer.writeLong(data.timestamp)
    }

    fun readMetadata(buffer: PacketByteBufferInput) {
        val id = buffer.readUUID()
        val sender = buffer.readString()
        val targetKind = buffer.readEnum<PacketTargetKind>()
        val targetValue = buffer.readString()
        val timestamp = buffer.readLong()

        metadata = PacketMetadata(
            sender,
            targetKind,
            targetValue,
            timestamp,
            id
        )
    }
}