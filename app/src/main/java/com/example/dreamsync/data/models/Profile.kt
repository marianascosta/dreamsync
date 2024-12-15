package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val userName: String = "",
    val userEmail: String = "",
    val userBio: String = "",
    val preferredRole: String = "",
    val profilePicture: String = ""
) {
    constructor() : this("", "", "", "", "")
}