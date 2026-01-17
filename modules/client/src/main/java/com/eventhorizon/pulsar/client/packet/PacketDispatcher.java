package com.eventhorizon.pulsar.client.packet;

import com.eventhorizon.pulsar.client.context.ServerContext;
import com.eventhorizon.pulsar.client.packet.processor.BinaryPacketProcessor;
import com.eventhorizon.pulsar.client.packet.processor.RedisBinaryPacketProcessor;
import com.eventhorizon.pulsar.core.blueprint.Blueprint;
import com.eventhorizon.pulsar.core.packet.Packet;
import com.eventhorizon.pulsar.core.packet.PacketMetadata;
import com.eventhorizon.pulsar.core.packet.PacketTargetKind;
import com.eventhorizon.pulsar.core.server.Server;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public final class PacketDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketDispatcher.class);

    private final boolean debugMode;
    private final BinaryPacketProcessor packetProcessor;

    public PacketDispatcher(@NotNull String redisURI, boolean debugMode) {
        this.packetProcessor = new RedisBinaryPacketProcessor(redisURI, debugMode);
        this.debugMode = debugMode;
    }

    public void start() {
        this.packetProcessor.startListening();
    }

    public void stop() {
        PulsarNetworkThread.LISTENER.shutdown();
        PulsarNetworkThread.EMITTER.shutdown();
    }

    public void publishTo(@NotNull Server server, @NotNull Packet packet) {
        this.publishTo(PacketTargetKind.DIRECT, server.getDiscriminator(), packet);
    }

    public void publishTo(@NotNull Blueprint blueprint, @NotNull Packet packet) {
        this.publishTo(PacketTargetKind.BLUEPRINT, blueprint.id(), packet);
    }

    public void publishTo(@NotNull PacketTargetKind targetKind, @NotNull String value, @NotNull Packet packet) {
        final var packetMetadata = PacketMetadata.builder()
                .id(UUID.randomUUID())
                .sender(ServerContext.getDiscriminator())
                .targetKind(targetKind)
                .targetValue(value)
                .timestamp(System.currentTimeMillis())
                .build();

        PulsarNetworkThread.EMITTER.execute(() -> {
            this.packetProcessor.publish(packetMetadata, packet);

            if (debugMode) {
                LOGGER.info(
                        "Packet {} sent to {} {} at {}",
                        packet.getClass().getName(),
                        targetKind.name(),
                        value,
                        packetMetadata.timestamp()
                );
            }
        });
    }

    public void broadcast(@NotNull Packet packet) {
        final var packetMetadata = PacketMetadata.builder()
                .id(UUID.randomUUID())
                .sender(ServerContext.getDiscriminator())
                .targetKind(PacketTargetKind.ALL)
                .targetValue("ALL")
                .timestamp(System.currentTimeMillis())
                .build();

        PulsarNetworkThread.EMITTER.execute(() -> {
            this.packetProcessor.publish(packetMetadata, packet);

            if (debugMode) {
                LOGGER.info(
                        "Packet {} broadcasted at {}",
                        packet.getClass().getName(),
                        packetMetadata.timestamp()
                );
            }
        });
    }
}
