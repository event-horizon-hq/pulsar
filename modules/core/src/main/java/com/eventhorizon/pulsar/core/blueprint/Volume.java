package com.eventhorizon.pulsar.core.blueprint;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Volume(
        String id,
        @JsonProperty("target_folder") String targetFolder
) {}
