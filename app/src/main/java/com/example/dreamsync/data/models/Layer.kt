package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Layer(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val difficulty: String = "",
    val startDate: String = "",
    val kickDate: String = "",
)