package com.example.dreamsync.screens.internal.hikes.insideHike

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.example.dreamsync.data.models.ParticipantStatus
import com.example.dreamsync.data.models.ParticipantStatusEntry
import com.example.dreamsync.data.models.Profile
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
    var allReady by remember { mutableStateOf(false) }
    var participantStatuses by remember { mutableStateOf(emptyList<ParticipantStatusEntry>()) }
    var readyCount by remember { mutableStateOf(0) }
    var totalParticipants by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        hikeService.observeParticipantStatus(hikeId) { statuses ->
//            Log.d("ParticipantStatus", "In waiting Statuses updated: $statuses")
//            participantStatuses = statuses//.toList()
            readyCount = statuses.count { it.participation == ParticipantStatus.READY } + 1
            totalParticipants = statuses.size + 1
//            allReady = readyCount == totalParticipants
            participantStatuses = statuses
            allReady = statuses.all { it.participation == ParticipantStatus.READY }
            Log.d("ParticipantStatus", "Total: ${statuses.size}, Ready: ${statuses.count { it.participation == ParticipantStatus.READY }}")

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
            Text(text = "Waiting for participants to confirm...")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "$readyCount / $totalParticipants ready")
            Spacer(modifier = Modifier.height(16.dp))

            if (allReady) {
                Button(onClick = { onStartHike() }) {
                    Text(text = "Start Hike")
                }
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
