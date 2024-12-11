package com.example.dreamsync.screens.internal

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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreamsync.R

@Composable
fun ProfileScreen(
    profile: Profile,
    roles: List<String>,
    onNavigateToFriendsScreen: () -> Unit,
    onRoleSelected: (String) -> Unit,
    onProfileUpdated: (Profile) -> Unit // Callback to update profile
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(profile.preferredRole) }
    var isEditing by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf(profile.userEmail) }
    var newBio by remember { mutableStateOf(profile.userBio) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp), // Space for top padding and app bar
            verticalArrangement = Arrangement.spacedBy(16.dp), // Consistent spacing between elements
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture Section
            val profilePicResource = if (profile.profilePicture.isEmpty()) {
                "defaultprofilepic" // Placeholder if no image provided
            } else {
                profile.profilePicture
            }

            Image(
                painter = painterResource(id = R.drawable.defaultprofilepic), // Default image
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp) // Larger size for better visibility
                    .clip(CircleShape) // Circular image
                    .background(MaterialTheme.colorScheme.surface) // Optional background
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "${profile.userName}'s Profile", style = MaterialTheme.typography.headlineSmall)

                    // Editable email and bio
                    if (isEditing) {
                        TextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                        TextField(
                            value = newBio,
                            onValueChange = { newBio = it },
                            label = { Text("Bio") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    } else {
                        Text(text = "Email: ${profile.userEmail.ifEmpty { "N/A" }}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Bio: ${profile.userBio.ifEmpty { "No bio available." }}", style = MaterialTheme.typography.bodyLarge)
                    }

                    Text(text = "Preferred Role: ${selectedRole.ifEmpty { "None" }}", style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Role Selection Dropdown
                    Button(onClick = { expanded = !expanded }) {
                        Text(text = "Select Role: ${selectedRole.ifEmpty { "Choose Role" }}")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = {
                                    selectedRole = role // Update selected role
                                    expanded = false // Close dropdown
                                    onRoleSelected(role) // Notify parent
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to toggle edit mode
            Button(
                onClick = {
                    if (isEditing) {
                        // Save updated profile info
                        onProfileUpdated(profile.copy(userEmail = newEmail, userBio = newBio))
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Save Changes" else "Edit Profile Info")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to navigate to Friends Screen
            Button(
                onClick = { onNavigateToFriendsScreen() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Friends Screen")
            }
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

