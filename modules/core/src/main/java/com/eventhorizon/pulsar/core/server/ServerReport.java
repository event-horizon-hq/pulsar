package com.eventhorizon.pulsar.core.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ServerReport(
        @JsonProperty("online_count") int onlineCount,
        @JsonProperty("online_since") long onlineSince,
        @JsonProperty("memory_usage") long memoryUsage,
        @JsonProperty("total_memory") long totalMemory,
        @JsonProperty("cpu_usage") long cpuUsage
) {}