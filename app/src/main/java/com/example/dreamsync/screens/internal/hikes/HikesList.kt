package com.example.dreamsync.screens.internal.hikes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.services.HikeService

@Composable
fun HikesListScreen(
    profileId: String,
    hikeService: HikeService,
    onHikeClicked: (Hike) -> Unit
) {
    var hikes by remember { mutableStateOf(emptyList<Hike>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        hikeService.getHikesByCreatedBy(
            userId = profileId,
            onHikesFetched = { fetchedHikes ->
                hikes = fetchedHikes
                isLoading = false
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxWidth(), strokeWidth = 4.dp)
        } else if (hikes.isEmpty()) {
            Text(
                text = "User has no hikes.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Gray color
            )
        } else {
            hikes.forEach { hike ->
                HikeCard(hike = hike, onClick = { onHikeClicked(hike) })
            }
        }
    }
}

@Composable
fun HikeCard(hike: Hike, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${hike.name}")
            Text(text = "Description: ${hike.description}")
            Text(text = "Number of layers: ${hike.layers.size}")
            Text(text = "Number of friends invited: ${hike.invitedFriends.size}")
            Text(text = "Complete: ${if (hike.isComplete) "Yes" else "No"}")
        }
    }
}