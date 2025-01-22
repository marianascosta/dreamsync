package com.example.dreamsync.screens.internal.home

import android.util.Log
import android.util.Log.d
import com.example.dreamsync.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dreamsync.AppState.loggedInUser
import com.example.dreamsync.data.models.Dream
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.DreamService
import com.example.dreamsync.data.services.ProfileService

@Composable
fun DreamFeedScreen(dreamService: DreamService) {

    val dreams = remember { mutableStateOf<List<Dream>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            dreamService.getDreamsList { fetchedDreams ->
                dreams.value = fetchedDreams
                isLoading.value = false
            }
        } catch (e: Exception) {
            isLoading.value = false
            errorMessage.value = "Failed to load dreams. Please try again later."
        }
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else if (errorMessage.value != null) {
        Text("Error: ${errorMessage.value}", color = Color.Red, textAlign = TextAlign.Center)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            dreams.value.forEach { dream ->
                DreamPost(
                    dream = dream
                )
            }
        }
    }
}


@Composable
fun DreamPost(
    dream: Dream,
    dreamService: DreamService = DreamService()
) {
    var isLiked by remember { mutableStateOf(dream.likedByProfiles.contains(loggedInUser.value.id)) }
    var amountLikes by remember { mutableIntStateOf(dream.likedByProfiles.size) }

    fun onLikeButtonClicked() {
        if (dream.likedByProfiles.contains(loggedInUser.value.id)) {
            dream.likedByProfiles = dream.likedByProfiles.toMutableList().apply {
                remove(loggedInUser.value.id)
            }
        } else {
            dream.likedByProfiles = dream.likedByProfiles.toMutableList().apply {
                add(loggedInUser.value.id)
            }
        }
        amountLikes = dream.likedByProfiles.size
        isLiked = dream.likedByProfiles.contains(loggedInUser.value.id)
        dreamService.updateDream(dream.id, dream)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Gray)
                )

                Text(
                    text = "John Doe",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Image(
                painter = painterResource(id = R.drawable.love_stars), //TODO temporary fix the resource cant be the id because thats updated with the db and the actual ids depend on each build
                contentDescription = "${dream.title} Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = dream.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = dream.description,
                style = MaterialTheme.typography.bodyMedium,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Time: 10:00 PM",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )

                Text(
                    text = "Location: New York",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { onLikeButtonClicked() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.Gray
                    )
                }
                Text(
                    text = "$amountLikes likes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
        }
    }
}
