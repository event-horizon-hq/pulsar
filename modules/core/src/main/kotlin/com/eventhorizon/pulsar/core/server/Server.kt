package com.eventhorizon.pulsar.core.server

import com.eventhorizon.pulsar.core.blueprint.Blueprint
import kotlinx.serialization.Serializable

@Serializable
data class Server(
    val blueprint: Blueprint,
    val discriminator: String,
    val port: Int,
    val status: Status,
    val report: Report?
)

@Serializable
enum class Status {
    INACTIVE,
    ERROR,
    CREATING,
    ACTIVE,
    RESTARTING
}

@Serializable
data class Report(
    val onlineCount: Int,
    val onlineSince: Long,
    val memoryUsage: Long,
    val totalMemory: Long,
    val cpuUsage: Long
)