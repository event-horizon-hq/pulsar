package com.eventhorizon.pulsar.client.context

import com.eventhorizon.pulsar.core.server.Server

object ServerContext {
    var server: Server = error("The client has not started yet. Please, use PulsarClient#start to start.")

    val blueprint get() = server.blueprint
    val discriminator get() = server.discriminator
    val status get() = server.status
    val report get() = server.report
}