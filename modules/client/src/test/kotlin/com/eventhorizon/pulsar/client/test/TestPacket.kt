package com.eventhorizon.pulsar.client.test

import com.eventhorizon.pulsar.core.packet.Packet
import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferInput
import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferOutput
import java.util.UUID

class TestPacket(
    var uuid: UUID? = null
) : Packet() {

    override fun write(buffer: PacketByteBufferOutput) {
        buffer.writeUUID(uuid!!)
    }

    override fun read(buffer: PacketByteBufferInput) {
        uuid = buffer.readUUID()
    }
}