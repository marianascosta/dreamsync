package com.example.dreamsync.data.initialization

import com.example.dreamsync.data.models.Dream
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.DreamService
import com.example.dreamsync.data.services.ProfileService
import com.google.firebase.database.FirebaseDatabase

class DatabaseInit {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val dreamService = DreamService()
    val profileService = ProfileService()

    fun initRealTimeDatabase() {
        database.reference.removeValue()

        saveProfilesSample(profilesSample)
        saveDreamsSample(dreamsSample)
    }

    fun saveDreamsSample(dreams: List<Dream>){
        dreams.forEach { dream ->
            dreamService.saveDream(dream)
        }
    }

    fun saveProfilesSample(profiles: List<Profile>){
        profiles.forEach { profile ->
            profileService.saveProfile(profile)
        }
        val firstProfile = profiles.first()
        addFriendsSample(firstProfile.id)
    }

    fun addFriendsSample(profileId: String){
        val friendIds = profilesSample.map { it.id }.filter { it != profileId }
        for (friendId in friendIds){
            profileService.addFriend(profileId, friendId)
        }
    }
}