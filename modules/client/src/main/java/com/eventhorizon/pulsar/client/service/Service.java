package com.eventhorizon.pulsar.client.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Service {

    protected final String baseUrl, accessToken;
    protected final OkHttpClient httpClient;
    protected final ObjectMapper mapper;

    protected static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

    protected <T> CompletableFuture<T> executeAsync(Request request, Class<T> type) {
        final var future = new CompletableFuture<T>();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    if (!response.isSuccessful()) {
                        final var err = response.body() != ResponseBody.EMPTY ? response.body().string() : "<empty>";
                        future.completeExceptionally(new IOException("HTTP " + response.code() + ": " + err));
                        return;
                    }

                    if (response.body() == ResponseBody.EMPTY) {
                        future.completeExceptionally(new IOException("Empty body"));
                        return;
                    }

                    final var value = mapper.readValue(response.body().string(), type);
                    future.complete(value);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    }

    protected <T> CompletableFuture<T> executeAsync(Request request, TypeReference<T> type) {
        final var future = new CompletableFuture<T>();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    if (!response.isSuccessful()) {
                        final var err = response.body() != ResponseBody.EMPTY ? response.body().string() : "<empty>";
                        future.completeExceptionally(
                                new IOException("HTTP " + response.code() + ": " + err)
                        );
                        return;
                    }

                    if (response.body() == ResponseBody.EMPTY) {
                        future.completeExceptionally(new IOException("Empty body"));
                        return;
                    }

                    final var value = mapper.readValue(response.body().string(), type);
                    future.complete(value);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    }


    protected <T> T execute(Request request, Class<T> type) throws IOException {
        try (final var response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP " + response.code());
            }

            return mapper.readValue(response.body().string(), type);
        }
    }

    protected <T> T execute(Request request, TypeReference<T> type) throws IOException {
        try (final var response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP " + response.code());
            }

            return mapper.readValue(response.body().string(), type);
        }
    }

    protected Request.Builder request(@NotNull String url) {
        return new Request.Builder()
                .url(baseUrl + "/v1" + url)
                .addHeader("Authorization", "Bearer " + accessToken);
    }
}
