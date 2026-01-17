package com.eventhorizon.pulsar.core.packet;

import com.eventhorizon.pulsar.core.server.Server;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PacketMetadata(
        String sender,
        PacketTargetKind targetKind,
        String targetValue,
        long timestamp,
        UUID id
) {
    public PacketMetadata(String sender, PacketTargetKind targetKind, String targetValue) {
        this(sender, targetKind, targetValue, System.currentTimeMillis(), UUID.randomUUID());
    }

    public boolean isValidTarget(Server server) {
        if (sender.equals(server.getDiscriminator())) return false;

        return switch (targetKind) {
            case ALL -> true;
            case DIRECT -> targetValue.equals(server.getDiscriminator());
            case BLUEPRINT -> targetValue.equals(server.getBlueprint().id());
        };
    }
}
