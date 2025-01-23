package com.example.dreamsync.data.initialization

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.AccountService
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

@Preview
@Composable
fun RunDatabaseInit() {
    val databaseInit = DatabaseInit()
    databaseInit.initRealTimeDatabase()
}

class DatabaseInit {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = database.reference
    val profileService = ProfileService()
    private val accountService = AccountService()
    private val hikesService = HikeService()

    // Cache
    private var profilesList = mutableListOf<Profile>()

    init {
        Log.d("DatabaseInit", "Database URL: ${database.reference}")
    }

    fun initRealTimeDatabase() {

        Log.i("DatabaseInit", "Initializing database: ${database.reference.root}")

        deleteAll()

        saveProfilesSample(
            profiles = profilesSample,
            onProfilesSaved = { profiles ->
                profilesList = profiles.toMutableList()
                saveAdmin()
                saveSophia()
                saveMichael()
            }
        )
    }

    private fun deleteAll() {
        databaseReference.removeValue()
    }

    private fun saveAdmin() {
        profileService.saveProfile(adminProfile) { adminProfileId ->
            if (adminProfileId != null) {
                saveAdminAccount(adminProfileId)
                saveAdminHikes(adminProfileId)
                addAdminFriends(adminProfileId)
            } else {
                Log.e("DatabaseInit", "Failed to save admin profile")
            }
        }
    }

    private fun saveSophia() {
        profileService.saveProfile(profilesSample[4]) { sophiaProfileId ->
            if (sophiaProfileId != null) {
                saveSophiaAccount(sophiaProfileId)
                saveSophiaHikes(sophiaProfileId)
                addSophiaFriends(sophiaProfileId)
            } else {
                Log.e("DatabaseInit", "Failed to save admin profile")
            }
        }
    }

    private fun saveMichael() {
        profileService.saveProfile(profilesSample[3]) { michaelProfileId ->
            if (michaelProfileId != null) {
                saveMichaelAccount(michaelProfileId)
                saveMichaelHikes(michaelProfileId)
                addMichaelFriends(michaelProfileId)
            } else {
                Log.e("DatabaseInit", "Failed to save admin profile")
            }
        }
    }

    private fun saveMichaelAccount(michaelProfileId: String) {
        accountService.saveAccount(michaelAccount.copy(profileId = michaelProfileId)) { success ->
            Log.d("DatabaseInit", "Michael account saved: $success")
        }
    }

    private fun saveMichaelHikes(michaelProfileId: String) {
        hikes.forEach { hike ->
            hikesService.saveHike(
                hike.copy(createdBy = michaelProfileId)

            ) {
                Log.d("DatabaseInit", "Hike saved: $hike")
            }
        }
    }

    private fun addMichaelFriends(michaelProfileId: String) {
        for (profile in profilesList) {
            if (profile.id != michaelProfileId) {
                profileService.addFriend(profilesSample[3], profile.id)
            }
        }
    }

    private fun addSophiaFriends(sophiaProfileId: String) {
        for (profile in profilesList) {
            if (profile.id != sophiaProfileId) {
                profileService.addFriend(profilesSample[4], profile.id)
            }
        }
    }

    private fun saveSophiaHikes(sophiaProfileId: String) {
        hikes.forEach { hike ->
            hikesService.saveHike(hike.copy(createdBy = sophiaProfileId)) {
                Log.d("DatabaseInit", "Hike saved: $hike")
            }
        }
    }

    private fun saveSophiaAccount(sophiaProfileId: String) {
        accountService.saveAccount(sophieAccount.copy(profileId = sophiaProfileId)) { success ->
            Log.d("DatabaseInit", "Sophia account saved: $success")
        }
    }

    private fun saveAdminAccount(adminProfileId: String) {
        accountService.saveAccount(adminAccount.copy(profileId = adminProfileId)) { success ->
            Log.d("DatabaseInit", "Admin account saved: $success")
        }
    }

    private fun saveAdminHikes(adminProfileId: String) {
        hikes.forEach { hike ->
            hikesService.saveHike(hike.copy(createdBy = adminProfileId)) {
                Log.d("DatabaseInit", "Hike saved: $hike")
            }
        }
    }

    private fun addAdminFriends(adminProfileId: String) {
        for (profile in profilesList) {
            if (profile.id != adminProfileId) {
                profileService.addFriend(adminProfile, profile.id)
            }
        }
    }

    private fun saveProfilesSample(profiles: List<Profile>, onProfilesSaved: (List<Profile>) -> Unit) {
        var counter = 0
        profiles.forEach { profile ->
            profileService.saveProfile(profile) { profileId ->
                if (profileId != null) {
                    counter += 1
                    if (counter == profiles.size) {
                        onProfilesSaved(profiles)
                    }
                }
            }
        }
    }
}



