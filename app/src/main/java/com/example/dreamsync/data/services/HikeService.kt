package com.example.dreamsync.data.services

import android.util.Log
import androidx.core.view.children
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.HikeParticipation
import com.example.dreamsync.data.models.HikeStatus
import com.example.dreamsync.data.models.Layer
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.models.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.atomic.AtomicInteger

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
//    open fun saveHike(hike: Hike, onHikeSaved: (Boolean) -> Unit) {
//        val hikeId = hikesRef.push().key ?: return onHikeSaved(false)
//
//        // Log the hike data to debug
//        Log.d("HikeService", "Attempting to save hike: $hike")
//
//        // Write the hike data to Firebase
//        hikesRef.child(hikeId).setValue(hike).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.d("HikeService", "Hike written successfully with ID: $hikeId")
//                onHikeSaved(true)
//            } else {
//                Log.e("HikeService", "Failed to write hike.", task.exception)
//                onHikeSaved(false)
//            }
//        }
//    }

//    fun getHikeById(id: String, onHikeFetched: (Hike?) -> Unit) {
//        hikesRef.child(id).addListenerForSingleValueEvent(
//            object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val hike = snapshot.getValue(Hike::class.java)
//                    onHikeFetched(hike)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("HikeService", "Failed to fetch hike by ID: $id", error.toException())
//                    onHikeFetched(null)
//                }
//            }
//        )
//    }
    fun getHikeById(id: String, onHikeFetched: (Hike?) -> Unit) {
        val hikeRef = FirebaseDatabase.getInstance().getReference("hikes").child(id)

        hikeRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val hike = snapshot.getValue(Hike::class.java)
                hike?.let {
                    // Manually map layers if needed
                    val layersSnapshot = snapshot.child("layers")
                    val layers = layersSnapshot.children.mapNotNull { it.getValue(Layer::class.java) }
                    it.copy(layers = layers) // Update the hike object with parsed layers
                }
                onHikeFetched(hike)
            } else {
                onHikeFetched(null)
            }
        }.addOnFailureListener {
            Log.e("HikeService", "Error fetching hike with ID: $id", it)
            onHikeFetched(null)
        }
    }

    fun getHikesCreatedByAndInvitedTo(userId: String, onHikesFetched: (List<Hike>) -> Unit) {
        val hikes = mutableListOf<Hike>()
        val hikesRef = FirebaseDatabase.getInstance().getReference("hikes")

        // Query for hikes created by the user
        val createdByQuery = hikesRef.orderByChild("createdBy").equalTo(userId)

        // Listen for createdByQuery
        createdByQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val createdByHikes = snapshot.children.mapNotNull { it.getValue(Hike::class.java) }
                hikes.addAll(createdByHikes)

                // Now check for invitedFriends
                hikesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(allHikesSnapshot: DataSnapshot) {
                        for (hikeSnapshot in allHikesSnapshot.children) {
                            val invitedFriends = hikeSnapshot.child("invitedFriends").children.mapNotNull { it.value as? String }
                            if (invitedFriends.contains(userId)) {
                                val hike = hikeSnapshot.getValue(Hike::class.java)
                                if (hike != null) {
                                    hikes.add(hike)
                                }
                            }
                        }

                        // Return combined results
                        onHikesFetched(hikes)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("HikeService", "Failed to fetch invited hikes", error.toException())
                        onHikesFetched(emptyList())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HikeService", "Failed to fetch createdBy hikes", error.toException())
                onHikesFetched(emptyList())
            }
        })
    }


    fun getParticipants(hikeId: String, callback: (Map<String, Profile>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val participants = mutableMapOf<String, Profile>()

        database.child("hikes").child(hikeId).get().addOnSuccessListener { hikeSnapshot ->
            val invitedFriends = hikeSnapshot.child("invitedFriends").children.mapNotNull { it.value as? String }

            val remainingFetches = AtomicInteger(invitedFriends.size)
            for (friendId in invitedFriends) {
                database.child("profiles").child(friendId).get().addOnSuccessListener { profileSnapshot ->
                    val profile = profileSnapshot.getValue(Profile::class.java)
                    if (profile != null) {
                        participants[friendId] = profile
                    }

                    if (remainingFetches.decrementAndGet() == 0) {
                        callback(participants)
                    }
                }.addOnFailureListener {
                    if (remainingFetches.decrementAndGet() == 0) {
                        callback(participants) // Return partial results in case of failure
                    }
                }
            }
        }.addOnFailureListener {
            callback(emptyMap())
        }
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

    fun updateParticipantStatus(hikeId: String, participantId: String, newStatus: Status) {
        val hikeParticipation = HikeParticipation(hikeId = hikeId, status = newStatus)

        hikesRef.child(hikeId).child("participantStatus").child(participantId).setValue(hikeParticipation)

//        hikesRef.child(hikeId).child(participantId).child("hikeStatuses")
//            .child(hikeId).child("status").setValue(newStatus)
    }

    fun startHike(hikeId: String) {
        hikesRef.child(hikeId).child("status").setValue(HikeStatus.IN_PROGRESS)
    }

    fun listenToHikeStatus(hikeId: String, onStatusChanged: (String) -> Unit) {
        hikesRef.child(hikeId).child("status")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(String::class.java)
                    if (status != null) onStatusChanged(status)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HikeService", "Error listening to hike status", error.toException())
                }
            })
    }

    fun listenToParticipants(hikeId: String, onParticipantsChange: (Map<String, Profile>) -> Unit) {
        hikesRef.child(hikeId).child("participants")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val participants = snapshot.children.mapNotNull { it.getValue(Profile::class.java) }
                        .associateBy { it.id }
                    onParticipantsChange(participants)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HikeService", "Failed to listen to participants: ${error.message}")
                }
            })
    }

    fun isCreator(hike: Hike): Boolean {
        return hike.createdBy == getCurrentUserId()
    }

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User not logged in")
    }

    fun updateHikeStatus(hikeId: String, status: HikeStatus) {
        hikesRef.child(hikeId).child("status").setValue(status)
    }
}