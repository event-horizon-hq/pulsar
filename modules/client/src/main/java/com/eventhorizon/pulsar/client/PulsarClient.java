package com.eventhorizon.pulsar.client;

import com.eventhorizon.pulsar.client.context.ServerContext;
import com.eventhorizon.pulsar.client.service.impl.BlueprintService;
import com.eventhorizon.pulsar.client.service.impl.ServerService;
import com.eventhorizon.pulsar.client.packet.PacketDispatcher;
import com.eventhorizon.pulsar.core.server.Server;
import com.eventhorizon.pulsar.core.server.ServerStatus;
import lombok.Getter;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Duration;

@Getter
public final class PulsarClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PulsarClient.class);

    @Getter
    private static PulsarClient instance;

    private final BlueprintService blueprintApiClient;
    private final ServerService serverService;
    private final PacketDispatcher packetDispatcher;

    public PulsarClient(
            @NotNull String baseUrl,
            @NotNull String redisURI
    ) {
        this(baseUrl, redisURI, false);
    }

    public PulsarClient(
            @NotNull String baseUrl,
            @NotNull String redisURI,
            boolean debugMode
    ) {
        if (instance != null) {
            throw new IllegalStateException("PulsarClient is already initialized.");
        }

        final var okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .retryOnConnectionFailure(true)
                .build();

        final var objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        final var singularityAccessToken = System.getenv("SINGULARITY_ACCESS_TOKEN");

        this.blueprintApiClient = new BlueprintService(baseUrl, singularityAccessToken, okHttpClient, objectMapper);
        this.serverService = new ServerService(baseUrl, singularityAccessToken, okHttpClient, objectMapper);
        this.packetDispatcher = new PacketDispatcher(redisURI, debugMode);

        instance = this;
    }

    public boolean start() {
        final var serverId = System.getenv("SERVER_ID") != null ?
                System.getenv("SERVER_ID") :
                System.getProperty("SERVER_ID");

        if (serverId == null) {
            throw new IllegalArgumentException("SERVER_ID is not set. This process must be started by Singularity.");
        }

        try {
            final var server = this.serverService.get(serverId);
            if (server == null) {
                throw new IllegalArgumentException("SERVER_ID is invalid. This process must be started by Singularity.");
            }

            ServerContext.setServer(server);

            this.serverService.updateStatus(ServerContext.getDiscriminator(), ServerStatus.ACTIVE);
            this.packetDispatcher.start();
            return true;
        } catch (IOException e) {
            LOGGER.error("An unexpected error occurred, stopping Pulsar Client.", e);
            return false;
        }
    }

    public void stop() {
        this.packetDispatcher.stop();
    }
}
