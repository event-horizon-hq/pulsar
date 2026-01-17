package com.eventhorizon.pulsar.core.packet.buffer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.util.UUID;

public final class PacketByteBufferInput {

    private final ByteArrayDataInput dataInput;

    public PacketByteBufferInput(byte[] data) {
        this.dataInput = ByteStreams.newDataInput(data);
    }

    public boolean readBoolean() {
        return dataInput.readBoolean();
    }

    public byte readByte() {
        return dataInput.readByte();
    }

    public short readShort() {
        return dataInput.readShort();
    }

    public float readFloat() {
        return dataInput.readFloat();
    }

    public double readDouble() {
        return dataInput.readDouble();
    }

    public <T extends Enum<T>> T readEnum(Class<T> type) {
        final var ordinal = readInt();
        final var values = type.getEnumConstants();

        if (ordinal < 0 || ordinal >= values.length)
            throw new InvalidDataException("Invalid enum ordinal: " + ordinal);

        return values[ordinal];
    }

    public UUID readUUID() {
        if (!dataInput.readBoolean())
            throw new InvalidDataException("Expected UUID, but found null.");

        final var most = dataInput.readLong();
        final var least = dataInput.readLong();

        return new UUID(most, least);
    }

    public String readString() {
        if (!dataInput.readBoolean())
            throw new InvalidDataException("Expected String, but found null.");

        return dataInput.readUTF();
    }

    public String[] readStringArray() {
        if (!dataInput.readBoolean())
            throw new InvalidDataException("Expected String array, but found null.");

        final var size = dataInput.readInt();
        if (size < 0)
            throw new InvalidDataException("Invalid array size: " + size);

        final var out = new String[size];
        for (int i = 0; i < size; i++) {
            out[i] = readString();
        }

        return out;
    }

    public long readLong() {
        return dataInput.readLong();
    }

    public int readInt() {
        return dataInput.readInt();
    }
}
