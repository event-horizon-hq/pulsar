package com.eventhorizon.pulsar.client.packet

import com.eventhorizon.pulsar.client.context.ServerContext
import com.eventhorizon.pulsar.client.packet.processor.BinaryPacketProcessor
import com.eventhorizon.pulsar.client.packet.processor.RedisBinaryPacketProcessor
import com.eventhorizon.pulsar.client.packet.processor.scope.PulsarNetworkScope
import com.eventhorizon.pulsar.core.blueprint.Blueprint
import com.eventhorizon.pulsar.core.packet.Packet
import com.eventhorizon.pulsar.core.packet.PacketMetadata
import com.eventhorizon.pulsar.core.packet.PacketTargetKind
import com.eventhorizon.pulsar.core.server.Server
import kotlinx.coroutines.launch

/**
 * Public facade over the Pulsar packet transport layer.
 *
 * `PacketService` owns a single [BinaryPacketProcessor] instance and exposes a
 * minimal, stable API for packet emission. It is responsible for:
 *
 * - Bootstrapping the listening loop
 * - Building [PacketMetadata] for each send operation
 * - Routing packets by semantic target (DIRECT, BLUEPRINT, ALL)
 *
 * This class does not perform serialization, I/O, concurrency control, or
 * dispatching. Those concerns are delegated entirely to the underlying
 * [BinaryPacketProcessor].
 *
 * From the caller’s perspective, this is the only surface required to interact
 * with the Pulsar network layer.
 */

class PacketClient(
    redisURI: String,
    maximumPacketSimultaneously: Int = 32,
    debugMode: Boolean = false
) {

    private val packetProcessor: BinaryPacketProcessor = RedisBinaryPacketProcessor(
        redisURI,
        maximumPacketSimultaneously,
        debugMode
    )

    internal fun start() {
        PulsarNetworkScope.launch {
            packetProcessor.startListening()
        }
    }

    /**
     * Sends a packet directly to a concrete server instance.
     *
     * This is a convenience overload that derives the discriminator from the
     * provided [Server] object and delegates to the string-based variant.
     *
     * @param server The target server instance.
     * @param packet The packet to be sent.
     */
    fun publishToTarget(server: Server, packet: Packet) {
        this.publishToTarget(server.discriminator, packet)
    }

    /**
     * Sends a packet to all servers associated with a specific blueprint.
     *
     * This is a convenience overload that derives the blueprint identifier from
     * the provided [Blueprint] object and delegates to the string-based variant.
     *
     * @param blueprint The target blueprint.
     * @param packet The packet to be sent.
     */
    fun publishToBlueprint(blueprint: Blueprint, packet: Packet) {
        this.publishToBlueprint(blueprint.id, packet)
    }


    /**
     * Sends a packet directly to a single server.
     *
     * @param targetDiscriminator The unique discriminator of the target server.
     * @param packet The packet instance to send.
     */
    fun publishToTarget(targetDiscriminator: String, packet: Packet) {
        val packetMetadata = PacketMetadata(
            ServerContext.discriminator,
            PacketTargetKind.DIRECT,
            targetDiscriminator
        )

        PulsarNetworkScope.launch {
            packetProcessor.publish(packetMetadata, packet)
        }
    }

    /**
     * Sends a packet to all servers belonging to a specific blueprint.
     *
     * @param blueprintId The blueprint identifier.
     * @param packet The packet instance to send.
     */
    fun publishToBlueprint(blueprintId: String, packet: Packet) {
        val packetMetadata = PacketMetadata(
            ServerContext.discriminator,
            PacketTargetKind.BLUEPRINT,
            blueprintId
        )

        PulsarNetworkScope.launch {
            packetProcessor.publish(packetMetadata, packet)
        }
    }

    /**
     * Broadcasts a packet to every server in the network.
     *
     * The packet will be delivered to all listeners except the sender.
     *
     * @param packet The packet instance to broadcast.
     */
    fun broadcast(packet: Packet) {
        val packetMetadata = PacketMetadata(
            ServerContext.discriminator,
            PacketTargetKind.ALL,
            "ALL"
        )

        PulsarNetworkScope.launch {
            packetProcessor.publish(packetMetadata, packet)
        }
    }
}