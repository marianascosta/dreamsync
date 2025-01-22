package com.example.dreamsync.data.services

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.dreamsync.data.models.Dream
import com.example.dreamsync.data.models.DreamCategory

open class DreamService {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val dreamsRef: DatabaseReference = database.getReference("dreams")

    fun saveDream(dream: Dream) {
        val dreamWithId = dream.copy(id = dreamsRef.push().key ?: "")
        dreamsRef.child(dreamWithId.id).setValue(dreamWithId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DreamService", "Dream written successfully.")
            } else {
                Log.e("DreamService", "Failed to write dream.", task.exception)
            }
        }
    }

    fun getDreamById(dreamId: String, onDreamFetched: (Dream?) -> Unit) {
        dreamsRef.child(dreamId).get().addOnSuccessListener {
            val dream = it.getValue(Dream::class.java)
            onDreamFetched(dream)
        }.addOnFailureListener { error ->
            Log.e("DreamService", "Failed to fetch dream: ${error.message}")
            onDreamFetched(null)
        }
    }

    fun getDreamsList(onDreamsFetched: (List<Dream>) -> Unit) {
        dreamsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dreams = mutableListOf<Dream>()
                for (dreamSnapshot in snapshot.children) {
                    val dream = dreamSnapshot.getValue(Dream::class.java)
                    if (dream != null) {
                        dreams.add(dream)
                    }
                }
                onDreamsFetched(dreams)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DreamService", "Error fetching dreams: ${error.message}")
                onDreamsFetched(emptyList())
            }
        })
    }

    fun updateDream(dreamId : String, dream: Dream) {
        dreamsRef.child(dreamId).setValue(dream).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DreamService", "Dream updated successfully.")
            } else {
                Log.e("DreamService", "Failed to update dream.", task.exception)
            }
        }
    }
}