package com.eventhorizon.pulsar.client.service.impl;

import com.eventhorizon.pulsar.client.context.ServerContext;
import com.eventhorizon.pulsar.client.service.Service;
import com.eventhorizon.pulsar.core.server.Server;
import com.eventhorizon.pulsar.core.server.ServerReport;
import com.eventhorizon.pulsar.core.server.ServerStatus;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Map.*;

public final class ServerService extends Service {

    public ServerService(@NotNull String baseUrl, String accessToken, OkHttpClient httpClient, ObjectMapper mapper) {
        super(baseUrl, accessToken, httpClient, mapper);
    }

    public @NotNull List<Server> list() throws IOException {
        final var request = request("/servers")
                .get()
                .build();

        return execute(request, new TypeReference<>() {});
    }

    public @Nullable Server get(@NotNull String serverId) throws IOException {
        final var request = request("/servers/" + serverId)
                .get()
                .build();

        return execute(request, Server.class);
    }

    public @Nullable Server create(@NotNull String blueprintId) throws IOException {
        final var httpUrl = HttpUrl.parse(baseUrl + "/servers")
                .newBuilder()
                .addQueryParameter("blueprintId", blueprintId)
                .build();

        final var request = request(httpUrl.toString())
                .post(RequestBody.EMPTY)
                .build();

        return execute(request, Server.class);
    }

    public @Nullable Server delete(@NotNull String id) throws IOException {
        final var request = request(baseUrl + "/servers/" + id)
                .delete()
                .build();

        return execute(request, Server.class);
    }

    public @NotNull Server updateReport(@NotNull String id, @NotNull ServerReport report) throws IOException {
        final var body = mapper.writeValueAsString(report);

        final var request = request(baseUrl + "/servers/" + id + "/report")
                .patch(RequestBody.create(body, JSON_MEDIA_TYPE))
                .addHeader("Content-Type", "application/json")
                .build();

        if (ServerContext.getDiscriminator().equals(id)) {
            ServerContext.getServer().setReport(report);
        }

        return execute(request, Server.class);
    }

    public @NotNull Server updateStatus(@NotNull String id, @NotNull ServerStatus status) throws IOException {
        final var body = mapper.writeValueAsString(of("status", status.name()));
        final var request = request(baseUrl + "/servers/" + id + "/status")
                .patch(RequestBody.create(body, JSON_MEDIA_TYPE))
                .addHeader("Content-Type", "application/json")
                .build();
        
        if (ServerContext.getDiscriminator().equals(id)) {
            ServerContext.getServer().setStatus(status);
        }

        return execute(request, Server.class);
    }

    public void restart(@NotNull String id) throws IOException {
        final var request = request(baseUrl + "/servers/" + id + "/restart")
                .post(RequestBody.create(new byte[0], null))
                .build();

        try (final var response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP " + response.code());
            }
        }
    }

    public CompletableFuture<List<Server>> listAsync() {
        final var request = request("/servers")
                .get()
                .build();

        return executeAsync(request, new TypeReference<>() {});
    }

    public CompletableFuture<Server> getAsync(@NotNull String serverId) {
        final var request = request("/servers/" + serverId)
                .get()
                .build();

        return executeAsync(request, Server.class);
    }

    public CompletableFuture<Server> createAsync(@NotNull String blueprintId) {
        final var httpUrl = HttpUrl.parse(baseUrl + "/v1/servers")
                .newBuilder()
                .addQueryParameter("blueprintId", blueprintId)
                .build();

        final var request = new Request.Builder()
                .url(httpUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(RequestBody.EMPTY)
                .build();

        return executeAsync(request, Server.class);
    }

    public CompletableFuture<Server> deleteAsync(@NotNull String id) {
        final var request = request("/servers/" + id)
                .delete()
                .build();

        return executeAsync(request, Server.class);
    }

    public CompletableFuture<Server> updateReportAsync(@NotNull String id, @NotNull ServerReport report) {
        final var body = mapper.writeValueAsString(report);

        final var request = request("/servers/" + id + "/report")
                .patch(RequestBody.create(body, JSON_MEDIA_TYPE))
                .addHeader("Content-Type", "application/json")
                .build();

        return executeAsync(request, Server.class);
    }

    public CompletableFuture<Server> updateStatusAsync(@NotNull String id, @NotNull ServerStatus status) {
        final var body = mapper.writeValueAsString(of("status", status.name()));

        final var request = request("/servers/" + id + "/status")
                .patch(RequestBody.create(body, JSON_MEDIA_TYPE))
                .addHeader("Content-Type", "application/json")
                .build();

        return executeAsync(request, Server.class);
    }

    public CompletableFuture<Void> restartAsync(@NotNull String id) {
        final var request = request("/servers/" + id + "/restart")
                .post(RequestBody.EMPTY)
                .build();

        var future = new CompletableFuture<Void>();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    if (!response.isSuccessful()) {
                        future.completeExceptionally(
                                new IOException("HTTP " + response.code())
                        );
                        return;
                    }
                    future.complete(null);
                }
            }
        });

        return future;
    }
}
