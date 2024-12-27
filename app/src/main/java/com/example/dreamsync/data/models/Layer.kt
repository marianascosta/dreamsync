package com.example.dreamsync.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Layer(
    val name: String = "",
    val description: String = "",
    val difficulty: String = "",
    val startDate: String = "",
)