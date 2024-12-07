package com.example.dreamsync.screens

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

@Composable
fun ProfileScreen(
    profile: Profile,
    onNavigateToFriendsList: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Optional padding for aesthetics
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween, // Ensures content spacing
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Centered text
            Text(text = "Profile for ${profile.name}", modifier = Modifier.align(Alignment.CenterHorizontally))

            // Button at the bottom
            Button(
                onClick = { onNavigateToFriendsList() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Go to Friends List")
            }
        }
    }
}