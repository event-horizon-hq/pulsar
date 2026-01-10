package com.eventhorizon.pulsar.client.packet.handler

import com.eventhorizon.pulsar.core.packet.Packet
import com.eventhorizon.pulsar.core.packet.handler.PacketHandler
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object PacketHandlerRegistry {
    private val registeredHandlers = mutableMapOf<String, MutableList<PacketHandler<out Packet>>>()

    inline fun <reified T : Packet> register(handler: PacketHandler<T>) {
        register(T::class, handler)
    }

    inline fun <reified T : Packet> unregister(handler: PacketHandler<T>) {
        unregister(T::class, handler)
    }

    fun <T : Packet> register(packetClass: KClass<out Packet>, handler: PacketHandler<T>) {
        val qualifiedName = packetClass.qualifiedName
            ?: error("Can't register packet handler because qualified class name is null.")

        val packetHandlers = registeredHandlers.computeIfAbsent(qualifiedName) {
            mutableListOf()
        }

        packetHandlers.add(handler)
        println("[PULSAR] A new packet handler registered to packet: $qualifiedName")
    }

    fun <T : Packet> unregister(packetClass: KClass<out Packet>, handler: PacketHandler<T>) {
        val qualifiedName = packetClass.qualifiedName
            ?: error("Can't unregister packet handler because qualified class name is null.")

        val packetHandlers = registeredHandlers[qualifiedName]
                as MutableList<PacketHandler<T>>? ?: return

        packetHandlers.remove(handler)

        println("[PULSAR] An packet handler unregistered to packet: $qualifiedName")
    }

    fun getAll(packetName: String): List<PacketHandler<out Packet>> {
        return registeredHandlers[packetName]?.toList() ?: emptyList()
    }
}