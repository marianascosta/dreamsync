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
    var adminFriendIds = emptyList<String>()

    fun initRealTimeDatabase() {
        database.reference.removeValue()

        saveProfilesSample(profilesSample)
        saveDreamsSample(dreamsSample)

        registerAdminUser()
    }

    fun registerAdminUser() {
        // guardar o profile do admin na colecao profiles
        profileService.saveProfile(adminProfile, onProfileSaved = { profileId ->
            if (profileId != null) {
                // guardar o informacao de login na colecao accounts
                accountService.saveAccount(
                    adminAccount.copy(profileId = profileId),
                    onAccountSaved = { success ->
                        Log.d("DatabaseInit", "Admin account saved: $success")
                    }
                )
                adminProfile.id = profileId
                // adicionar os amigos do admin
                for (friendId in adminFriendIds) {
                    profileService.addFriend(adminProfile, friendId)
                }
            }
        })


    }

    fun saveDreamsSample(dreams: List<Dream>){
        dreams.forEach { dream ->
            dreamService.saveDream(dream)
        }
    }

    fun saveProfilesSample(profiles: List<Profile>) {
        profiles.forEach { profile ->
            profileService.saveProfile(profile, onProfileSaved = { profileId ->
                Log.d("DatabaseInit", "Profile saved: $profileId")
            })

            if (adminFriendIds.size < 3) {
                profileService.saveProfile(profile, onProfileSaved = { profileId ->
                    adminFriendIds += profileId!!
                })
            }
        }
    }
}