package com.eventhorizon.pulsar.core.server;

import com.eventhorizon.pulsar.core.blueprint.Blueprint;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public final class Server {
    @JsonProperty("blueprint")
    private final Blueprint blueprint;

    @JsonProperty("discriminator")
    private final String discriminator;

    @JsonProperty("port")
    private final int port;

    @JsonProperty("metrics_port")
    private final Integer metricsPort;

    @JsonProperty("status")
    private ServerStatus status;

    @JsonProperty("report")
    private ServerReport report;
}
