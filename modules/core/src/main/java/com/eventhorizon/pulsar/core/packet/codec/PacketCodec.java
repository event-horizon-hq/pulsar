package com.eventhorizon.pulsar.core.packet.codec;

import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferInput;
import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferOutput;

public interface PacketCodec {
    void write(PacketByteBufferOutput buffer);
    void read(PacketByteBufferInput buffer);
}
