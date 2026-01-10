package com.eventhorizon.pulsar.client

import com.eventhorizon.pulsar.client.api.BlueprintApiClient
import com.eventhorizon.pulsar.client.api.ServerApiClient
import com.eventhorizon.pulsar.client.context.ServerContext
import com.eventhorizon.pulsar.client.packet.PacketClient
import com.eventhorizon.pulsar.client.packet.processor.scope.PulsarNetworkScope
import com.eventhorizon.pulsar.core.server.Server
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking

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
            json()
        }
    }

    val blueprints = BlueprintApiClient(httpClient, endpointUrl, secretAccessToken)
    val servers = ServerApiClient(httpClient, endpointUrl, secretAccessToken)

    var packets: PacketClient = error("The client has not started yet. Please, use PulsarClient#start to start.")
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
        PulsarNetworkScope.cancel("Client shutdown.")
    }

    private fun requireServerId(): String =
        System.getenv("SERVER_ID")
            ?: error("SERVER_ID is not defined. This process must be started by Singularity.")

    private suspend fun requireServer(serverId: String): Server =
        servers.get(serverId)
            ?: error("Server '$serverId' not found in Singularity.")

}