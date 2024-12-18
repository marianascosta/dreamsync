package com.example.dreamsync.screens.internal.hikes

import android.R.attr.text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamsync.AppState
import com.example.dreamsync.data.initialization.hikes
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.services.HikeService

@Composable
fun HikesListScreen(
    hikeService: HikeService,
    onHikeClicked: (Hike) -> Unit
) {

    var hikes by remember { mutableStateOf(emptyList<Hike>()) }

    LaunchedEffect(Unit) {
        hikeService.getHikesByCreatedBy(
            AppState.loggedInUser.value.id,
            onHikesFetched = { fetchedHikes ->
                hikes = fetchedHikes
            }
        )
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        hikes.forEach { hike ->
            HikeCard(hike = hike, onClick = { onHikeClicked(hike) })
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