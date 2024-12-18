package com.example.dreamsync.data.models

import com.google.firebase.database.PropertyName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


@Serializable
data class Hike(
    val _id: String = "",
    val name: String = "",
    val description: String = "",
    val layers: List<Layer> = emptyList(),
    val isComplete: Boolean = false,
    val createdBy: String = "",
    val invitedFriends: List<String> = emptyList()
)
@Serializable
data class Layer(
    val name: String = "",
    val description: String = "",
    val difficulty: String = "",
    val startDate: String = "",
    val kickDate: String = "",
)