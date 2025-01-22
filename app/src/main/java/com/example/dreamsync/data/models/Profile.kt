package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class Profile(
    var id: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userBio: String = "",
    val preferredRole: Role = Role.Dreamer,
    val profilePicture: String = "",
    var friendsIds: List<String> = emptyList(),
    @Transient val imageResId: Int = 0
)

@Serializable
enum class Role {
    Architect,
    Dreamer,
    Extractor,
    Forger,
    PointMan,
    Chemist
}