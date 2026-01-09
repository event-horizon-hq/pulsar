package com.eventhorizon.pulsar.client.api

import com.eventhorizon.pulsar.core.blueprint.Blueprint
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * API client for interacting with blueprint endpoints in Pulsar.
 *
 * @property httpClient Configured Ktor [HttpClient] for making requests.
 * @property baseUrl Base URL of the Pulsar API.
 * @property secretAccessToken Bearer token used for authentication.
 */
class BlueprintApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val secretAccessToken: String
) {

    /**
     * Retrieves a list of all blueprints.
     *
     * @return List of [Blueprint] objects from the `/v1/blueprints` endpoint.
     * @throws io.ktor.client.plugins.ClientRequestException for 4xx responses.
     * @throws io.ktor.client.plugins.ServerResponseException for 5xx responses.
     */
    suspend fun list(): List<Blueprint> {
        return httpClient.get("$baseUrl/v1/blueprints") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretAccessToken")
            }
        }.body()
    }

    /**
     * Retrieves a specific blueprint by its ID.
     *
     * @param id The blueprint ID to fetch.
     * @return The [Blueprint] object corresponding to the provided ID.
     */
    suspend fun get(id: String): Blueprint {
        return httpClient.get("$baseUrl/v1/blueprints/$id") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretAccessToken")
            }
        }.body()
    }
}
