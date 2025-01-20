package com.example.dreamsync.screens.internal.profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

private val DEFAULT_ROLE = Role.Dreamer

@Composable
fun ProfileScreen(
    profileService: ProfileService,
    profileId: String,
    onNavigateToCreateHikeScreen: () -> Unit,
    onNavigateToHikeInfoScreen: (Hike) -> Unit,
    onHikeCreated: (Hike) -> Unit,
    onRoleSelected: (Role) -> Unit,
    onProfileUpdated: (Profile) -> Unit,
    hikeService: HikeService,
    onHikeClicked: (Hike) -> Unit
) {
    var profile by remember { mutableStateOf<Profile?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }
    var newBio by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<Role?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }

    LaunchedEffect(profileId) {
        profileService.getProfileById(
            profileId = profileId,
            onProfileFetched = { fetchedProfile ->
                if (fetchedProfile != null) {
                    profile = fetchedProfile
                    newEmail = fetchedProfile.userEmail
                    newBio = fetchedProfile.userBio
                    selectedRole = fetchedProfile.preferredRole
                    selectedImageUri = fetchedProfile.profilePicture
                    errorMessage = null
                } else {
                    errorMessage = "Profile not found."
                }
                isLoading = false
            }
        )
    }

    val isCurrentUserProfile = AppState.loggedInUser.collectAsState().value.id == profile?.id

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(errorMessage!!, style = MaterialTheme.typography.headlineSmall)
        } else if (profile != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImageSection(
                    profile = profile!!,
                    isEditing = isEditing,
                    selectedImageUri = selectedImageUri,
                    onImageSelected = { imagePickerLauncher.launch("image/*") }
                )

                ProfileInfoCard(
                    profile = profile!!,
                    isEditing = isEditing,
                    newEmail = newEmail,
                    newBio = newBio,
                    selectedRole = selectedRole,
                    isCurrentUserProfile = isCurrentUserProfile,
                    onEditToggled = {
                        if (isEditing) {
                            val updatedProfile = profile!!.copy(
                                userEmail = newEmail,
                                userBio = newBio,
                                profilePicture = selectedImageUri ?: profile!!.profilePicture,
                                preferredRole = selectedRole ?: DEFAULT_ROLE
                            )
                            profileService.updateProfile(
                                profileId = updatedProfile.id,
                                updatedProfile = updatedProfile,
                                onComplete = { isSuccessful ->
                                    if (isSuccessful) {
                                        profileService.getProfileById(
                                            profileId = updatedProfile.id,
                                            onProfileFetched = { reloadedProfile ->
                                                if (reloadedProfile != null) {
                                                    profile = reloadedProfile
                                                    onProfileUpdated(reloadedProfile)
                                                    Log.d("ProfileScreen", "Profile updated and reloaded: $reloadedProfile")
                                                } else {
                                                    Log.e("ProfileScreen", "Failed to reload updated profile.")
                                                }
                                            }
                                        )
                                    } else {
                                        Log.e("ProfileScreen", "Failed to update profile.")
                                    }
                                }
                            )
                        }
                        isEditing = !isEditing
                    },
                    onEmailChanged = { newEmail = it },
                    onBioChanged = { newBio = it },
                    onRoleChanged = { selectedRole = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${profile!!.userName}'s Dream Hikes",
                    style = MaterialTheme.typography.headlineSmall
                )

                if (isCurrentUserProfile) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                    profileId = profile!!.id,
                    hikeService = hikeService,
                    onHikeClicked = onHikeClicked
                )
            }
        }
    }
}

@Composable
fun ProfileImageSection(
    profile: Profile,
    isEditing: Boolean,
    selectedImageUri: String?,
    onImageSelected: () -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        if (isEditing && selectedImageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = "Selected Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { onImageSelected() }
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(
                    model = selectedImageUri ?: profile.profilePicture
                ),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable(enabled = isEditing) { onImageSelected() }
            )
        }

        if (isEditing) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add New Profile Picture",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
                    .clickable { onImageSelected() }
            )
        }
    }
}


@Composable
fun ProfileInfoCard(
    profile: Profile,
    isEditing: Boolean,
    newEmail: String,
    newBio: String,
    selectedRole: Role?,
    isCurrentUserProfile: Boolean,
    onEditToggled: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onBioChanged: (String) -> Unit,
    onRoleChanged: (Role) -> Unit
) {
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
                if (isCurrentUserProfile) {
                    IconButton(onClick = onEditToggled) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Save Changes" else "Edit Profile Info"
                        )
                    }
                }
            }
            if (isEditing) {
                TextField(
                    value = newEmail,
                    onValueChange = onEmailChanged,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = newBio,
                    onValueChange = onBioChanged,
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownRoleSelector(
                    selectedRole = selectedRole ?: DEFAULT_ROLE,
                    onRoleChanged = onRoleChanged
                )
            } else {
                Text("Email: ${profile.userEmail.ifEmpty { "N/A" }}")
                Text("Bio: ${profile.userBio.ifEmpty { "No bio available." }}")
                Text("Preferred Role: ${profile.preferredRole}")
            }
        }
    }
}

@Composable
fun DropdownRoleSelector(
    selectedRole: Role,
    onRoleChanged: (Role) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    val roles = Role.entries

    Box {
        Button(onClick = { dropdownExpanded = true }) {
            Text("Preferred Role: $selectedRole")
        }
        DropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
        ) {
            roles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role.name) },
                    onClick = {
                        onRoleChanged(role)
                        dropdownExpanded = false
                    }
                )
            }
        }
    }
}