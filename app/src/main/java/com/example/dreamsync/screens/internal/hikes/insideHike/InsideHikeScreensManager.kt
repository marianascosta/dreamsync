package com.example.dreamsync.screens.internal.hikes.insideHike

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

const val CIRCLE_SIZE = 300
const val WAITING_FOR_OTHERS_TIME = 10
const val ENTERING_LAYER_TIME = 3
const val LEAVING_LAYER_TIME = 3

enum class HikeStage {
    WAITING_FOR_OTHERS,
    ENTERING_LAYER,
    IN_LAYER,
    LEAVING_LAYER,
    HIKE_COMPLETE
}

@Composable
fun InsideHikeScreensManager(
    hikeId : String
) {
    var stage by remember { mutableStateOf(HikeStage.WAITING_FOR_OTHERS) }
    var progress by remember { mutableStateOf(0f) }
    var showEnteringLayerScreen by remember { mutableStateOf(false) }

    var waitingForOthersTimer by remember { mutableStateOf(WAITING_FOR_OTHERS_TIME) }
    var enteringLayerTimer by remember { mutableStateOf(ENTERING_LAYER_TIME) }
    var leavingLayerTimer by remember { mutableStateOf(LEAVING_LAYER_TIME) }

    LaunchedEffect(stage) {
        when (stage) {
            HikeStage.WAITING_FOR_OTHERS -> {
                startStageCountdown(WAITING_FOR_OTHERS_TIME) { timeLeft ->
                    waitingForOthersTimer = timeLeft
                    progress = 1f - timeLeft / WAITING_FOR_OTHERS_TIME.toFloat()
                    if (timeLeft == 0) {
                        stage = getNextStage(stage)
                    }
                }
            }
            HikeStage.ENTERING_LAYER -> {
                startStageCountdown(ENTERING_LAYER_TIME) { timeLeft ->
                    enteringLayerTimer = timeLeft
                    if (timeLeft == 0) {
                        showEnteringLayerScreen = false
                        stage = getNextStage(stage)
                    }
                }
            }
            HikeStage.LEAVING_LAYER -> {
                startStageCountdown(LEAVING_LAYER_TIME) { timeLeft ->
                    leavingLayerTimer = timeLeft
                    if (timeLeft == 0) {
                        stage = getNextStage(stage)
                    }
                }
            }
            else -> {} // No transition countdown for HIKE_COMPLETE and IN_LAYER stages
        }
    }

    // UI layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (stage) {
            HikeStage.WAITING_FOR_OTHERS -> WaitingForOthersScreen(progress, waitingForOthersTimer)
            HikeStage.ENTERING_LAYER -> TransitionLayerScreenWithAnimation(
                isVisible = true,
                label = "Entering the layer..."
            )
            else -> {
                // Show other stages
                Text(
                    text = "Stage: $stage",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 24.sp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


suspend fun startStageCountdown(initialTime: Int, onTick: (Int) -> Unit) {
    var timeLeft = initialTime
    while (timeLeft > 0) {
        delay(1000L)
        timeLeft--
        onTick(timeLeft)
    }
}

fun getNextStage(currentStage: HikeStage): HikeStage {
    return when (currentStage) {
        HikeStage.WAITING_FOR_OTHERS -> HikeStage.ENTERING_LAYER
        HikeStage.ENTERING_LAYER -> HikeStage.IN_LAYER
        HikeStage.IN_LAYER -> HikeStage.LEAVING_LAYER
        HikeStage.LEAVING_LAYER -> HikeStage.HIKE_COMPLETE
        HikeStage.HIKE_COMPLETE -> HikeStage.WAITING_FOR_OTHERS // Loop back to waiting for others
    }
}

@Preview
@Composable
fun HikeStagesScreenPreview() {
    InsideHikeScreensManager("1")
}