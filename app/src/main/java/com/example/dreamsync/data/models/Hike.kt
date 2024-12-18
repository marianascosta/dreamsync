package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Hike(
    val _id: String = "",
    val name: String = "",
    val description: String = "",
    val layers: Int = 0,
    val isComplete: Boolean = false,
    val createdBy: String = "",
    val invitedFriends: List<String> = emptyList()
)