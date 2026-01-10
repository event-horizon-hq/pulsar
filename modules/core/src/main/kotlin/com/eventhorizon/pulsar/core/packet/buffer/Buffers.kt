package com.eventhorizon.pulsar.core.packet.buffer

import com.google.common.io.ByteStreams
import java.util.*

class InvalidDataException(message: String) : RuntimeException(message)

class PacketByteBufferOutput {

    private val dataOutput = ByteStreams.newDataOutput()

    fun writeBoolean(value: Boolean): PacketByteBufferOutput {
        dataOutput.writeBoolean(value)
        return this
    }

    fun writeByte(value: Byte): PacketByteBufferOutput {
        dataOutput.writeByte(value.toInt())
        return this
    }

    fun writeShort(value: Short): PacketByteBufferOutput {
        dataOutput.writeShort(value.toInt())
        return this
    }

    fun writeFloat(value: Float): PacketByteBufferOutput {
        dataOutput.writeFloat(value)
        return this
    }

    fun writeDouble(value: Double): PacketByteBufferOutput {
        dataOutput.writeDouble(value)
        return this
    }

    fun writeUUID(uuid: UUID): PacketByteBufferOutput {
        dataOutput.writeBoolean(true)
        dataOutput.writeLong(uuid.mostSignificantBits)
        dataOutput.writeLong(uuid.leastSignificantBits)
        return this
    }

    fun writeString(text: String): PacketByteBufferOutput {
        if (text.isEmpty())
            throw InvalidDataException("String cannot be empty.")

        dataOutput.writeBoolean(true)
        dataOutput.writeUTF(text)
        return this
    }

    fun writeStringArray(array: Array<String>): PacketByteBufferOutput {
        if (array.isEmpty()) throw InvalidDataException("Array cannot be empty.")

        dataOutput.writeBoolean(true)
        dataOutput.writeInt(array.size)

        array.forEach { writeString(it) }
        return this
    }

    fun writeLong(value: Long): PacketByteBufferOutput {
        dataOutput.writeLong(value)
        return this
    }

    fun writeInt(value: Int): PacketByteBufferOutput {
        dataOutput.writeInt(value)
        return this
    }

    fun writeEnum(enum: Enum<*>) = dataOutput.writeInt(enum.ordinal)

    fun toByteArray(): ByteArray = dataOutput.toByteArray()
}

class PacketByteBufferInput(private val data: ByteArray) {
    private val dataInput = ByteStreams.newDataInput(data)

    fun readBoolean(): Boolean = dataInput.readBoolean()

    fun readByte(): Byte = dataInput.readByte()

    fun readShort(): Short = dataInput.readShort()

    fun readFloat(): Float = dataInput.readFloat()

    fun readDouble(): Double = dataInput.readDouble()

    inline fun <reified T : Enum<T>> readEnum() : T {
        val ordinal = this.readInt()
        return enumValues<T>()[ordinal]
    }

    fun readUUID(): UUID {
        if (!dataInput.readBoolean())
            throw InvalidDataException("Expected UUID, but found null.")

        val most = dataInput.readLong()
        val least = dataInput.readLong()

        return UUID(most, least)
    }

    fun readString(): String {
        if (!dataInput.readBoolean())
            throw InvalidDataException("Expected String, but found null.")

        return dataInput.readUTF()
    }

    fun readStringArray(): Array<String> {
        if (!dataInput.readBoolean())
            throw InvalidDataException("Expected String array, but found null.")

        val size = dataInput.readInt()
        if (size < 0) throw InvalidDataException("Invalid array size: $size")

        return Array(size) {
            readString()
        }
    }

    fun readLong(): Long = dataInput.readLong()

    fun readInt(): Int = dataInput.readInt()
}