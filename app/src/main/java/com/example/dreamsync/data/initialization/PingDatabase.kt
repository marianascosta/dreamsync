package com.example.dreamsync.data.initialization

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.database.*


@Preview
@Composable
fun PingDatabase() {
    PingDatabase.ping()
}

object PingDatabase {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val pingRef: DatabaseReference = database.getReference("ping")

    fun ping() {
        val currentTime = System.currentTimeMillis()
        pingRef.setValue(currentTime).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("PingDatabase", "Ping written successfully: $currentTime")
            } else {
                Log.e("PingDatabase", "Failed to write ping.", task.exception)
            }
        }
    }
}