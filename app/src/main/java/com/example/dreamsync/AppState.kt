package com.example.dreamsync

import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.ProfileService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppState {
    private val _loggedInUser = MutableStateFlow(Profile(userName = ""))
    val loggedInUser: StateFlow<Profile> = _loggedInUser
    val profileService = ProfileService()

    fun updateLoggedInUser(profile: Profile) {
        _loggedInUser.value = profile
        updateProfileInDB()
    }

    private fun updateProfileInDB() {
        profileService.updateProfile(_loggedInUser.value.id, _loggedInUser.value) { success ->
            if (success) {
                println("Logged in user profile updated successfully")
            } else {
                println("Logged in user profile failed to update")
            }
        }
    }
}