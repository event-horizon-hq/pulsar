package com.eventhorizon.pulsar.client.test

import com.eventhorizon.pulsar.core.packet.handler.PacketHandler

class TestPacketHandler : PacketHandler<TestPacket>() {

    override fun onReceivePacket(packet: TestPacket) {
        println("[RECEIVE] Test packet: ${packet.uuid}")
    }

    override fun onSendPacket(packet: TestPacket) {
        println("[SEND] Test packet: ${packet.uuid}")
    }
}