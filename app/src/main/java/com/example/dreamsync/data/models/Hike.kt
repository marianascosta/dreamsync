package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

@Serializable
class Hike (
    val name: String,
    val description: String,
    val layers: Int,
    val isComplete: Boolean,
    val createdBy: String
) {
    constructor(): this("", "", 0, false, "")
}