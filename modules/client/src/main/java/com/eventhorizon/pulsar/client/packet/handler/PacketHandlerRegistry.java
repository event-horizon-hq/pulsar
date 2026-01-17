package com.eventhorizon.pulsar.client.packet.handler;

import com.eventhorizon.pulsar.core.packet.Packet;
import com.eventhorizon.pulsar.core.packet.handler.PacketHandler;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@UtilityClass
public final class PacketHandlerRegistry {

    private static final Map<String, List<PacketHandler<?>>> REGISTERED_HANDLERS = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketHandlerRegistry.class.getName());

    public static <T extends Packet> void register(@NotNull Class<T> packetClass, @NotNull PacketHandler<T> handler) {
        final var qualifiedName = packetClass.getName();

        REGISTERED_HANDLERS
                .computeIfAbsent(qualifiedName, k -> new CopyOnWriteArrayList<>())
                .add(handler);

        LOGGER.info("[PULSAR] A packet handler registered for {}", qualifiedName);
    }

    public static <T extends Packet> void unregister(Class<T> packetClass, PacketHandler<T> handler) {
        final var qualifiedName = packetClass.getName();
        final var handlers = REGISTERED_HANDLERS.get(qualifiedName);

        if (handlers != null) {
            if (handlers.remove(handler)) {
                LOGGER.info("[PULSAR] A packet handler unregistered for: {}", qualifiedName);
            }
        }
    }

    public static List<PacketHandler<?>> getAll(String packetName) {
        final var handlers = REGISTERED_HANDLERS.get(packetName);

        return handlers != null ?
                Collections.unmodifiableList(handlers) :
                Collections.emptyList();
    }
}