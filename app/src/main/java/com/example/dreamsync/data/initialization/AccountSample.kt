package com.example.dreamsync.data.initialization

import com.example.dreamsync.data.models.Account

val adminAccount = Account(
    email = "admin@email.com",
    password = "password",
    profileId = "" // This will be set after saving the profile
)
