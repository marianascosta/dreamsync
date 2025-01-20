package com.example.dreamsync.data.services

import android.util.Log
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.HikeStatus
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.models.participantStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

open class HikeService {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val hikesRef: DatabaseReference = database.getReference("hikes")

    open fun saveHike(hike: Hike, onHikeSaved: (Boolean) -> Unit) {
        val hikeId = hikesRef.push().key ?: return onHikeSaved(false)
        val hikeWithId = hike.copy(id = hikeId)
        hikesRef.child(hikeId).setValue(hikeWithId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("HikeService", "Hike written successfully with ID: $hikeId")
                onHikeSaved(true)
            } else {
                Log.e("HikeService", "Failed to write hike.", task.exception)
                onHikeSaved(false)
            }
        }
    }

    fun getHikeById(id: String, onHikeFetched: (Hike?) -> Unit) {
        hikesRef.child(id).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hike = snapshot.getValue(Hike::class.java)
                    onHikeFetched(hike)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HikeService", "Failed to fetch hike by ID: $id", error.toException())
                    onHikeFetched(null)
                }
            }
        )
    }

//    fun getHikesByCreatedBy(userId: String, onHikesFetched: (List<Hike>) -> Unit) {
//        hikesRef.orderByChild("createdBy").equalTo(userId).addListenerForSingleValueEvent(
//            object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val hikes = mutableListOf<Hike>()
//                    for (hikeSnapshot in snapshot.children) {
//                        val hike = hikeSnapshot.getValue(Hike::class.java)
//                        if (hike != null) {
//                            hikes.add(hike)
//                        }
//                    }
//                    Log.d("HikeService", "Fetched ${hikes.size} hikes by createdBy: $userId")
//                    onHikesFetched(hikes)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("HikeService", "Failed to fetch hikes by createdBy: $userId", error.toException())
//                    onHikesFetched(emptyList())
//                }
//            }
//        )
//    }
    fun getHikesByUser(userId: String, onHikesFetched: (List<Hike>) -> Unit) {
        hikesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hikes = mutableListOf<Hike>()
                for (hikeSnapshot in snapshot.children) {
                    val hike = hikeSnapshot.getValue(Hike::class.java)
                    Log.d("HikeService", "userId: $userId")
                    if (hike != null && (hike.createdBy == userId || hike.invitedFriends.contains(userId))) {
                        hikes.add(hike)
                    }
                }
                Log.d("HikeService", "Fetched ${hikes.size} hikes for user: $userId")
                onHikesFetched(hikes)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HikeService", "Failed to fetch hikes for user: $userId", error.toException())
                onHikesFetched(emptyList())
            }
        })
    }



    fun updateHike(hike: Hike, onUpdateComplete: (Boolean) -> Unit) {
        hikesRef.child(hike.id).setValue(hike).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("HikeService", "Hike updated successfully: ${hike.id}")
                onUpdateComplete(true)
            } else {
                Log.e("HikeService", "Failed to update hike: ${hike.id}", task.exception)
                onUpdateComplete(false)
            }
        }
    }

    fun updateHikeStatus(hikeId: String, newStatus: HikeStatus) {
        hikesRef.child(hikeId).child("status").setValue(newStatus).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("HikeService", "Hike status updated successfully: $hikeId")
            } else {
                Log.e("HikeService", "Failed to update hike status: $hikeId", task.exception)
            }
        }
    }

    fun updateParticipantStatus(hikeId: String, userId: String, newStatus: participantStatus) {
        hikesRef.child(hikeId).child("invitedFriends").child(userId).child("status").setValue(newStatus).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("HikeService", "Participant status updated successfully: $hikeId, $userId")
                } else {
                Log.e("HikeService", "Failed to update participant status: $hikeId, $userId", task.exception)
            }
        }
    }

    fun getParticipants(hikeId: String, onResult: (List<String>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val hikeRef = database.child("hikes").child(hikeId)

        hikeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    onResult(emptyList())
                    return
                }

                val participants = mutableListOf<String>()

                // Add the creator
                val createdBy = snapshot.child("createdBy").getValue(String::class.java)
                createdBy?.let { participants.add(it) }

                // Add invited friends
                val invitedFriendsSnapshot = snapshot.child("invitedFriends")
                for (friendSnapshot in invitedFriendsSnapshot.children) {
                    val friendId = friendSnapshot.getValue(String::class.java)
                    friendId?.let { participants.add(it) }
                }

                onResult(participants)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("getParticipants", "Error fetching participants: ${error.message}")
                onResult(emptyList())
            }
        })
    }


}