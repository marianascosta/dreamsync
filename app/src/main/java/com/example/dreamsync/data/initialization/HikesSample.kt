package com.example.dreamsync.data.initialization

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.Layer
import com.example.dreamsync.data.services.HikeService

val hikes = listOf(
    Hike(
        id = "1",
        name = "Dream Inception",
        description = "A hike inspired by the layers of dreams in Inception.",
        isComplete = false,
        createdBy = "Dom Cobb",
        invitedFriends = listOf(profilesSample[1].id, profilesSample[2].id, profilesSample[3].id),
        layers = listOf(
            Layer(
                name = "Dream Level 1",
                description = "The first level of the dream, where everything seems real.",
                difficulty = "Easy",
                startDate = "2024-12-18T08:00",
            ),
            Layer(
                name = "Dream Level 2",
                description = "The second level, where time moves slower and dreams within dreams begin.",
                difficulty = "Medium",
                startDate = "2024-12-19T08:00",
            ),
            Layer(
                name = "Dream Level 3",
                description = "The deepest dream level, where gravity and the environment are distorted.",
                difficulty = "Hard",
                startDate = "2024-12-20T08:00",
            )
        )
    )
)

// run to update the database with the sample hikes
@Preview
@Composable
fun HikesSample() {
    val hikeService : HikeService = HikeService()
    hikes.forEach {
        hikeService.saveHike(it) { success ->
            if (success) {
                println("Hike saved successfully: ${it.name}")
            } else {
                println("Failed to save hike: ${it.name}")
            }
        }
    }
}