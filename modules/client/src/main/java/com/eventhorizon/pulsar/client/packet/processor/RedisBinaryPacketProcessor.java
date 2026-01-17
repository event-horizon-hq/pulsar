package com.eventhorizon.pulsar.client.packet.processor;

import com.eventhorizon.pulsar.client.context.ServerContext;
import com.eventhorizon.pulsar.client.factory.RedisConnectionFactory;
import com.eventhorizon.pulsar.client.packet.PulsarNetworkThread;
import com.eventhorizon.pulsar.client.packet.exception.PacketConstructorException;
import com.eventhorizon.pulsar.client.packet.handler.PacketHandlerRegistry;
import com.eventhorizon.pulsar.core.packet.Packet;
import com.eventhorizon.pulsar.core.packet.PacketMetadata;
import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferInput;
import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferOutput;
import com.eventhorizon.pulsar.core.packet.handler.PacketHandler;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Cache;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public final class RedisBinaryPacketProcessor extends BinaryPacketProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisBinaryPacketProcessor.class);
    private static final byte[] CHANNEL = "pulsar:network:packets".getBytes();

    private final Cache<String, Constructor<? extends Packet>> packetConstructors =
            Caffeine.newBuilder()
                    .maximumSize(512)
                    .build();

    private final JedisPool publisherJedis;
    private final JedisPool subscriberJedis;

    private final boolean debugMode;

    public RedisBinaryPacketProcessor(
            @NotNull String redisURI,
            boolean debugMode
    ) {
        this.publisherJedis = RedisConnectionFactory.create(redisURI);
        this.subscriberJedis = RedisConnectionFactory.create(redisURI);
        this.debugMode = debugMode;
    }

    @Override
    public void startListening() {
        PulsarNetworkThread.LISTENER.execute(() -> {
            while (Thread.currentThread().isAlive()) {
                try (final var jedis = this.subscriberJedis.getResource()) {
                    jedis.subscribe(this, CHANNEL);
                } catch (Exception exception) {
                    try {
                        Thread.sleep(Duration.ofSeconds(1));
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void publish(@NotNull PacketMetadata metadata, @NotNull Packet packet) {
        final var packetName = packet.getClass().getName();
        final var bufferOutput = new PacketByteBufferOutput();

        bufferOutput.writeString(packetName);
        packet.writeMetadata(bufferOutput, metadata);
        packet.write(bufferOutput);

        try (final var jedis = publisherJedis.getResource()) {
            jedis.publish(CHANNEL, bufferOutput.toByteArray());
        }
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        if (!Arrays.equals(channel, CHANNEL)) return;

        PulsarNetworkThread.LISTENER.execute(() -> {
            try {
                processPacket(message);
            } catch (Throwable throwable) {
                if (debugMode) throwable.printStackTrace();
            }
        });
    }

    private void processPacket(byte[] message) throws Exception {
        final var byteBufferInput = new PacketByteBufferInput(message);
        final var packet = resolvePacket(byteBufferInput);

        packet.readMetadata(byteBufferInput);

        if (!packet.getMetadata().isValidTarget(ServerContext.getServer())) return;

        packet.read(byteBufferInput);

        final var name = packet.getClass().getName();
        for (final var handler : PacketHandlerRegistry.getAll(name)) {
            try {
                final var packetHandler = (PacketHandler<Packet>) handler;

                packetHandler.onReceivePacket(packet);
            } catch (Throwable throwable) {
                if (debugMode) throwable.printStackTrace();
            }
        }
    }

    private Packet resolvePacket(PacketByteBufferInput in) throws Exception {
        final var className = in.readString();

        final var constructor = packetConstructors.get(className, clazzName -> {
            try {
                final var clazz = Class.forName(clazzName).asSubclass(Packet.class);
                if (!Packet.class.isAssignableFrom(clazz)) {
                    return null;
                }

                return clazz.getDeclaredConstructor();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        if (constructor == null) {
            throw new PacketConstructorException("The packet must have at least one constructor with no arguments.");
        }

        return constructor.newInstance();
    }
}
