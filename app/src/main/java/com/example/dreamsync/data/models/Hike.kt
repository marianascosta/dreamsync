package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

enum class HikeStatus {
    COMPLETED,
    IN_PROGRESS,
    NOT_STARTED,
    WAITING
}

@Serializable
data class Hike(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val layers: List<Layer> = emptyList(),
    val isComplete: Boolean = false,
    val createdBy: String = "",
    val invitedFriends: List<String> = emptyList(),
    val status : HikeStatus = HikeStatus.NOT_STARTED
)
