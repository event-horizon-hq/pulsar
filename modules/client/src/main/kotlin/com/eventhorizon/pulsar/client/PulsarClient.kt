package com.eventhorizon.pulsar.client

import com.eventhorizon.pulsar.client.api.BlueprintApiClient
import com.eventhorizon.pulsar.client.api.ServerApiClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

class PulsarClient(
    private val endpointUrl: String,
    private val secretAccessToken: String
) {
    internal val httpClient = HttpClient(OkHttp) {
        install(Logging)
        install(ContentNegotiation) {
            json()
        }
    }

    val blueprint = BlueprintApiClient(httpClient, endpointUrl, secretAccessToken)
    val server = ServerApiClient(httpClient, endpointUrl, secretAccessToken)
}