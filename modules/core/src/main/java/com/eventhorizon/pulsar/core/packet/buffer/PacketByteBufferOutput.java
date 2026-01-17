package com.eventhorizon.pulsar.core.packet.buffer;

import com.google.common.io.ByteStreams;

import java.util.UUID;

public final class PacketByteBufferOutput {

    private final com.google.common.io.ByteArrayDataOutput dataOutput =
            ByteStreams.newDataOutput();

    public PacketByteBufferOutput writeBoolean(boolean value) {
        dataOutput.writeBoolean(value);
        return this;
    }

    public PacketByteBufferOutput writeByte(byte value) {
        dataOutput.writeByte(value);
        return this;
    }

    public PacketByteBufferOutput writeShort(short value) {
        dataOutput.writeShort(value);
        return this;
    }

    public PacketByteBufferOutput writeFloat(float value) {
        dataOutput.writeFloat(value);
        return this;
    }

    public PacketByteBufferOutput writeDouble(double value) {
        dataOutput.writeDouble(value);
        return this;
    }

    public PacketByteBufferOutput writeUUID(UUID uuid) {
        dataOutput.writeBoolean(true);
        dataOutput.writeLong(uuid.getMostSignificantBits());
        dataOutput.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public PacketByteBufferOutput writeString(String text) {
        if (text.isEmpty())
            throw new InvalidDataException("String cannot be empty.");

        dataOutput.writeBoolean(true);
        dataOutput.writeUTF(text);
        return this;
    }

    public PacketByteBufferOutput writeStringArray(String[] array) {
        if (array.length == 0)
            throw new InvalidDataException("Array cannot be empty.");

        dataOutput.writeBoolean(true);
        dataOutput.writeInt(array.length);

        for (final var value : array) {
            writeString(value);
        }

        return this;
    }

    public PacketByteBufferOutput writeLong(long value) {
        dataOutput.writeLong(value);
        return this;
    }

    public PacketByteBufferOutput writeInt(int value) {
        dataOutput.writeInt(value);
        return this;
    }

    public void writeEnum(Enum<?> e) {
        dataOutput.writeInt(e.ordinal());
    }

    public byte[] toByteArray() {
        return dataOutput.toByteArray();
    }
}
