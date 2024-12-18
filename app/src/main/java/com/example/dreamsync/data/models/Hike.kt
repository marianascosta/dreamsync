package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
class Hike (
    val id: String,
    val name: String,
    val description: String,
    val layers: Int,
    val isComplete: Boolean
) {
    constructor(
        name: String,
        description: String,
        layers: Int,
        isComplete: Boolean
    ) : this(
        id = UUID.randomUUID().toString(),
        name = name,
        description = description,
        layers = layers,
        isComplete = isComplete
    )
}