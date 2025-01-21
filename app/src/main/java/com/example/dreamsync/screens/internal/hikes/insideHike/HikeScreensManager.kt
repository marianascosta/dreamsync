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
import androidx.navigation.NavController
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
import kotlinx.coroutines.delay

const val CIRCLE_SIZE = 300
const val WAITING_FOR_OTHERS_TIME = 5
const val ENTERING_LAYER_TIME = 3

enum class HikeStage {
    NOT_STARTED,
    WAITING_FOR_OTHERS,
    ENTERING_OR_LEAVING_LAYER,
    IN_LAYER,
    HIKE_COMPLETE
}

@Composable
fun HikeScreensManager(
    hikeId: String,
    hikeService: HikeService,
    navController: NavController,
    loggedUser: Profile,
    profileService: ProfileService,
    onBackToHome: () -> Unit = {},
    onStartHike: () -> Unit = {}
) {
    var stage by remember { mutableStateOf(HikeStage.WAITING_FOR_OTHERS) }
    var hike by remember { mutableStateOf(Hike()) }
    var invitedFriends by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var currentLayerIndex by remember { mutableIntStateOf(0) }
    var readyCount by remember { mutableStateOf(0) }
    var allParticipantsReady by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isCreator by remember { mutableStateOf(true) }

    // Load hike data and create friends list
    LaunchedEffect(Unit) {
        hikeService.getHikeById(hikeId) { fetchedHike ->
            if (fetchedHike != null) {
                hike = fetchedHike
                isLoading = false
                isCreator = fetchedHike.createdBy == loggedUser.id
                fetchedHike.invitedFriends.forEach { friendId ->
                    profileService.getProfileById(friendId) { friend ->
                        if (friend != null && friend.id != loggedUser.id) {
                            invitedFriends += friend
                        }
                    }
                }
                val creator = fetchedHike.createdBy
                profileService.getProfileById(creator) { creatorProfile ->
                    if (loggedUser != creatorProfile) {
                        invitedFriends += creatorProfile
                    }
                }
            }
        }
    }
    //Track Layer
    LaunchedEffect(hikeId) {
        val layerIndexRef = FirebaseDatabase.getInstance().getReference("hikes").child(hikeId).child("currentLayerIndex")

        layerIndexRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedIndex = snapshot.getValue(Int::class.java)
                if (updatedIndex != null) {
                    currentLayerIndex = updatedIndex
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HikeDebug", "Failed to listen for layer index updates: ", error.toException())
            }
        })
    }
    //Track Ready Status
    LaunchedEffect(hike.participantStatus) {
        readyCount = hike.participantStatus.count { it.participation == ParticipantStatus.READY }
        allParticipantsReady = readyCount == hike.participantStatus.size
    }
    //Track Stage
    LaunchedEffect(hikeId) {
        val hikeStageRef = FirebaseDatabase.getInstance().getReference("hikes").child(hikeId).child("stage")

        hikeStageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the stage field exists and update it
                val updatedStage = snapshot.getValue(String::class.java)
                if (updatedStage != null) {
                    // Sync the stage from the Firebase data
                    stage = HikeStage.valueOf(updatedStage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HikeDebug", "Failed to listen for stage updates: ", error.toException())
            }
        })
    }

    // Handle stage transitions
    LaunchedEffect(stage) {
        when (stage) {
            HikeStage.ENTERING_OR_LEAVING_LAYER -> {
                startStageCountdown(ENTERING_LAYER_TIME) { timeLeft ->
                    if (timeLeft == 0) {
                        stage = getNextStage(stage)
                        hikeService.updateHikeStage(hikeId, stage)
                    }
                }
            }
            HikeStage.IN_LAYER -> {
                hikeService.updateParticipantStatus(hikeId, loggedUser.id, ParticipantStatus.NOT_READY)
            }
            else -> {}
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
            HikeStage.WAITING_FOR_OTHERS ->
                if(isCreator) {
                    WaitingForOthersScreen(
                        hikeId, hikeService, profileService, navController, loggedUser,
                        onStartHike = {
                            hikeService.updateHikeStage(hikeId, HikeStage.ENTERING_OR_LEAVING_LAYER)
                            stage = HikeStage.ENTERING_OR_LEAVING_LAYER
                        }
                    )
                } else {
                    ConfirmationScreen(hikeId, hikeService, loggedUser)
                }
            HikeStage.ENTERING_OR_LEAVING_LAYER -> TransitionLayerScreenWithAnimation(
                isVisible = true,
                label = "Entering ${hike.layers[currentLayerIndex].name}...",
            )
            HikeStage.IN_LAYER -> InLayerScreen(
                layer = hike.layers[currentLayerIndex],
                friends = invitedFriends,
                loggedUser = loggedUser,
                isCreator = isCreator,
                onLeaveLayer = {
                    currentLayerIndex--
                    hikeService.updateCurrentLayerIndex(hikeId, currentLayerIndex)
                    hikeService.updateHikeStage(hikeId, HikeStage.WAITING_FOR_OTHERS)
                    stage = HikeStage.WAITING_FOR_OTHERS
                    stage = if (currentLayerIndex >= 0) HikeStage.ENTERING_OR_LEAVING_LAYER else HikeStage.HIKE_COMPLETE
                },
                onClickNextLayer = {
                    currentLayerIndex++
                    hikeService.updateCurrentLayerIndex(hikeId, currentLayerIndex)
                    hikeService.updateHikeStage(hikeId, HikeStage.WAITING_FOR_OTHERS)
                    stage = HikeStage.WAITING_FOR_OTHERS
                    if (loggedUser.id == hike.createdBy && allParticipantsReady) {
                        if (currentLayerIndex < hike.layers.size) {
                            stage = HikeStage.WAITING_FOR_OTHERS
                        } else {
                            stage = HikeStage.HIKE_COMPLETE
                        }
                    }
                }
            )
            HikeStage.HIKE_COMPLETE -> HikeCompletedScreen(
                layers = hike.layers.map { it.name to "00:30:00" },
                friends = invitedFriends,
                onBackToHome = {
                    hikeService.updateHike(
                        hike.copy(status = HikeStatus.COMPLETED)
                    ) { onBackToHome() }
                }
            )

            HikeStage.NOT_STARTED -> {}
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
