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
    var name: String = "",
    var description: String = "",
    val layers: List<Layer> = emptyList(),
    val isComplete: Boolean = false,
    val createdBy: String = "",
    val invitedFriends: List<String> = emptyList(),
    val participantStatus: List<ParticipantStatusEntry> = emptyList(),
    val stage: HikeStage = HikeStage.WAITING_FOR_OTHERS,
    val status : HikeStatus = HikeStatus.NOT_STARTED,
    val currentLayerIndex: Int = 0,
    var categories: List<Category> = listOf(),
    var likedByProfiles: List<String> = listOf(),
    var hikeDefaultImage: HikeDefaultImage = HikeDefaultImage.ATLANTIS
)

@Serializable
data class ParticipantStatusEntry(
    val id: String = "",
    val participation: ParticipantStatus = ParticipantStatus.NOT_READY,
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

enum class HikeDefaultImage(val fileName: String) {
    ATLANTIS("atlantis.jpg"),
    FOREST("forest.jpg"),
    LAST_ENCHANTMENT("last_enchantment.jpg"),
    CHRONICLES_OF_TIME("chronicles_of_time.jpg"),
    HAUNTING_SHADOWS("haunting_shadows.jpg"),
    LOVE_STARS("love_stars.jpg"),
    COSMIC_WONDERS("cosmic_wonders.jpg"),
    ESCAPE_UNKNOWN("escape_unknown.jpg"),
    JOURNEY_HEART("journey_heart.jpg"),
    MISSING_ARTIFACT("missing_artifact.jpg")
}