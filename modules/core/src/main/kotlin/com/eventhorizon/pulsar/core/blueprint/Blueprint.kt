package com.eventhorizon.pulsar.core.blueprint

import kotlinx.serialization.Serializable

@Serializable
data class Blueprint(
    val id: String,
    val name: String,
    val type: BlueprintType,
    val volume: List<Volume>,
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
    val targetFolder: String
)