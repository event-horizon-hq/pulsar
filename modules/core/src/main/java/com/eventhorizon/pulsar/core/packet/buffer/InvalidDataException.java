package com.eventhorizon.pulsar.core.packet.buffer;

public final class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}
