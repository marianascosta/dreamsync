package com.example.dreamsync.screens.internal.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Hike

//@Composable
//fun HikeInfoScreen(hike: Hike, onBack: () -> Unit) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = hike.name,
//            style = MaterialTheme.typography.headlineMedium
//        )
//        Text(
//            text = "Description: ${hike.description}",
//            style = MaterialTheme.typography.bodyLarge
//        )
//        Text(
//            text = "Layers: ${hike.layers}",
//            style = MaterialTheme.typography.bodyLarge
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = onBack) {
//            Text("Back")
//        }
//    }
//}
