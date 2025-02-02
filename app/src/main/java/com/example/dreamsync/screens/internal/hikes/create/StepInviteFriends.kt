package com.example.dreamsync.screens.internal.hikes.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dreamsync.AppState
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.ParticipantStatus
import com.example.dreamsync.data.models.ParticipantStatusEntry
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.ProfileService
import com.example.dreamsync.screens.internal.hikes.insideHike.HikeStage

@Composable
fun StepInviteFriends(
    hike: Hike,
    profileService: ProfileService,
    onClickFinish: (Hike) -> Unit
) {
    var friends by remember { mutableStateOf(emptyList<Profile>()) }
    var selectedFriends by remember { mutableStateOf(hike.invitedFriends + hike.createdBy) }
    //selectedFriends.add(hike.createdBy)
    LaunchedEffect(Unit) {
        profileService.getFriendsList(AppState.loggedInUser.value.id) { fetchedFriends ->
            friends = fetchedFriends
        }
    }
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column (
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            friends.forEach { friend ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = friend.userName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = selectedFriends.contains(friend.id),
                            onCheckedChange = { isChecked ->
                                selectedFriends = if (isChecked) {
                                    selectedFriends + friend.id
                                } else {
                                    selectedFriends - friend.id
                                }
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                val participantStatus = selectedFriends.map { friendId ->
                    if (friendId == hike.createdBy) {
                        return@map ParticipantStatusEntry(id = friendId, participation = ParticipantStatus.READY, kicked = false)
                    } else {
                        return@map ParticipantStatusEntry(id = friendId, participation = ParticipantStatus.NOT_READY, kicked = false)
                    }
                }
                val stage = HikeStage.NOT_STARTED
                onClickFinish(
                hike.copy(invitedFriends = selectedFriends, participantStatus = participantStatus, stage = stage, currentLayerIndex = 0)
            ) },
            modifier = Modifier.align(Alignment.End).padding(bottom = 16.dp)
        ) {
            Text("Create Hike")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewStepInviteFriends() {
    val sampleHike = Hike(
        id = "1",
        name = "Sample Hike",
        description = "This is a sample description.",
        isComplete = false,
        createdBy = "user_1",
        invitedFriends = listOf("friend_2")
    )

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

    val onUpdateHike: (Hike) -> Unit = { updatedHike ->
        println("Updated hike: $updatedHike")
    }

    StepInviteFriends(hike = sampleHike, profileService = mockProfileService, onClickFinish = onUpdateHike)
}