package com.eventhorizon.pulsar.core.packet;

import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferInput;
import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferOutput;
import com.eventhorizon.pulsar.core.packet.codec.PacketCodec;
import lombok.Getter;

@Getter
public abstract class Packet implements PacketCodec {
    protected PacketMetadata metadata;

    public void writeMetadata(PacketByteBufferOutput buffer, PacketMetadata data) {
        buffer.writeUUID(data.id());
        buffer.writeString(data.sender());
        buffer.writeEnum(data.targetKind());
        buffer.writeString(data.targetValue());
        buffer.writeLong(data.timestamp());
    }

    public void readMetadata(PacketByteBufferInput buffer) {
        final var id = buffer.readUUID();
        final var sender = buffer.readString();
        final var targetKind = buffer.readEnum(PacketTargetKind.class);
        final var targetValue = buffer.readString();
        final var timestamp = buffer.readLong();

        this.metadata = new PacketMetadata(sender, targetKind, targetValue, timestamp, id);
    }

}
