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
import androidx.compose.ui.unit.dp
import com.example.dreamsync.R

@Composable
fun ProfileScreen(
    profile: Profile,
    roles: List<String>,
    onNavigateToFriendsScreen: () -> Unit,
    onRoleSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(profile.preferredRole) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture (Dynamic based on profilePicture field)
            val profilePicResource = if (profile.profilePicture.isNotEmpty()) {
                // Replace with dynamic URL or resource reference if available
                profile.profilePicture
            } else {
                "defaultprofilepic" // Default if no profile picture is set
            }

            Image(
                painter = painterResource(id = R.drawable.defaultprofilepic), // Default image
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp) // Size of the image
                    .padding(bottom = 16.dp) // Padding to give space
                    .clip(CircleShape) // Circular shape for profile picture
            )

            // Displaying profile details
            Text(text = "Profile for ${profile.userName}", modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(text = "Email: ${profile.userEmail.ifEmpty { "N/A" }}", modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(text = "Bio: ${profile.userBio.ifEmpty { "No bio available." }}", modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(text = "Preferred Role: ${selectedRole.ifEmpty { "None" }}", modifier = Modifier.align(Alignment.CenterHorizontally))

            // Dropdown Menu for roles
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Button(onClick = { expanded = !expanded }) {
                    Text(text = selectedRole.ifEmpty { "Choose Role" })
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                selectedRole = role // Update UI
                                expanded = false // Close dropdown
                                onRoleSelected(role) // Notify parent
                            }
                        )
                    }
                }
            }

            // Button to navigate to Friends Screen
            Button(
                onClick = { onNavigateToFriendsScreen() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
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

