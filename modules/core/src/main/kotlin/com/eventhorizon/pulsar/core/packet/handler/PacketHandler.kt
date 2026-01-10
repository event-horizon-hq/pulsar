package com.eventhorizon.pulsar.core.packet.handler

import com.eventhorizon.pulsar.core.packet.Packet

enum class PacketHandlerPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST;
}

abstract class PacketHandler<T : Packet> {
    open val priority: PacketHandlerPriority = PacketHandlerPriority.NORMAL

    open fun onSendPacket(packet: T) {}
    open fun onReceivePacket(packet: T) {}
}
