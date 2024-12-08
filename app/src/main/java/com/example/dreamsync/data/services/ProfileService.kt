package com.example.dreamsync.data.services

import android.util.Log
import com.example.dreamsync.data.models.Profile
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileService {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val profilesRef: DatabaseReference = database.getReference("profiles")

    fun initProfiles() {
        val profiles = listOf(
            Profile(name = "John Doe"),
            Profile(name = "Jane Smith")
        )

        profiles.forEach { profile ->
            profilesRef.push().setValue(profile).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ProfileService", "Profile written successfully.")
                } else {
                    Log.e("ProfileService", "Failed to write profile.", task.exception)
                }
            }
        }
    }

    fun saveProfile(profile: Profile) {
        profilesRef.push().setValue(profile).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("ProfileService", "Profile written successfully.")
            } else {
                Log.e("ProfileService", "Failed to write profile.", task.exception)
            }
        }
    }

    fun getProfiles() {
        profilesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (profileSnapshot in snapshot.children) {
                    val profile = profileSnapshot.getValue(Profile::class.java)
                    Log.d("ProfileService", "Profile read: ${profile?.name}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ProfileService", "Failed to read profiles.", error.toException())
            }
        })
    }

    fun getProfileById(profileId: String, onProfileFetched: (Profile?) -> Unit) {
        profilesRef.child(profileId).get().addOnSuccessListener {
            val profile = it.getValue(Profile::class.java)
            onProfileFetched(profile)
        }.addOnFailureListener { error ->
            Log.e("ProfileService", "Failed to fetch profile: ${error.message}")
            onProfileFetched(null)
        }
    }
}