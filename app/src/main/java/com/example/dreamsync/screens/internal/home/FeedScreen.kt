//package com.example.dreamsync.screens.internal.home
//
//import android.util.Log
//import android.util.Log.d
//import com.example.dreamsync.R
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import com.example.dreamsync.AppState.loggedInUser
//import com.example.dreamsync.data.models.Dream
//import com.example.dreamsync.data.models.Profile
//import com.example.dreamsync.data.services.DreamService
//import com.example.dreamsync.data.services.ProfileService
//
//@Composable
//fun DreamFeedScreen(dreamService: DreamService) {
//
//    val dreams = remember { mutableStateOf<List<Dream>>(emptyList()) }
//    val isLoading = remember { mutableStateOf(true) }
//    val errorMessage = remember { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(Unit) {
//        try {
//            dreamService.getDreamsList { fetchedDreams ->
//                dreams.value = fetchedDreams
//                isLoading.value = false
//            }
//        } catch (e: Exception) {
//            isLoading.value = false
//            errorMessage.value = "Failed to load dreams. Please try again later."
//        }
//    }
//
//    if (isLoading.value) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator(
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
//    } else if (errorMessage.value != null) {
//        Text("Error: ${errorMessage.value}", color = Color.Red, textAlign = TextAlign.Center)
//    } else {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            dreams.value.forEach { dream ->
//                DreamPost(
//                    dream = dream
//                )
//            }
//        }
//    }
//}
//
//
