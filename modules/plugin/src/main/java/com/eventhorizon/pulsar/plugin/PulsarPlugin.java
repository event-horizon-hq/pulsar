package com.eventhorizon.pulsar.plugin;

import com.eventhorizon.pulsar.client.PulsarClient;
import com.eventhorizon.pulsar.plugin.config.PulsarConfig;
import com.eventhorizon.pulsar.plugin.task.ServerReportTask;
import com.eventhorizon.pulsar.plugin.util.Stopwatch;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.jetbrains.annotations.NotNull;

import static com.hypixel.hytale.server.core.ShutdownReason.SHUTDOWN;

public final class PulsarPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private PulsarConfig pulsarConfig;

    public PulsarPlugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        final var config = this.withConfig("Pulsar", PulsarConfig.CODEC);
        pulsarConfig = config.get();

        new PulsarClient(
                pulsarConfig.getRedisURI(),
                pulsarConfig.getSingularityUrl(),
                pulsarConfig.isDebugMode());
    }

    @Override
    protected void start() {
        final var stopwatch = Stopwatch.startNew();
        final var started = PulsarClient.getInstance().start();
        if (!started) {
            LOGGER.atSevere().log("Failed to start Pulsar Client, shutting down the server.");
            stopwatch.stop();

            HytaleServer.get().shutdownServer(SHUTDOWN.withMessage("Pulsar Client error!"));
            return;
        }

        ServerReportTask.start();

        stopwatch.stop();
        if (pulsarConfig.isDebugMode()) {
            LOGGER.atInfo()
                    .log("Took %dms to start Pulsar Client.", stopwatch.elapsedMillis());
        }
    }
}
