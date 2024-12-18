package com.example.dreamsync.data.initialization

import android.util.Log
import com.example.dreamsync.data.models.Dream
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.AccountService
import com.example.dreamsync.data.services.DreamService
import com.example.dreamsync.data.services.ProfileService
import com.google.firebase.database.FirebaseDatabase

class DatabaseInit {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val dreamService = DreamService()
    val profileService = ProfileService()
    val accountService = AccountService()

    // cache
    var profilesList = mutableListOf<Profile>()
    var accountsList = mutableListOf<Profile>()

    constructor() {
        Log.d("DatabaseInit", "Database URL: ${database.reference}")
    }

    fun initRealTimeDatabase() {
        database.reference.removeValue()

        saveAdmin()
        saveProfilesSample(profilesSample)
        saveDreamsSample(dreamsSample)

        //wait for the profiles to be saved
        Log.d("DatabaseInit", "Profiles saved: $profilesList")
        Log.i("DatabaseInit", "Database initialized")
    }

    fun saveAdmin() {
        // guardar o profile do admin na colecao profiles
        profileService.saveProfile(adminProfile, onProfileSaved = {
            adminProfileId ->
                // guardar o informacao de login na colecao accounts
                accountService.saveAccount(
                    adminAccount.copy(profileId = adminProfileId!!),
                    onAccountSaved = { success ->
                        Log.d("DatabaseInit", "Admin account saved: $success")
                    }
                )
        })
    }

    fun saveDreamsSample(dreams: List<Dream>){
        dreams.forEach { dream ->
            dreamService.saveDream(dream)
        }
    }

    fun saveProfilesSample(profiles: List<Profile>) {
        profiles.forEach { profile ->
            profileService.saveProfile(
                profile = profile,
                onProfileSaved = { profileId ->
                    //update the profile.id before saving to cache
                    profile.id = profileId!!
                    profilesList.add(profile)
                    addFriendToAdmin(profileId)
                    Log.d("DatabaseInit", "Profile saved: $profile")

            })
        }
    }

    fun addFriendToAdmin(friendId: String) {
        profileService.addFriend(adminProfile, friendId)
        Log.d("DatabaseInit", "Friend added to admin: $friendId")
    }
}