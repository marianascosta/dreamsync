package com.example.dreamsync.data.services

import android.util.Log
import com.example.dreamsync.data.models.Profile
import com.google.firebase.database.*

open class ProfileService {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val profilesRef: DatabaseReference = database.getReference("profiles")

    fun saveProfile(profile: Profile) {
        val newProfileId = profilesRef.push().key
        if (newProfileId != null) {
            profile.id = newProfileId
        }
        profilesRef.push().setValue(profile).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("ProfileService", "Profile written successfully.")
            } else {
                Log.e("ProfileService", "Failed to write profile.", task.exception)
            }
        }
    }


    fun getProfileById(profileId: String, onProfileFetched: (Profile?) -> Unit) {
        profilesRef.child(profileId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(Profile::class.java)
                // Callback to return the fetched profile
                onProfileFetched(profile)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileService", "Failed to fetch profile: $profileId", error.toException())
                // Returning null if fetch failed
                onProfileFetched(null)
            }
        })
    }

    fun updateProfile(profileId: String, updatedProfile: Profile, onComplete: (Boolean) -> Unit) {
        profilesRef.child(profileId).setValue(updatedProfile)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
                if (task.isSuccessful) {
                    Log.d("ProfileService", "Profile updated: ${updatedProfile.userName}")
                } else {
                    Log.e("ProfileService", "Failed to update profile: ${task.exception}")
                }
            }
    }

    fun deleteProfile(profileId: String, onComplete: (Boolean) -> Unit) {
        profilesRef.child(profileId).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
                if (task.isSuccessful) {
                    Log.d("ProfileService", "Profile deleted: $profileId")
                } else {
                    Log.e("ProfileService", "Failed to delete profile: ${task.exception}")
                }
            }
    }

    open fun getFriendsList(profileId: String, onFriendsFetched: (List<Profile>) -> Unit) {
        profilesRef.child(profileId).child("friendsIds").get().addOnSuccessListener { snapshot ->
            val friendIds = snapshot.children.mapNotNull { it.getValue(String::class.java) }
            val friends = mutableListOf<Profile>()
            friendIds.forEach { friendId ->
                profilesRef.child(friendId).get().addOnSuccessListener { friendSnapshot ->
                    val friend = friendSnapshot.getValue(Profile::class.java)
                    if (friend != null) {
                        friends.add(friend)
                    }
                    if (friends.size == friendIds.size) {
                        onFriendsFetched(friends)
                    }
                }
            }
            if (friendIds.isEmpty()) {
                onFriendsFetched(friends)
            }
        }.addOnFailureListener {
            onFriendsFetched(emptyList())
        }
    }

    fun addFriend(profileId: String, friendId: String) {
        profilesRef.child(profileId).child("friendsIds").push().setValue(friendId)
            .addOnSuccessListener {
                Log.d("ProfileService", "Friend added successfully for $profileId")
            }
            .addOnFailureListener {
                Log.e("ProfileService", "Failed to add friend for $profileId")
            }
    }
}