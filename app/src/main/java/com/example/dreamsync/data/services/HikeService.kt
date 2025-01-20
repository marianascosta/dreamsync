package com.example.dreamsync.data.services

import android.util.Log
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.HikeStatus
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

    fun getHikesByCreatedBy(userId: String, onHikesFetched: (List<Hike>) -> Unit) {
        hikesRef.orderByChild("createdBy").equalTo(userId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hikes = mutableListOf<Hike>()
                    for (hikeSnapshot in snapshot.children) {
                        val hike = hikeSnapshot.getValue(Hike::class.java)
                        if (hike != null) {
                            hikes.add(hike)
                        }
                    }
                    Log.d("HikeService", "Fetched ${hikes.size} hikes by createdBy: $userId")
                    onHikesFetched(hikes)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HikeService", "Failed to fetch hikes by createdBy: $userId", error.toException())
                    onHikesFetched(emptyList())
                }
            }
        )
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
}