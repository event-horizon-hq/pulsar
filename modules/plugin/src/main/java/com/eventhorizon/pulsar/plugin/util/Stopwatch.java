package com.eventhorizon.pulsar.plugin.util;

import lombok.Getter;

@Getter
public final class Stopwatch {
    private long start;
    private long end;
    private boolean running;

    public static Stopwatch startNew() {
        final var stopwatch = new Stopwatch();
        stopwatch.start();

        return stopwatch;
    }

    public void start() {
        if (running) return;

        this.running = true;
        this.start = System.nanoTime();
        this.end = 0;
    }

    public void stop() {
        if (!running) return;

        this.end = System.nanoTime();
        this.running = false;
    }

    public void reset() {
        this.running = false;
        this.start = 0;
        this.end = 0;
    }

    public long elapsedNanos() {
        return this.running
                ? System.nanoTime() - this.start
                : this.end - this.start;
    }

    public long elapsedMillis() {
        return elapsedNanos() / 1_000_000;
    }

    public double elapsedSeconds() {
        return elapsedNanos() / 1_000_000_000.0;
    }
}
