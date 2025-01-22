package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Dream(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    var dreamCategories: List<DreamCategory> = listOf(),
    var likedByProfiles: List<String> = listOf(),
    @Transient val imageResId: Int = 0
)

enum class DreamCategory(val displayName: String) {
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