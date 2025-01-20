package com.example.dreamsync.data.initialization

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.dreamsync.AppState.profileService
import com.example.dreamsync.data.models.Dream
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.AccountService
import com.example.dreamsync.data.services.DreamService
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Transaction
import kotlin.concurrent.thread

@Preview
@Composable
fun RunDatabaseInit() {
    val databaseInit = DatabaseInit()
    databaseInit.initRealTimeDatabase()
}

class DatabaseInit {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = database.reference
    val dreamService = DreamService()
    val profileService = ProfileService()
    val accountService = AccountService()
    val hikesService = HikeService()

    // Cache
    var profilesList = mutableListOf<Profile>()

    constructor() {
        Log.d("DatabaseInit", "Database URL: ${database.reference}")
    }

    fun initRealTimeDatabase() {

        Log.i("DatabaseInit", "Initializing database: ${database.reference.root}")

        deleteAll()

        saveProfilesSample(profilesSample)
        saveDreamsSample(dreamsSample)
        saveAdmin()
        saveHikesSample(hikes)
    }

    fun deleteAll() {
        databaseReference.removeValue()
    }

    fun saveAdmin() {
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

    private fun saveAdminAccount(adminProfileId: String) {
        accountService.saveAccount(adminAccount.copy(profileId = adminProfileId)) { success ->
            Log.d("DatabaseInit", "Admin account saved: $success")
        }
    }

    private fun saveAdminHikes(adminProfileId: String) {
        hikes.forEach { hike ->
            hikesService.saveHike(hike.copy(createdBy = adminProfileId)) { success ->
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

    fun saveDreamsSample(dreams: List<Dream>) {
        dreams.forEach { dream ->
            dreamService.saveDream(dream)
        }
    }

    fun saveHikesSample(hikes: List<Hike>) {
        hikes.forEach { hike ->
            hikesService.saveHike(hike) {
                Log.d("DatabaseInit", "Hike saved: $hike")
            }
        }
    }

    fun saveProfilesSample(profiles: List<Profile>) {
        profiles.forEach { profile ->
            profileService.saveProfile(profile) { profileId ->
                profile.id = profileId!!
                profilesList.add(profile)
                Log.d("DatabaseInit", "Profile saved: $profile")
            }
        }
    }
}