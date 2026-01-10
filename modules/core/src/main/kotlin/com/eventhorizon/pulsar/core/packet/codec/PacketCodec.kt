package com.eventhorizon.pulsar.core.packet.codec

import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferInput
import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferOutput

interface PacketCodec {
    fun write(buffer: PacketByteBufferOutput)
    fun read(buffer: PacketByteBufferInput)
}