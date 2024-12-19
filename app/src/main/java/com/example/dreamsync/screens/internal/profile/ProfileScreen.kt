package com.example.dreamsync.screens.internal.profile

import android.R.attr.contentDescription
import android.util.Log
import android.util.Log.i
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Profile
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.dreamsync.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import com.example.dreamsync.AppState
import com.example.dreamsync.data.initialization.hikes
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.Role
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService
import com.example.dreamsync.screens.internal.hikes.HikesListScreen

@Composable
fun ProfileScreen(
    profileService: ProfileService,
    profileId : String,
    onNavigateToCreateHikeScreen: () -> Unit,
    onNavigateToHikeInfoScreen: (Hike) -> Unit,
    onHikeCreated: (Hike) -> Unit,
    onRoleSelected: (Role) -> Unit,
    onProfileUpdated: (Profile) -> Unit,
    hikeService: HikeService,
    onHikeClicked: (Hike) -> Unit
) {
    var profile by remember { mutableStateOf(Profile()) }
    var expanded by remember { mutableStateOf(false) }
    var updatedProfile by remember { mutableStateOf(profile) }
    var selectedRole by remember { mutableStateOf(profile.preferredRole) }
    var isEditing by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf(profile.userEmail) }
    var newBio by remember { mutableStateOf(profile.userBio) }
    val roles = Role.entries

    LaunchedEffect(Unit) {
        profileService.getProfileById(
            profileId = profileId,
            onProfileFetched = { fetchedProfile ->
                profile = fetchedProfile!!
                updatedProfile = fetchedProfile
            }
        )
    }

    Log.i("ProfileScreen", "Profile: $profile")

    // se o perfil nao for do utilizador logged in, limitar algumas funcionalidades
    val isCurrentUserProfile = AppState.loggedInUser.collectAsState().value.id == profile.id

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!profile.profilePicture.isEmpty()){
                Image(
                    painter = rememberAsyncImagePainter(profile.profilePicture),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${profile.userName}'s Profile",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        IconButton(
                            onClick = {
                                if (isEditing) {
                                    onProfileUpdated(
                                        profile.copy(
                                            userEmail = newEmail,
                                            userBio = newBio,
                                            preferredRole = selectedRole
                                        )
                                    )
                                }
                                isEditing = !isEditing
                            }
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = if (isEditing) "Save Changes" else "Edit Profile Info"
                            )
                        }
                    }
                    if (isEditing) {
                        TextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                        TextField(
                            value = newBio,
                            onValueChange = { newBio = it },
                            label = { Text("Bio") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                        var roleDropdownExpanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Button(
                                onClick = { roleDropdownExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Preferred Role: $selectedRole")
                            }
                            DropdownMenu(
                                expanded = roleDropdownExpanded,
                                onDismissRequest = { roleDropdownExpanded = false }
                            ) {
                                roles.forEach { role ->
                                    HighlightDropMenuItem(
                                        onClick = {
                                            selectedRole = role
                                            roleDropdownExpanded = false
                                        },
                                        text = role.name
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Email: ${profile.userEmail.ifEmpty { "N/A" }}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Bio: ${profile.userBio.ifEmpty { "No bio available." }}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Preferred Role: $selectedRole",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${profile.userName}'s Dream Hikes",
                style = MaterialTheme.typography.headlineSmall
            )

            if (isCurrentUserProfile) {
                Row {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Create Dream Hike",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onNavigateToCreateHikeScreen() },
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Create Dream Hike",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }

            HikesListScreen(
                profileId = AppState.loggedInUser.collectAsState().value.id, //TODO FIX THIS
                hikeService = hikeService,
                onHikeClicked = onHikeClicked
            )
        }
    }
}

@Composable
fun HighlightDropMenuItem(
    text: String,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    DropdownMenuItem(
        modifier = Modifier
            .background(if (isHovered) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHovered = true
                        tryAwaitRelease()
                        isHovered = false
                    }
                )
            },
        onClick = onClick,
        text = { Text(text) }
    )
}