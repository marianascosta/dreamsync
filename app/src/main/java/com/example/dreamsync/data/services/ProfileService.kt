package com.example.dreamsync.data.services

import android.util.Log
import com.example.dreamsync.data.models.Profile
import com.google.firebase.database.*

open class ProfileService {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val profilesRef: DatabaseReference = database.getReference("profiles")

    fun saveProfile(profile: Profile, onProfileSaved: (String?) -> Unit) {
        val newProfileId = profilesRef.push().key
            ?: throw Exception("Failed to generate a new profile ID")
        profile.id = newProfileId
        profilesRef.child(newProfileId).setValue(profile).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onProfileSaved(newProfileId)
                Log.d("ProfileService", "Profile written successfully: $profile")
            } else {
                onProfileSaved(null)
                Log.e("ProfileService", "Failed to write profile.", task.exception)
            }
        }
    }

    fun getProfileById(profileId: String, onProfileFetched: (Profile) -> Unit) {
        profilesRef.child(profileId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(Profile::class.java)
                if (profile != null) {
                    onProfileFetched(profile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileService", "Failed to fetch profile: $profileId", error.toException())
                //onProfileFetched(null)
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

    fun deleteProfile(profileId: String) {
        profilesRef.child(profileId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ProfileService", "Profile deleted: $profileId")
                } else {
                    Log.e("ProfileService", "Failed to delete profile: ${task.exception}")
                }
            }
    }

    open fun getFriendsList(profileId: String, onFriendsFetched: (List<Profile>) -> Unit) {
        getProfileById(profileId) { profile ->
            if (profile != null) {
                val friendsIds: List<String> = profile.friendsIds
                val friends = mutableListOf<Profile>()
                var friendsFetchedCount = 0

                for (friendId in friendsIds) {
                    getProfileById(friendId) { friend ->
                        if (friend != null) {
                            friends.add(friend)
                        }
                        friendsFetchedCount++
                        if (friendsFetchedCount == friendsIds.size) {
                            onFriendsFetched(friends)
                            Log.d("com.example.dreamsync.screens.internal.friends.FriendsScreen", "Fetched ${friends.size} friends for ${profile.userName}")
                        }
                    }
                }

                if (friendsIds.isEmpty()) {
                    onFriendsFetched(emptyList())
                }
            } else {
                onFriendsFetched(emptyList())
            }
        }
    }

    fun addFriend(profile: Profile, friendId: String) {
        val friendsRef = profilesRef.child(profile.id).child("friendsIds")

        friendsRef.get().addOnSuccessListener { snapshot ->
            val currentFriends = snapshot.getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()

            if (!currentFriends.contains(friendId)) {
                val updatedFriends = currentFriends.toMutableList().apply { add(friendId) }
                friendsRef.setValue(updatedFriends).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("ProfileService", "Friend added: $friendId for ${profile.userName}")
                    } else {
                        Log.e("ProfileService", "Failed to add friend.")
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("ProfileService", "Failed to fetch friends list", exception)
        }
    }

    fun searchProfiles(searchText: String, onProfilesFound: (List<Profile>) -> Unit) {
        profilesRef.orderByChild("userName").startAt(searchText).endAt("$searchText\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profiles = snapshot.children.mapNotNull { it.getValue(Profile::class.java) }
                    onProfilesFound(profiles)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProfileService", "Error searching profiles: ${error.message}")
                    onProfilesFound(emptyList())
                }
            })
    }
}