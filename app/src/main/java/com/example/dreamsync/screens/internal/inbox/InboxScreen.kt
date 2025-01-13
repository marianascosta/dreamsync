package com.example.dreamsync.screens.internal.inbox

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.initialization.InboxSample
import com.example.dreamsync.data.models.FriendInvite
import com.example.dreamsync.data.models.HikeInvite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen() {
    val inboxSamples = InboxSample.inboxSamples

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inbox") }
            )
        }
    ) { innerPadding ->
        // Remove the fillMaxSize() here
        Box(
            modifier = Modifier
                .fillMaxSize() // Ensure this box fills the available space
                .padding(innerPadding)
        ) {
            // Only use padding for the LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight() // Try filling max height
                    .padding(horizontal = 16.dp), // Horizontal padding only
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp) // Add vertical padding for list items
            ) {
                items(inboxSamples) { item ->
                    when (item) {
                        is FriendInvite -> FriendInviteItem(item)
                        is HikeInvite -> HikeInviteItem(item)
                    }
                }
            }
        }
    }
}

@Composable
fun FriendInviteItem(friendInvite: FriendInvite) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Friend Invite", fontWeight = FontWeight.Bold)
            Text("From: ${friendInvite.senderName}")
            Text("Status: ${friendInvite.status}")
            Text("Time: ${friendInvite.timestamp}")
        }
    }
}

@Composable
fun HikeInviteItem(hikeInvite: HikeInvite) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Hike Invite", fontWeight = FontWeight.Bold)
            Text("Hike: ${hikeInvite.hikeName}")
            Text("From: ${hikeInvite.senderName}")
            Text("Status: ${hikeInvite.status}")
            Text("Time: ${hikeInvite.timestamp}")
        }
    }
}
