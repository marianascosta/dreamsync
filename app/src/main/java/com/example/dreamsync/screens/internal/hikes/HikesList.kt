package com.example.dreamsync.screens.internal.hikes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    LaunchedEffect(profileId) {
        hikeService.getHikesByUser(
            userId = profileId,
            onHikesFetched = { fetchedHikes ->
                hikes = fetchedHikes
                isLoading = false
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    strokeWidth = 4.dp
                )
            }
            hikes.isEmpty() -> {
                Text(
                    text = "User has no hikes.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            else -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    for (hike in hikes) {
                        HikeCard(hike = hike, onClick = { onHikeClicked(hike) })
                    }
                }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Name: ${hike.name}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Description: ${hike.description}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Number of layers: ${hike.layers.size}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Number of friends invited: ${hike.invitedFriends.size}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Complete: ${if (hike.isComplete) "Yes" else "No"}",
                style = MaterialTheme.typography.bodySmall,
                color = if (hike.isComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
