package com.eventhorizon.pulsar.client.context

import com.eventhorizon.pulsar.core.server.Server

object ServerContext {
    lateinit var server: Server
        internal set

    val blueprint get() = server.blueprint
    val discriminator get() = server.discriminator
    val status get() = server.status
    val report get() = server.report
}