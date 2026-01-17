package com.eventhorizon.pulsar.client.context;

import com.eventhorizon.pulsar.client.PulsarClient;
import com.eventhorizon.pulsar.core.blueprint.Blueprint;
import com.eventhorizon.pulsar.core.server.ServerReport;
import com.eventhorizon.pulsar.core.server.Server;
import com.eventhorizon.pulsar.core.server.ServerStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Global context that exposes the current {@link Server} for this process.
 *
 * <p>This class acts as a static holder for the server associated with this
 * client instance. The value is set during bootstrap (e.g. in {@link PulsarClient#start()}).</p>
 *
 * <p>The {@code server} field is {@code volatile}, ensuring cross-thread
 * visibility when the reference is replaced. The intended usage pattern is
 * to always swap the entire {@link Server} instance, never mutate it in place.</p>
 *
 * <p>The exposed accessors delegate directly to the current {@link Server},
 * simplifying access to global information such as {@link Blueprint} and the
 * server discriminator without propagating dependencies across the codebase.</p>
 *
 * <p>If {@code server} is not initialized, any call to this class will result
 * in a {@link IllegalArgumentException}. Therefore, {@code setServer(...)} must be
 * invoked before any access.</p>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerContext {

    @Setter
    private static volatile Server server;

    public static Server getServer() {
        if (server == null) {
            throw new IllegalArgumentException(
                    "ServerContext is initialized in PulsarClient#start(). Please use it before using ServerContext.");
        }

        return server;
    }

    public static Blueprint getBlueprint() {
        return getServer().getBlueprint();
    }

    public static String getDiscriminator() {
        return getServer().getDiscriminator();
    }

    public static ServerStatus getStatus() {
        return getServer().getStatus();
    }

    public static ServerReport getReport() {
        return getServer().getReport();
    }
}
