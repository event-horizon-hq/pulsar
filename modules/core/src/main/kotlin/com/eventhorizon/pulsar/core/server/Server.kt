package com.eventhorizon.pulsar.core.server

import com.eventhorizon.pulsar.core.blueprint.Blueprint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Server(
    @SerialName("blueprint") val blueprint: Blueprint,
    @SerialName("discriminator") val discriminator: String,
    @SerialName("port") val port: Int,
    @SerialName("metrics_port") val metricsPort: Int?,
    @SerialName("status") val status: Status,
    @SerialName("report") val report: Report?
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
    @SerialName("online_count") val onlineCount: Int,
    @SerialName("online_since") val onlineSince: Long,
    @SerialName("memory_usage") val memoryUsage: Long,
    @SerialName("total_memory") val totalMemory: Long,
    @SerialName("cpu_usage") val cpuUsage: Long
)
