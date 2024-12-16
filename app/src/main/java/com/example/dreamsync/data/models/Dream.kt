package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Dream(
    val title: String = "",
    val description: String = "",
    val date: String = ""
) {
    constructor() : this("", "", "")
}
