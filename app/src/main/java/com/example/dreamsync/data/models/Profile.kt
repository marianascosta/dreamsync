package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

const val DEFAULT_PROFILE_PICTURE = "https://static.vecteezy.com/system/resources/previews/004/511/281/large_2x/default-avatar-photo-placeholder-profile-picture-vector.jpg"

enum class Status {
    NOT_READY,
    READY
}

@Serializable
data class Profile(
    var id: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userBio: String = "",
    val preferredRole: Role = Role.Dreamer,
    val profilePicture: String = DEFAULT_PROFILE_PICTURE,
    var friendsIds: List<String> = emptyList(),
    var hikeStatuses: List<HikeParticipation> = emptyList()
)


@Serializable
class HikeParticipation(
    val hikeId: String = "",
    val status: Status = Status.NOT_READY
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