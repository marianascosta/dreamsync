package com.example.dreamsync.screens.internal.hikes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dreamsync.AppState
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.HikeStatus
import com.example.dreamsync.data.models.Layer
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.HikeService

@Composable
fun HikeDetailScreen(
    hikeService: HikeService,
    hikeId: String,
    loggedUser: Profile,
    onClickStartHike : () -> Unit = {},
    onNavigateToConfirmation : () -> Unit = {}
) {
    var hike by remember { mutableStateOf<Hike>(Hike()) }
    var isLoading by remember { mutableStateOf(true) }
    var isCreator by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hikeService.getHikeById(
            id = hikeId,
            onHikeFetched = { fetchedHike ->
                if (fetchedHike != null) {
                    hike = fetchedHike
                    isCreator = loggedUser.id == hike.createdBy //POSSIVELMENTE REFORMULAR
                }
                isLoading = false
            }
        )
    }

    if (isLoading) {
        // Show loading effect
        LoadingIndicator()
    } else {
        // Show the timeline screen with layers
        TimelineScreen(layers = hike.layers, isCreator = isCreator, onClickStartHike = onClickStartHike, hike = hike, onNavigateToConfirmation = onNavigateToConfirmation)
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun TimelineScreen(
    layers: List<Layer>,
    isCreator: Boolean,
    onClickStartHike: () -> Unit = {},
    hike: Hike,
    onNavigateToConfirmation: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hike Layers", style = MaterialTheme.typography.headlineSmall)

            if (isCreator) {
                Button(
                    onClick = onClickStartHike,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8DB600)),
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = "Play Icon",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Hike", style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            } else if (hike.status == HikeStatus.WAITING) {
                Button(
                    onClick = onNavigateToConfirmation,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8DB600)),
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = "Play Icon",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Join Hike", style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }

        }


        if (layers.isEmpty()) {
            // Show empty message if no layers
            Text("No layers available.", style = MaterialTheme.typography.bodyMedium)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                layers.forEachIndexed { index, _ ->
                    val isFirstItem = index == 0
                    val isLastItem = index == layers.size - 1
                    TimelineItem(
                        label = layers[index].startDate,
                        layer = layers[index],
                        isFirstItem = isFirstItem,
                        isLastItem = isLastItem
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineItem(
    label: String,
    layer: Layer,
    isFirstItem: Boolean = false,
    isLastItem: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
            )

            if (!isLastItem) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    if (isFirstItem) {
                        Text("Start Date: $label", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        Text("Kick Date: $label", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(layer.name, style = MaterialTheme.typography.bodyLarge)
                    Text(layer.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Difficulty: ${layer.difficulty}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}