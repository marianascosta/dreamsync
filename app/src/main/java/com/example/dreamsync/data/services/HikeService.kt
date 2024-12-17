package com.example.dreamsync.data.services

import android.util.Log
import com.example.dreamsync.data.models.Hike
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HikeService {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val hikesRef: DatabaseReference = database.getReference("hikes")

    fun saveHike(hike: Hike) {
        hikesRef.push().setValue(hike).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("HikeService", "Hike written successfully.")
            } else {
                Log.e("HikeService", "Failed to write hike.", task.exception)
            }
        }
    }

    fun getHikeById(hikeId: String, onHikeFetched: (Hike?) -> Unit) {
        hikesRef.child(hikeId).get().addOnSuccessListener {
            val hike = it.getValue(Hike::class.java)
            onHikeFetched(hike)
        }
    }
}