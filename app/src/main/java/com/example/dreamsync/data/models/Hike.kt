package com.example.dreamsync.data.models

import com.google.firebase.database.PropertyName
import kotlinx.serialization.Serializable

@Serializable
data class Hike(
    val _id: String = "",
    val name: String = "",
    val description: String = "",

    @get:PropertyName("layersSize")
    @set:PropertyName("layersSize")
    var layers: Int = 0,

    val isComplete: Boolean = false,
    val createdBy: String = "",
    val invitedFriends: List<String> = emptyList()
)