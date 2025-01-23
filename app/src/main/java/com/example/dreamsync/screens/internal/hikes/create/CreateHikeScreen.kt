package com.example.dreamsync.screens.internal.hikes.create

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dreamsync.AppState
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService

@Composable
fun CreateHikeScreen(
    hikeService: HikeService,
    profileService: ProfileService,
    onFinish: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val tabs = listOf("Details", "Layers", "Friends")
    var hike by remember { mutableStateOf(Hike(createdBy = AppState.loggedInUser.value.id)) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TabRow(selectedTabIndex = currentStep) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = currentStep == index,
                    onClick = { }, // must use the continue button inside each step screen
                    text = { Text(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (currentStep) {
            0 -> StepHikeDetails(hike = hike, onClickContinue = { updatedHike ->
                hike = updatedHike
                currentStep++
                Log.d("StepHikeDetails", "Hike: $hike")
            })
            1 -> StepCreateLayers(hike = hike, onClickContinue = { updatedHike ->
                hike = updatedHike
                currentStep++
                Log.d("StepCreateLayers", "Hike: $hike")
            })
            2 -> StepInviteFriends(hike = hike, profileService = profileService,
                onClickFinish = { updatedHike ->
                    hike = updatedHike
                    hikeService.saveHike(hike) { success ->
                        if (success) {
                            Toast.makeText(context, "Hike created!", Toast.LENGTH_SHORT).show()
                            onFinish()
                        } else {
                            Toast.makeText(context, "Failed to create hike", Toast.LENGTH_SHORT).show()
                            onFinish()
                        }
                    }
                }
            )

        }
    }

}

@Composable
@Preview
fun PreviewCreateHikeScreen() {
    val mockHikeService = object : HikeService() {
        override fun saveHike(hike: Hike, onComplete: (Boolean) -> Unit) {
            Log.i("MockHikeService", "Hike saved: $hike")
            onComplete(true)
        }
    }

    val mockProfileService = object : ProfileService() {
        override fun getFriendsList(profileId: String, onFriendsFetched: (List<Profile>) -> Unit) {
            onFriendsFetched(
                listOf(
                    Profile(id = "friend_1", userName = "Alice"),
                    Profile(id = "friend_2", userName = "Bob"),
                    Profile(id = "friend_3", userName = "Charlie")
                )
            )
        }
    }

    CreateHikeScreen(
        hikeService = mockHikeService,
        profileService = mockProfileService,
        onFinish = { Log.d("CreateHikeScreen", "Hike created!") }
    )
}