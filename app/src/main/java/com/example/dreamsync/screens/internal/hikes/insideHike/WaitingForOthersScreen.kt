package com.example.dreamsync.screens.internal.hikes.insideHike

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dreamsync.AppState.profileService
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.HikeStatus
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.models.participantStatus
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService


@Composable
fun WaitingForOthersScreen(
    hikeId: String,
    hikeService: HikeService,
    profileService: ProfileService,
    navController: NavController,
    loggedUser: Profile,
    onStartHike: () -> Unit
) {
    var isCreator by remember { mutableStateOf(false) }
    var hike by remember { mutableStateOf<Hike?>(null) }
    var participants by remember { mutableStateOf(mapOf<String, Profile>()) }
    var allParticipantsReady by remember { mutableStateOf(false) }
    var readyCount by remember { mutableStateOf(0) }

    LaunchedEffect(hikeId) {
        hikeService.getHikeById(hikeId) { fetchedHike ->
            hike = fetchedHike
            isCreator = fetchedHike?.createdBy == loggedUser.id
        }
    }

//    LaunchedEffect(hikeId) {
//        hikeService.getParticipants(hikeId) { fetchedParticipants ->
//            participants = fetchedParticipants
//            readyCount = participants.count { it.value.hikeStatuses.find { it.hikeId == hikeId }?.status == participantStatus.READY }
//            allParticipantsReady = readyCount == participants.size
//        }
//    }
    LaunchedEffect(hikeId) {
        hikeService.getParticipants(hikeId) { participantIds ->
            val participantProfiles = mutableMapOf<String, Profile>()

            participantIds.forEach { userId ->
                profileService.getProfileById(userId) { profile ->
                    if (profile != null) {
                        participantProfiles[userId] = profile
                        participants = participantProfiles.toMap()  // Update state
                        readyCount = participants.count { it.value.hikeStatuses.find { it.hikeId == hikeId }?.status == participantStatus.READY }
                        allParticipantsReady = readyCount == participants.size
                    }
                }
            }
        }
    }


    if (hike == null) {
        Text("Loading hike details...")
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Waiting for Participants... ($readyCount/${participants.size} ready)")
            participants.forEach { (id, participant) ->
                val statusForHike = participant.hikeStatuses.find { it.hikeId == hikeId }?.status ?: participantStatus.NOT_READY
                Text("${participant.userName}: ${statusForHike.name}")
            }

            if (isCreator && allParticipantsReady) {
                Button(onClick = {
                    hikeService.updateHikeStatus(hikeId, HikeStatus.IN_PROGRESS)
                    onStartHike()
                }) {
                    Text("Start Hike")
                }
            } else {
                Button(enabled = false, onClick = {}) {
                    Text("Waiting for participants")
                }
            }
        }
    }

    LaunchedEffect(hikeId) {
        hikeService.getHikeById(hikeId) { fetchedHike ->
            hike = fetchedHike
            // Listen for hike status changes
            if (fetchedHike?.status == HikeStatus.IN_PROGRESS) {
                navController.navigate("hike_info/${hikeId}/start")
            }
        }
    }
}


@Composable
fun CircularTimerWithInnerContent(
    progress: Float,
    currentTime: Int,
    label: String,
    information: String = "",
    isButtonVisible: Boolean = false,
    buttonLabel: String = "Continue",
    onClickButton: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        CircularTimer(progress = progress, modifier = Modifier.size(CIRCLE_SIZE.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Text(
                text = "${currentTime}s",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            if (information.isNotEmpty()) {
                Text(
                    text = information,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    color = Color.Gray
                )
            }

            if (isButtonVisible) {
                Button(
                    onClick = { onClickButton() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39CCCC)),
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(buttonLabel, style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CircularTimer(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        val sizeOffset = strokeWidth / 2
        val arcSize = Size(size.width - sizeOffset, size.height - sizeOffset)

        drawCircle(
            color = Color.Gray.copy(alpha = 0.2f),
            radius = size.minDimension / 2 - sizeOffset,
            style = Stroke(strokeWidth)
        )
        drawArc(
            color = Color(0xFF39CCCC),
            startAngle = -90f,
            sweepAngle = 360 * progress,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(sizeOffset, sizeOffset),
            size = arcSize
        )
    }
}
