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
import com.example.dreamsync.data.models.participantStatus
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService

@Composable
fun ConfirmationScreen(
    hikeId: String,
    hikeService: HikeService,
    profileService: ProfileService,  // Needed to fetch participant profiles
    navController: NavHostController,
    loggedUser: Profile
) {
    var hike by remember { mutableStateOf<Hike?>(null) }
    var participants by remember { mutableStateOf(mapOf<String, Profile>()) }
    var userStatus by remember { mutableStateOf(participantStatus.NOT_READY) }
    var readyCount by remember { mutableStateOf(0) }

    val userId = loggedUser.id

    // Fetch hike details
    LaunchedEffect(hikeId) {
        hikeService.getHikeById(hikeId) { fetchedHike ->
            hike = fetchedHike
        }
    }

    // Fetch participants and their profiles
    LaunchedEffect(hikeId) {
        hikeService.getParticipants(hikeId) { participantIds ->
            val participantProfiles = mutableMapOf<String, Profile>()

            participantIds.forEach { participantId ->
                profileService.getProfileById(participantId) { profile : Profile ->
                    participantProfiles[participantId] = profile
                    participants = participantProfiles.toMap()


                    userStatus = participants[userId]?.hikeStatuses?.find { it.hikeId == hikeId }?.status ?: participantStatus.NOT_READY
                    readyCount = participants.count { it.value.hikeStatuses.find { it.hikeId == hikeId }?.status == participantStatus.READY }
                }
            }
        }
    }

    // Navigate to the hike start screen if the hike starts
    LaunchedEffect(hikeId) {
        hikeService.getHikeById(hikeId) { fetchedHike ->
            if (fetchedHike?.status == HikeStatus.IN_PROGRESS) {
                navController.navigate("hike_info/${hikeId}/start")
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Confirm your readiness, ($readyCount/${participants.size} ready)")
        Button(
            onClick = {
                hikeService.updateParticipantStatus(hikeId, userId, participantStatus.READY)
                userStatus = participantStatus.READY // Update UI state immediately
            },
            enabled = userStatus != participantStatus.READY
        ) {
            Text(if (userStatus == participantStatus.READY) "Waiting for others" else "Confirm")
        }
    }
}
