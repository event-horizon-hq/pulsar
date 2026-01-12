package com.eventhorizon.pulsar.client

import com.eventhorizon.pulsar.client.api.BlueprintApiClient
import com.eventhorizon.pulsar.client.api.ServerApiClient
import com.eventhorizon.pulsar.client.context.ServerContext
import com.eventhorizon.pulsar.client.packet.PacketClient
import com.eventhorizon.pulsar.client.packet.processor.scope.PulsarNetworkScope
import com.eventhorizon.pulsar.core.server.Server
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class PulsarClient(
    endpointUrl: String,
    secretAccessToken: String,

    private val redisURI: String,
    private val maximumPacketSimultaneously: Int = 32,
    private val debugMode: Boolean = false
) {
    internal val httpClient = HttpClient(OkHttp) {
        install(Logging)
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
    }

    val blueprints = BlueprintApiClient(httpClient, endpointUrl, secretAccessToken)
    val servers = ServerApiClient(httpClient, endpointUrl, secretAccessToken)

    lateinit var packets: PacketClient
        private set

    fun start() {
        val serverId = requireServerId()
        val server = runBlocking { requireServer(serverId) }

        ServerContext.server = server

        packets = PacketClient(
            redisURI,
            maximumPacketSimultaneously,
            debugMode
        )

        packets.start()
    }


    fun stop() {
        PulsarNetworkScope.launch {
            servers.delete(ServerContext.discriminator)
        }

        PulsarNetworkScope.cancel("Client shutdown.")
        httpClient.close()
    }

    private fun requireServerId(): String {
        return System.getenv("SERVER_ID")
            ?: System.getProperty("SERVER_ID")
            ?: error("SERVER_ID is not defined. This process must be started by Singularity.")
    }

    private suspend fun requireServer(serverId: String): Server =
        servers.get(serverId)
            ?: error("Server '$serverId' not found in Singularity.")

}