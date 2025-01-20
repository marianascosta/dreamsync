package com.example.dreamsync.screens.internal.hikes.insideHike

import androidx.compose.foundation.layout.Arrangement
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
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.models.Status
import com.example.dreamsync.data.services.HikeService

@Composable
fun ConfirmationScreen(
    hikeId: String,
    hikeService: HikeService,
    navController: NavHostController
) {
    var hike by remember { mutableStateOf<Hike?>(null) }
    var participants by remember { mutableStateOf(mapOf<String, Profile>()) }
    var userStatus by remember { mutableStateOf(Status.NOT_READY) }
    var readyCount by remember { mutableStateOf(0) }

    val userId = hikeService.getCurrentUserId()

    LaunchedEffect(hikeId) {
        hikeService.getHikeById(hikeId) { fetchedHike ->
            hike = fetchedHike
        }
    }

    LaunchedEffect(hikeId) {
        hikeService.getParticipants(hikeId) { fetchedParticipants ->
            participants = fetchedParticipants
            userStatus = fetchedParticipants[userId]?.hikeStatuses?.find { it.hikeId == hikeId }?.status ?: Status.NOT_READY
            readyCount = fetchedParticipants.count { it.value.hikeStatuses.find { it.hikeId == hikeId }?.status == Status.READY }
        }
    }

    LaunchedEffect(hikeId) {
        hikeService.getHikeById(hikeId) { fetchedHike ->
            hike = fetchedHike
            // Navigate to the next screen if the hike starts
            if (fetchedHike?.status == HikeStatus.IN_PROGRESS) {
                navController.navigate("hike_info/${hikeId}/start")
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Confirm your readiness, ($readyCount/${participants.size} ready)")
        Button(
            onClick = {
                hikeService.updateParticipantStatus(hikeId, userId, Status.READY)
            },
            enabled = userStatus != Status.READY
        ) {
            Text(if (userStatus == Status.READY) "Waiting for others" else "Confirm")
        }
    }
}
