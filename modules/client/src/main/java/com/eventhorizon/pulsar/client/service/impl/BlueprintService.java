package com.eventhorizon.pulsar.client.service.impl;

import com.eventhorizon.pulsar.client.service.Service;
import com.eventhorizon.pulsar.core.blueprint.Blueprint;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class BlueprintService extends Service {

    public BlueprintService(String baseUrl, String accessToken, OkHttpClient httpClient, ObjectMapper mapper) {
        super(baseUrl, accessToken, httpClient, mapper);
    }

    public @NotNull List<Blueprint> list() throws IOException {
        final var request = request("/blueprints")
                .get()
                .build();

        return execute(request, new TypeReference<>() {});
    }

    public @Nullable Blueprint get(@NotNull String blueprintId) throws IOException {
        final var request = request("/blueprints/" + blueprintId)
                .get()
                .build();

        return execute(request, Blueprint.class);
    }

    public @NotNull CompletableFuture<List<Blueprint>> listAsync() {
        final var request = request("/blueprints")
                .get()
                .build();

        return executeAsync(request, new TypeReference<>() {});
    }

    public @Nullable CompletableFuture<Blueprint> getAsync(@NotNull String blueprintId) {
        final var request = request("/blueprints/" + blueprintId)
                .get()
                .build();

        return executeAsync(request, Blueprint.class);
    }

}
