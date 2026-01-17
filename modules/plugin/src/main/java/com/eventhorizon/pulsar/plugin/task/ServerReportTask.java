package com.eventhorizon.pulsar.plugin.task;

import com.eventhorizon.pulsar.client.PulsarClient;
import com.eventhorizon.pulsar.client.context.ServerContext;
import com.eventhorizon.pulsar.core.server.ServerReport;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.Universe;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ServerReportTask implements Runnable {

    private static final Spark SPARK = SparkProvider.get();

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors
            .newSingleThreadScheduledExecutor();

    public static void start() {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new ServerReportTask(), 0, 1, TimeUnit.SECONDS);
    }

    private ServerReportTask() {}

    @Override
    public void run() {
        final var cpu = SPARK.cpuProcess().poll(StatisticWindow.CpuUsage.MINUTES_1);
        final var heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        final var maxMemory = heapMemoryUsage.getMax();
        final var usedMemory = heapMemoryUsage.getUsed();

        final var playerCount = Universe.get().getPlayerCount();

        final var serverReport = new ServerReport(
                playerCount,
                HytaleServer.get().getBootStart(),
                usedMemory,
                maxMemory,
                (long) cpu);

        PulsarClient.getInstance()
                .getServerService()
                .updateReportAsync(ServerContext.getDiscriminator(), serverReport)
                .thenAccept(server -> ServerContext.getServer().setReport(serverReport));
    }
}
