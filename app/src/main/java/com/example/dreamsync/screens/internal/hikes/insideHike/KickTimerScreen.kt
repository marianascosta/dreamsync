package com.example.dreamsync.screens.internal.hikes.insideHike

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.ParticipantStatusEntry
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.sensors.detectKick
import com.example.dreamsync.data.services.HikeService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun KickTimerScreen(
    hikeId: String,
    participantId: String,
    hikeService: HikeService,
    onTransitionToLayer: () -> Unit,
    onTransitionToStuckScreen: () -> Unit
) {
    val context = LocalContext.current
    var remainingTime by remember { mutableStateOf(10) } // Example: 10 seconds countdown
    val timerScope = rememberCoroutineScope()
    var kicked by remember { mutableStateOf(false) }

    val participantStatuses = remember { mutableStateListOf<ParticipantStatusEntry>() }
    LaunchedEffect(Unit) {
        hikeService.observeParticipantStatus(hikeId) { statuses ->
            participantStatuses.clear()
            participantStatuses.addAll(statuses)
        }
    }

    // Start the timer
    LaunchedEffect(Unit) {
        timerScope.launch {
            while (remainingTime > 0) {
                delay(1000)
                remainingTime -= 1
            }

            // Timer finished, transition users based on kick status
            val currentParticipant = participantStatuses.find { it.id == participantId }
            if (currentParticipant?.kicked == true) {
                onTransitionToLayer()
            } else {
                onTransitionToStuckScreen()
            }
        }
    }


    // Kick detection logic
    LaunchedEffect(Unit) {
        detectKick(context) { detected ->
            if (detected) {
                kicked = true
                hikeService.updateParticipantKickStatus(hikeId, participantId, true)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Time Remaining: $remainingTime seconds",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "People who kicked: ${participantStatuses.count { it.kicked }}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


