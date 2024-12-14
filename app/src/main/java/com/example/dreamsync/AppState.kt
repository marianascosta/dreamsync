package com.example.dreamsync

import com.example.dreamsync.data.models.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppState {
    private val _loggedInUser = MutableStateFlow(Profile(name = ""))
    val loggedInUser: StateFlow<Profile> = _loggedInUser

    fun updateLoggedInUser(profile: Profile) {
        _loggedInUser.value = profile
    }
}