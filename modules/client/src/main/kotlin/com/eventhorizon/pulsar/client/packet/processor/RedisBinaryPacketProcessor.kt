package com.eventhorizon.pulsar.client.packet.processor

import com.eventhorizon.pulsar.client.context.ServerContext
import com.eventhorizon.pulsar.client.factory.RedisConnectionFactory
import com.eventhorizon.pulsar.client.packet.handler.PacketHandlerRegistry
import com.eventhorizon.pulsar.client.packet.processor.scope.PulsarNetworkScope
import com.eventhorizon.pulsar.core.packet.Packet
import com.eventhorizon.pulsar.core.packet.PacketMetadata
import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferInput
import com.eventhorizon.pulsar.core.packet.buffer.PacketByteBufferOutput
import com.eventhorizon.pulsar.core.packet.handler.PacketHandler
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.lang.reflect.Constructor
import kotlin.time.Duration.Companion.seconds

/**
 * Binary packet processor using Redis as the transport layer.
 *
 * This class implements [BinaryPacketProcessor] and handles:
 * - Subscribing to Redis channels for incoming packets.
 * - Publishing packets to the network.
 * - Concurrent processing with backpressure control via [Semaphore].
 * - Dynamic packet instantiation via reflection with caching.
 * - Dispatching packets to registered handlers.
 *
 * It is designed to be used inside a single [CoroutineScope] (e.g., [PulsarNetworkScope])
 * and cooperates with cancellation signals to stop listening safely.
 *
 * @property redisURI The Redis server URI used for publisher and subscriber connections.
 * @property maximumPacketSimultaneously Maximum number of packets to process concurrently.
 * @property debugMode Enables debug logs for packet handling and failures.
 */
class RedisBinaryPacketProcessor(
    redisURI: String,
    maximumPacketSimultaneously: Int = 32,
    private val debugMode: Boolean = false
) : BinaryPacketProcessor() {

    private val packetConstructors = Caffeine.newBuilder()
        .maximumSize(1024)
        .build<String, Constructor<out Packet>>()

    private val publisherJedis = RedisConnectionFactory.create(redisURI)
    private val subscriberJedis = RedisConnectionFactory.create(redisURI)

    private val channelByteArray = "pulsar:network:packets".toByteArray()

    private val semaphore = Semaphore(maximumPacketSimultaneously)

    /**
     * Starts listening to the Redis channel for incoming packets.
     *
     * This is a long-running suspending function that:
     * - Runs in a loop while the current coroutine context is active.
     * - Subscribes to the Redis channel using [subscriberJedis].
     * - Implements basic reconnection with a 1-second delay on failure.
     *
     * The listening loop respects cancellation: if the [CoroutineScope] is cancelled,
     * the loop terminates and the subscriber is closed automatically.
     */
    override suspend fun startListening() {
        while (currentCoroutineContext().isActive) {
            runCatching {
                subscriberJedis.resource.use {
                    it.subscribe(this@RedisBinaryPacketProcessor, channelByteArray)
                }
            }.onFailure {
                if (it is CancellationException) throw it

                delay(1.seconds)
            }
        }
    }

    /**
     * Publishes a packet to the Redis network.
     *
     * @param metadata Metadata associated with the packet, including target and sender.
     * @param packet The packet instance to serialize and send.
     *
     * Throws an exception if [packet::class.qualifiedName] is null.
     */
    override suspend fun publish(
        metadata: PacketMetadata,
        packet: Packet
    ) {
        val packetName = packet::class.qualifiedName
            ?: error("Can't publish packet because qualified class name is null.")

        val packetByteBufferOutput = PacketByteBufferOutput()

        packetByteBufferOutput.writeString(packetName)
        packet.writeMetadata(packetByteBufferOutput, metadata)
        packet.write(packetByteBufferOutput)

        publisherJedis.resource.use {
            it.publish(channelByteArray, packetByteBufferOutput.toByteArray())
        }
    }

    /**
     * Handles incoming Redis messages.
     *
     * This method is called by the Redis subscription client. Each message is:
     * - Launched in a coroutine on [PulsarNetworkScope].
     * - Processed through [semaphore] to limit concurrent packet handling.
     * - Exceptions in packet processing are caught and logged if [debugMode] is enabled.
     *
     * @param channel The channel from which the message was received.
     * @param message The raw byte array representing the serialized packet.
     */
    override fun onMessage(channel: ByteArray, message: ByteArray) {
        if (!channel.contentEquals(channelByteArray)) {
            return
        }

        PulsarNetworkScope.launch {
            semaphore.withPermit {
                runCatching {
                    processPacket(message)
                }.onFailure {
                    if (debugMode) {
                        it.printStackTrace()
                    }
                }
            }
        }
    }

    /**
     * Processes a single packet message.
     *
     * Steps:
     * 1. Deserialize the packet using [PacketByteBufferInput].
     * 2. Validate that the packet target matches [ServerContext.server].
     * 3. Dispatch the packet to all registered handlers in [PacketHandlerRegistry].
     *
     * Exceptions thrown by handlers are caught per-handler to avoid cancelling other handlers.
     *
     * @param message Serialized packet bytes.
     */
    private fun processPacket(message: ByteArray) {
        val packetByteBufferInput = PacketByteBufferInput(message)
        val packet = this.resolvePacket(packetByteBufferInput)

        packet.readMetadata(packetByteBufferInput)
        if (!packet.metadata.isValidTarget(ServerContext.server)) {
            return
        }

        packet.read(packetByteBufferInput)

        val packetName = packet::class.qualifiedName ?: return

        PacketHandlerRegistry.getAll(packetName).forEach { handler ->

            @Suppress("UNCHECKED_CAST")
            (handler as? PacketHandler<Packet>)?.onReceivePacket(packet)
        }
    }

    /**
     * Resolves a packet class from its qualified name and creates a new instance.
     *
     * - Uses [packetConstructors] cache to avoid repeated reflection lookups.
     * - Throws [ClassNotFoundException] or [NoSuchMethodException] if the packet class
     *   cannot be found or has no no-arg constructor.
     *
     * @param packetByteBufferInput The input buffer containing the serialized packet.
     * @return A new instance of the deserialized [Packet].
     */
    private fun resolvePacket(packetByteBufferInput: PacketByteBufferInput): Packet {
        val className = packetByteBufferInput.readString()
        val constructor = packetConstructors.getIfPresent(className)
            ?: run {
                val clazz = Class.forName(className).asSubclass(Packet::class.java)
                val constructor = clazz.getDeclaredConstructor()

                packetConstructors.put(className, constructor)
                constructor
            }

        return constructor.newInstance()
    }
}
