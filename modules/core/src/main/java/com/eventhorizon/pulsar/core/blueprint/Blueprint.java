package com.eventhorizon.pulsar.core.blueprint;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record Blueprint(
        String id,
        String name,
        @JsonProperty("blueprint_type") BlueprintType type,
        List<Volume> volumes,
        Map<String, String> environment
) {}
