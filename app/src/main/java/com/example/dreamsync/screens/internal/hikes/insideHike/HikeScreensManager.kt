package com.example.dreamsync.screens.internal.hikes.insideHike

import InLayerScreen
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.HikeStatus
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService
import kotlinx.coroutines.delay

const val CIRCLE_SIZE = 300
const val WAITING_FOR_OTHERS_TIME = 5
const val ENTERING_LAYER_TIME = 3

enum class HikeStage {
    WAITING_FOR_OTHERS,
    ENTERING_OR_LEAVING_LAYER,
    IN_LAYER,
    HIKE_COMPLETE
}

@Composable
fun HikeScreensManager(
    hikeId : String,
    hikeService: HikeService = HikeService(),
    profileService: ProfileService = ProfileService(),
    onBackToHome : () -> Unit = {}
) {
    var stage by remember { mutableStateOf(HikeStage.WAITING_FOR_OTHERS) }
    var progress by remember { mutableFloatStateOf(0f) }
    var showEnteringLayerScreen by remember { mutableStateOf(false) }

    var waitingForOthersTimer by remember { mutableIntStateOf(WAITING_FOR_OTHERS_TIME) }
    var enteringLayerTimer by remember { mutableIntStateOf(ENTERING_LAYER_TIME) }

    var hike by remember { mutableStateOf(Hike()) }
    var invitedFriends by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var currentLayerIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        hikeService.getHikeById(hikeId) { fetchedHike ->
            if (fetchedHike != null) {
                hike = fetchedHike
                Log.d("InsideHikeScreensManager", "Fetched hike: $fetchedHike")

                // fetch the friends invited to the hike
                for (friendId in fetchedHike.invitedFriends) {
                    profileService.getProfileById(friendId) { friend ->
                        if (friend != null) {
                            invitedFriends += friend
                            Log.d("InsideHikeScreensManager", "Fetched friend: $friend")
                        }
                    }
                }
            }
        }
    }

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
            HikeStage.ENTERING_OR_LEAVING_LAYER -> {
                startStageCountdown(ENTERING_LAYER_TIME) { timeLeft ->
                    enteringLayerTimer = timeLeft
                    if (timeLeft == 0) {
                        showEnteringLayerScreen = false
                        stage = getNextStage(stage)
                    }
                }
            }
            else -> {} // No transition countdown for HIKE_COMPLETE and IN_LAYER stages
        }
    }

    LaunchedEffect(invitedFriends) {
        Log.d("InsideHikeScreensManager", "Invited friends: $invitedFriends")
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
            HikeStage.ENTERING_OR_LEAVING_LAYER -> TransitionLayerScreenWithAnimation(
                isVisible = true,
                label = String.format("Entering %s...", hike.layers[currentLayerIndex].name),
            )
            HikeStage.IN_LAYER -> InLayerScreen(
                layer = hike.layers[currentLayerIndex],
                friends = invitedFriends,
                onLeaveLayer = {
                    currentLayerIndex--
                    stage = if (currentLayerIndex >= 0) {
                        HikeStage.ENTERING_OR_LEAVING_LAYER
                    } else {
                        HikeStage.HIKE_COMPLETE
                    }

                },
                onClickNextLayer = {
                    currentLayerIndex++
                    if (currentLayerIndex < hike.layers.size) {
                        showEnteringLayerScreen = true
                        stage = HikeStage.ENTERING_OR_LEAVING_LAYER
                    } else {
                        stage = HikeStage.HIKE_COMPLETE
                    }
                }
            )
            HikeStage.HIKE_COMPLETE -> {
                HikeCompletedScreen(
                    layers = hike.layers.map { it.name to "00:30:00" },
                    friends = invitedFriends,
                    onBackToHome = {
                        hikeService.updateHike(
                            hike = hike.copy(status = HikeStatus.COMPLETED),
                            onUpdateComplete = {
                                onBackToHome()
                            }
                        )
                    }
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
        HikeStage.WAITING_FOR_OTHERS -> HikeStage.ENTERING_OR_LEAVING_LAYER
        HikeStage.ENTERING_OR_LEAVING_LAYER -> HikeStage.IN_LAYER
        HikeStage.IN_LAYER -> HikeStage.ENTERING_OR_LEAVING_LAYER
        else -> HikeStage.HIKE_COMPLETE
    }
}

@Preview
@Composable
fun HikeStagesScreenPreview() {
    HikeScreensManager("hikeId")
}