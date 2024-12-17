package com.example.dreamsync.data.models;

import kotlinx.serialization.Serializable;

@Serializable
data class Account (
    val email: String = "",
    val password: String = "",
    val profileId : String = ""
)