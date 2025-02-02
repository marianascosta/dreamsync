package com.example.dreamsync.data.services

import android.util.Log
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.HikeStatus
import com.example.dreamsync.data.models.ParticipantStatusEntry
import com.example.dreamsync.data.models.ParticipantStatus
import com.example.dreamsync.screens.internal.hikes.insideHike.HikeStage
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

    fun getAllHikes(onHikesFetched: (List<Hike>) -> Unit) {
        hikesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hikes = snapshot.children.mapNotNull { it.getValue(Hike::class.java) }
                onHikesFetched(hikes)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HikeService", "Failed to fetch hikes", error.toException())
                onHikesFetched(emptyList())
            }
        })
    }

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

    fun updateParticipantStatus(hikeId: String, userId: String, newStatus: ParticipantStatus) {
        hikesRef.child(hikeId).child("participantStatus").get().addOnSuccessListener { dataSnapshot ->
            var found = false
            for (participantSnapshot in dataSnapshot.children) {
                val participantId = participantSnapshot.child("id").getValue(String::class.java)
                if (participantId == userId) {
                    val participantRef = participantSnapshot.ref
                    participantRef.child("participation").setValue(newStatus).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("HikeService", "Participant status updated successfully: $hikeId, $userId")
                        } else {
                            Log.e("HikeService", "Failed to update participant status: $hikeId, $userId", task.exception)
                        }
                    }
                    found = true
                    break
                }
            }
            if (!found) {
                Log.e("HikeService", "Participant not found: $hikeId, $userId")
            }
        }.addOnFailureListener {
            Log.e("HikeService", "Failed to get participant status: $hikeId, $userId", it)
        }
    }

    fun observeParticipantStatus(hikeId: String, onStatusChanged: (List<ParticipantStatusEntry>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("hikes").child(hikeId).child("participantStatus")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val statuses = snapshot.children.mapNotNull { child ->
                            val userId = child.child("id").getValue(String::class.java)
                            val status = child.child("participation").getValue(String::class.java)
                            val kicked = child.child("kicked").getValue(Boolean::class.java)

                            if (userId != null && status != null && kicked != null) {
                                ParticipantStatusEntry(
                                    id = userId,
                                    participation = ParticipantStatus.valueOf(status),
                                    kicked = kicked
                                )
                            } else null
                        }
                        onStatusChanged(statuses)
                    } else {
                        onStatusChanged(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Failed to read participant statuses", error.toException())
                }
            })
    }

    fun updateCurrentLayerIndex(hikeId: String, layerIndex: Int) {
        val hikeRef = hikesRef.child(hikeId)
        hikeRef.child("currentLayerIndex").setValue(layerIndex)
    }

    fun getCurrentLayerIndex(hikeId: String, onCurrentLayerIndexFetched: (Int) -> Unit) {
        val hikeRef =hikesRef.child(hikeId)
        hikeRef.child("currentLayerIndex").get().addOnSuccessListener { snapshot ->
            val currentLayerIndex = snapshot.getValue(Int::class.java) ?: 0
            onCurrentLayerIndexFetched(currentLayerIndex)
            Log.d("HikeDebug", "Current layer index fetched: $currentLayerIndex")
        }.addOnFailureListener { e ->
            Log.e("HikeDebug", "Failed to fetch current layer index: ", e)
        }
    }

    fun updateHikeStage(hikeId: String, newStage: HikeStage) {
        val hikeRef = hikesRef.child(hikeId)

        hikeRef.updateChildren(mapOf("stage" to newStage.name))
            .addOnSuccessListener {
                Log.d("HikeDebug", "Hike stage updated to $newStage")
            }
            .addOnFailureListener { e ->
                Log.e("HikeDebug", "Failed to update hike stage: ", e)
            }
    }

    fun updateParticipantKickStatus(hikeId: String, userId: String, kicked: Boolean) {
        hikesRef.child(hikeId).child("participantStatus").get().addOnSuccessListener { dataSnapshot ->
            var found = false
            for (participantSnapshot in dataSnapshot.children) {
                val participantId = participantSnapshot.child("id").getValue(String::class.java)
                if (participantId == userId) {
                    val participantRef = participantSnapshot.ref
                    participantRef.child("kicked").setValue(kicked).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("HikeService", "Participant status updated successfully: $hikeId, $userId")
                        } else {
                            Log.e("HikeService", "Failed to update participant status: $hikeId, $userId", task.exception)
                        }
                    }
                    found = true
                    break
                }
            }
            if (!found) {
                Log.e("HikeService", "Participant not found: $hikeId, $userId")
            }
        }.addOnFailureListener {
            Log.e("HikeService", "Failed to get participant status: $hikeId, $userId", it)
        }
    }
}