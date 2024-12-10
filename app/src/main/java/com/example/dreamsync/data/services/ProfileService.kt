package com.example.dreamsync.data.services

import android.util.Log
import com.example.dreamsync.data.models.Profile
import com.google.firebase.database.*

class ProfileService {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val profilesRef: DatabaseReference = database.getReference("profiles")

    fun initProfiles() {
        val profiles = listOf(
            Profile(userName = "John Doe"),
            Profile(userName = "Jane Smith")
        )

        profiles.forEach { profile ->
            saveProfile(profile) { success ->
                if (success) {
                    Log.d("ProfileService", "Profile initialized: ${profile.userName}")
                } else {
                    Log.e("ProfileService", "Failed to initialize profile: ${profile.userName}")
                }
            }
        }
    }

    fun saveProfile(profile: Profile, onComplete: (Boolean) -> Unit) {
        val profileKey = profilesRef.push().key ?: return onComplete(false)
        profilesRef.child(profileKey).setValue(profile)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
                if (task.isSuccessful) {
                    Log.d("ProfileService", "Profile saved: ${profile.userName}")
                } else {
                    Log.e("ProfileService", "Failed to save profile: ${task.exception}")
                }
            }
    }

    fun getProfiles(onProfilesFetched: (List<Profile>) -> Unit) {
        profilesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profiles = mutableListOf<Profile>()
                snapshot.children.forEach { profileSnapshot ->
                    val profile = profileSnapshot.getValue(Profile::class.java)
                    profile?.let { profiles.add(it) }
                }
                // Callback to provide the fetched profiles list
                onProfilesFetched(profiles)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileService", "Failed to fetch profiles.", error.toException())
                // Returning empty list if fetch failed
                onProfilesFetched(emptyList())
            }
        })
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
