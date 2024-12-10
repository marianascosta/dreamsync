package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val userName: String = "",
    val userEmail: String = "",
    val userBio: String = ""
) {
    // This constructor is necessary for Firebase deserialization.
    constructor() : this("", "", "")
}


