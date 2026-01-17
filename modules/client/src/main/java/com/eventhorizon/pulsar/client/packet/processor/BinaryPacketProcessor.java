package com.eventhorizon.pulsar.client.packet.processor;

import com.eventhorizon.pulsar.core.packet.Packet;
import com.eventhorizon.pulsar.core.packet.PacketMetadata;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.BinaryJedisPubSub;

public abstract class BinaryPacketProcessor extends BinaryJedisPubSub {

    public abstract void startListening();

    public abstract void publish(@NotNull PacketMetadata metadata, @NotNull Packet packet);
}
