package com.example.dreamsync.data.services

import android.util.Log
import com.example.dreamsync.data.models.Profile
import com.google.firebase.database.*

class ProfileService {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val profilesRef: DatabaseReference = database.getReference("profiles")

    fun saveProfile(profile: Profile) {
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
}