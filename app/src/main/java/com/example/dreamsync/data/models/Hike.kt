package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

enum class HikeStatus {
    COMPLETED,
    IN_PROGRESS,
    WAITING,
    NOT_STARTED
}

@Serializable
data class Hike(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val layers: List<Layer> = emptyList(),
    val isComplete: Boolean = false,
    var createdBy: String = "",
    val invitedFriends: List<String> = emptyList(),
    val participantStatus: List<ParticipantStatusEntry> = emptyList(),
    val status: HikeStatus = HikeStatus.NOT_STARTED
)

@Serializable
data class ParticipantStatusEntry(
    val id: String,  //participant's id
    val participation: HikeParticipation   //model in Profile.kt
)

