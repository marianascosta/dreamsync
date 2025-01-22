package com.example.dreamsync.screens.internal.hikes.insideHike

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.HikeStatus
import com.example.dreamsync.data.models.ParticipantStatus
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun ConfirmationScreen(
    hikeId: String,
    hikeService: HikeService,
    //navController: NavHostController,
    loggedUser: Profile,
    leavingLayer: Boolean
) {
    var isConfirmed by remember { mutableStateOf(false) }
    var readyCount by remember { mutableStateOf(0) }
    var totalParticipants by remember { mutableStateOf(0) }
    var stage by remember { mutableStateOf(HikeStage.NOT_STARTED) } // Initial stage

    LaunchedEffect(Unit) {
        hikeService.observeParticipantStatus(hikeId) { statuses ->
//            Log.d("ParticipantStatus", "In confirm Statuses updated: $statuses")
//            readyCount = statuses.count { it.participation == ParticipantStatus.READY }
            Log.d("ParticipantStatus", "In confirm Statuses updated: $statuses")
            readyCount = statuses.count { it.participation == ParticipantStatus.READY }
            totalParticipants = statuses.size
        }
    }

    LaunchedEffect(isConfirmed) {
        if (isConfirmed) {
            hikeService.updateParticipantStatus(hikeId, loggedUser.id, ParticipantStatus.READY)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Confirm to proceed to the next layer")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "$readyCount / $totalParticipants ready")
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { isConfirmed = true },
                enabled = !isConfirmed
            ) {
                Text(text = if (isConfirmed) "Waiting for others" else "Confirm")
            }
        }
    }
}

