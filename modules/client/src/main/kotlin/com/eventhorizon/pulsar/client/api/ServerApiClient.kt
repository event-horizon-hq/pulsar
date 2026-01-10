package com.eventhorizon.pulsar.client.api

import com.eventhorizon.pulsar.core.server.Report
import com.eventhorizon.pulsar.core.server.Server
import com.eventhorizon.pulsar.core.server.Status
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * API client for interacting with server endpoints in Pulsar.
 *
 * @property httpClient Configured Ktor [HttpClient] for making requests.
 * @property baseUrl Base URL of the Pulsar API.
 * @property secretAccessToken Bearer token used for authentication.
 */
class ServerApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val secretAccessToken: String
) {

    /**
     * Retrieves a list of all servers.
     *
     * @return List of [Server] objects from the `/v1/servers` endpoint.
     * @throws io.ktor.client.plugins.ClientRequestException for 4xx responses.
     * @throws io.ktor.client.plugins.ServerResponseException for 5xx responses.
     */
    suspend fun list(): List<Server> {
        return httpClient.get("$baseUrl/v1/servers") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretAccessToken")
            }
        }.body()
    }

    /**
     * Retrieves a specific server by its ID.
     *
     * @param id The server ID to fetch.
     * @return The [Server] object corresponding to the provided ID.
     */
    suspend fun get(id: String): Server? {
        return httpClient.get("$baseUrl/v1/servers/$id") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretAccessToken")
            }
        }.body()
    }

    /**
     * Creates a new server based on a blueprint ID.
     *
     * @param blueprintId The ID of the blueprint to use for creating the server.
     * @return The created [Server] object.
     */
    suspend fun create(blueprintId: String): Server {
        return httpClient.post("$baseUrl/v1/servers") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretAccessToken")
            }
            parameter("blueprintId", blueprintId)
        }.body()
    }

    /**
     * Deletes a server by its ID.
     *
     * @param id The ID of the server to delete.
     * @return The deleted [Server] object.
     */
    suspend fun delete(id: String): Server {
        return httpClient.delete("$baseUrl/v1/servers/$id") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretAccessToken")
            }
        }.body()
    }

    /**
     * Updates the report of a server.
     *
     * @param id The ID of the server to update.
     * @param report The [Report] object containing the new data.
     * @return The updated [Server] object.
     */
    suspend fun update(id: String, report: Report): Server {
        return httpClient.patch("$baseUrl/v1/servers/$id/report") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretAccessToken")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            setBody(report)
        }.body()
    }

    /**
     * Updates the status of a server.
     *
     * @param id The ID of the server to update.
     * @param status The new [Status] to set.
     * @return The updated [Server] object.
     */
    suspend fun update(id: String, status: Status): Server {
        return httpClient.patch("$baseUrl/v1/servers/$id/status") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretAccessToken")
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            setBody(mapOf("status" to status.name))
        }.body()
    }

    /**
     * Restarts a server by its ID.
     *
     * @param id The ID of the server to restart.
     */
    suspend fun restart(id: String) {
        httpClient.post("$baseUrl/v1/servers/$id/restart") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretAccessToken")
            }
        }
    }
}
