package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable


@Serializable
data class Hike(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val layers: List<Layer> = emptyList(),
    val isComplete: Boolean = false,
    val createdBy: String = "",
    val invitedFriends: List<String> = emptyList()
)
