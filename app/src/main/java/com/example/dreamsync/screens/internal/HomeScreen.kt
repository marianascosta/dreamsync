package com.example.dreamsync.screens.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Dream
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.DreamService
import androidx.compose.foundation.lazy.items

@Composable
fun HomeScreen(
    profile: Profile,
    onNavigateToFriendsScreen: () -> Unit,
    dreamService: DreamService = DreamService()
) {

    // GET dreams from firebase
    val dreams = remember { mutableStateOf<List<Dream>>(emptyList()) }
    LaunchedEffect(Unit) {
        dreamService.getDreamsList { fetchedDreams ->
            dreams.value = fetchedDreams
        }
    }

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
            Text(text = "Home Screen", modifier = Modifier.align(Alignment.CenterHorizontally))

            // Show the list of dreams dynamically
            LazyColumn {
                items(dreams.value) { dream ->
                    DreamListItem(dream = dream)
                }
            }

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
fun DreamListItem(dream: Dream) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Title: ${dream.title}")
        Text(text = "Description: ${dream.description}")
        Text(text = "Date: ${dream.date}")
    }
}