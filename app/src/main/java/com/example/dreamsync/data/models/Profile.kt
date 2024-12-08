package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(val name: String = "") {
    constructor() : this("") // This is the no-argument constructor Firebase requires
}
