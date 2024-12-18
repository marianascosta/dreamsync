package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
class Hike (
    val id: String,
    val name: String,
    val description: String,
    val numLayers: Int,
    val layers: List<Layer>,
    val isComplete: Boolean
) {
    constructor(
        name: String,
        description: String,
        numLayers: Int,
        layers: List<Layer>,
        isComplete: Boolean
    ) : this(
        id = UUID.randomUUID().toString(),
        name = name,
        description = description,
        numLayers = numLayers,
        layers = emptyList(),
        isComplete = isComplete
    )
}