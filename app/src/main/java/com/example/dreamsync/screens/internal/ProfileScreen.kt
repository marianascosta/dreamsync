package com.example.dreamsync.screens.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Profile

@Composable
fun ProfileScreen(
    profile: Profile,
    onNavigateToFriendsScreen: () -> Unit,
) {
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
            // Use profile.userName instead of profile.name
            Text(text = "Profile for ${profile.userName}", modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(text = "Email: ${profile.userEmail.ifEmpty { "N/A" }}", modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(text = "Bio: ${profile.userBio.ifEmpty { "No bio available." }}", modifier = Modifier.align(Alignment.CenterHorizontally))

            Button(
                onClick = { onNavigateToFriendsScreen() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Go to Friends Screen")
            }
        }
    }
}
