package com.eventhorizon.pulsar.core.blueprint

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(
    val id: String,
    val name: String,
    @SerialName("blueprint_type") val type: BlueprintType,
    val volumes: List<Volume>,
    val environment: Map<String, String>
)

@Serializable
enum class BlueprintType {
    STANDALONE,
    HYTALE
}

@Serializable
data class Volume(
    val id: String,
    @SerialName("target_folder") val targetFolder: String
)