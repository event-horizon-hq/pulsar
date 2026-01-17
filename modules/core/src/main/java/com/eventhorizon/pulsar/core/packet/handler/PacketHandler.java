package com.eventhorizon.pulsar.core.packet.handler;

import com.eventhorizon.pulsar.core.packet.Packet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class PacketHandler<T extends Packet> {

    private final PacketHandlerPriority priority;

    public void onSendPacket(T packet) {}

    public void onReceivePacket(T packet) {}
}
