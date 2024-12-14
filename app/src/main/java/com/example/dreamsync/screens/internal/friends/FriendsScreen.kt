package com.example.dreamsync.screens.internal.friends

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
fun FriendsScreen(
    onNavigateToProfile: () -> Unit
) {

    Column {
        Text(text = "Friends Screen", modifier = Modifier.align(Alignment.CenterHorizontally))
        Button(
            onClick = { onNavigateToProfile() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Go to Profile")
        }
    }
}