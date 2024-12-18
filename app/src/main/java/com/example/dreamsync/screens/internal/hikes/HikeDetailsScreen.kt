package com.example.dreamsync.screens.internal.hikes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
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
import com.example.dreamsync.data.models.Layer
import com.example.dreamsync.data.services.HikeService

@Composable
fun HikeDetailScreen(
    hikeService: HikeService,
    hikeId: String
) {
    var hike by remember { mutableStateOf<Hike>(Hike()) }
    LaunchedEffect(Unit) {
        hikeService.getHikeById(
            id = hikeId,
            onHikeFetched = { fetchedHike ->
                if (fetchedHike != null) {
                    hike = fetchedHike
                }
            }
        )
    }
    TimelineScreen(layers = hike.layers)
}

@Composable
fun TimelineScreen(layers: List<Layer>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text("Hike Layers", style = MaterialTheme.typography.headlineMedium)

        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            layers.forEachIndexed { index, _ ->
                val isFirstItem = index == 0
                val isLastItem = index == layers.size - 1
                TimelineItem(
                    label = if (isFirstItem) layers[index].startDate else layers[index].kickDate,
                    layer = layers[index],
                    isFirstItem = isFirstItem,
                    isLastItem = isLastItem
                )
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