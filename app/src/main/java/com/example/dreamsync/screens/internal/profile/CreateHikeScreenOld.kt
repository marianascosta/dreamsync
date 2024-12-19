package com.example.dreamsync.screens.internal.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.dreamsync.AppState
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService


@Composable
fun CreateHikeScreenOld(
    onHikeCreated: (Hike) -> Unit,
    hikeService: HikeService,
    profileService: ProfileService,
) {

    var hikeName by remember { mutableStateOf("") }
    var hikeDescription by remember { mutableStateOf("") }
    var selectedLayer by remember { mutableIntStateOf(1) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var friends by remember { mutableStateOf(emptyList<Profile>()) }
    var selectedFriendsIds by remember { mutableStateOf(emptyList<String>()) }

    val layers = (1..10).toList()
    val context = LocalContext.current //for toast

    LaunchedEffect(Unit) {
        profileService.getFriendsList(
            profileId = AppState.loggedInUser.value.id,
            onFriendsFetched = { friendsFetched ->
                friends = friendsFetched
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create a Dream Hike",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        OutlinedTextField(
            value = hikeName,
            onValueChange = { hikeName = it },
            label = { Text("Hike Name") },
            placeholder = { Text("Enter the name of your dream hike") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = hikeDescription,
            onValueChange = { hikeDescription = it },
            label = { Text("Hike Description") },
            placeholder = { Text("Enter a brief description of the hike") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Number of Layers: $selectedLayer",
                style = MaterialTheme.typography.bodyLarge
            )

            Box {
                Button(onClick = { dropdownExpanded = true }) {
                    Text(text = "Select Layers")
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    layers.forEach { layer ->
                        DropdownMenuItem(
                            text = { Text(layer.toString()) },
                            onClick = {
                                selectedLayer = layer
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
        Text(
            text = "Who's joining the Hike?",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            LazyColumn {
                items(friends.size) { friendIndex ->
                    val friend = friends[friendIndex]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(friend.userName, style = MaterialTheme.typography.bodyLarge)
                        Checkbox(
                            checked = selectedFriendsIds.contains(friend.id),
                            onCheckedChange = { isChecked ->
                                selectedFriendsIds = if (isChecked) {
                                    selectedFriendsIds + friend.id
                                } else {
                                    selectedFriendsIds - friend.id
                                }
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (hikeName.isNotEmpty() && hikeDescription.isNotEmpty()) {
                    val newHike = Hike(
                        name = hikeName,
                        description = hikeDescription,
                        isComplete = false,
                        createdBy = AppState.loggedInUser.value.id,
                        invitedFriends = selectedFriendsIds
                    )

                    hikeService.saveHike(newHike) { success ->
                        if (success) {
                            Toast.makeText(context, "Hike Created!", Toast.LENGTH_SHORT).show()
                            onHikeCreated(newHike)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Hike")
        }
    }
}
