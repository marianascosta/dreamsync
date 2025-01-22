package com.example.dreamsync.data.models

import com.example.dreamsync.screens.internal.hikes.insideHike.HikeStage
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
    val participantStatus: List<ParticipantStatusEntry> = emptyList(),
    val stage: HikeStage = HikeStage.WAITING_FOR_OTHERS,
    val status : HikeStatus = HikeStatus.NOT_STARTED,
    val currentLayerIndex: Int = 0,
    var categories: List<Category> = listOf(),
    var likedByProfiles: List<String> = listOf()
)

@Serializable
data class ParticipantStatusEntry(
    val id: String = "",                        //participant's id
    val participation: ParticipantStatus = ParticipantStatus.NOT_READY,     //model in Profile.kt
    val kicked: Boolean = false
)

enum class Category(val displayName: String) {
    ADVENTURE("Adventure"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    SCI_FI("Science Fiction"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    COMEDY("Comedy"),
    INSPIRATIONAL("Inspirational"),
    HISTORICAL("Historical"),
    OTHER("Other");
}