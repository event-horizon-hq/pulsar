package com.eventhorizon.pulsar.client.packet;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;

import java.util.concurrent.*;

@UtilityClass
public final class PulsarNetworkThread {
    public static final int MACHINE_CORES = Runtime.getRuntime().availableProcessors() * 4;

    public static final int MAXIMUM_PACKETS_SIMULTANEOUSLY = 32;
    private static final int MAX_IN_FLIGHT = 64;

    public static final ExecutorService LISTENER = new ThreadPoolExecutor(
            MACHINE_CORES,
            MACHINE_CORES,
            0, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(MAXIMUM_PACKETS_SIMULTANEOUSLY),
            new ThreadFactoryBuilder()
                    .setNameFormat("pulsar-listener-%d")
                    .setDaemon(true)
                    .build());


    public static final ExecutorService EMITTER =
            new ThreadPoolExecutor(
                    MACHINE_CORES,
                    MACHINE_CORES,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(MAX_IN_FLIGHT),
                    new ThreadFactoryBuilder()
                            .setNameFormat("pulsar-emitter-%d")
                            .setDaemon(true)
                            .build(),
                    new ThreadPoolExecutor.AbortPolicy()
            );
}
