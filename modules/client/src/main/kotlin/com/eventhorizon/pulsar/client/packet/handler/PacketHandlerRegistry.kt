package com.eventhorizon.pulsar.client.packet.handler

import com.eventhorizon.pulsar.core.packet.Packet
import com.eventhorizon.pulsar.core.packet.handler.PacketHandler

@Suppress("UNCHECKED_CAST")
object PacketHandlerRegistry {
    val registeredHandlers = mutableMapOf<String, MutableList<PacketHandler<Packet>>>()

    inline fun <reified T : Packet> register(handler: PacketHandler<T>) {
        val qualifiedName = T::class.qualifiedName
            ?: error("Can't register packet handler because qualified class name is null.")

        val packetHandlers = registeredHandlers.getOrPut(qualifiedName) {
            mutableListOf()
        } as MutableList<PacketHandler<T>>

        packetHandlers.add(handler)
        println("[PULSAR] A new packet handler registered to packet: $qualifiedName")
    }

    inline fun <reified T : Packet> unregister(handler: PacketHandler<T>) {
        val qualifiedName = T::class.qualifiedName
            ?: error("Can't unregister packet handler because qualified class name is null.")

        val packetHandlers = registeredHandlers[qualifiedName]
                as MutableList<PacketHandler<T>>? ?: return

        packetHandlers.remove(handler)

        println("[PULSAR] An packet handler unregistered to packet: $qualifiedName")
    }

    fun getAll(packetName: String) : List<PacketHandler<*>> {
        return registeredHandlers[packetName]?.toList() ?: emptyList()
    }
}