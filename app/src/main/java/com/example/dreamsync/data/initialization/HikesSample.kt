package com.example.dreamsync.data.initialization

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.Layer
import com.example.dreamsync.data.services.HikeService

val hikes = listOf(
    Hike(
        _id = "1",
        name = "Dream Inception",
        description = "A hike inspired by the layers of dreams in Inception.",
        isComplete = false,
        createdBy = "Dom Cobb",
        invitedFriends = emptyList(),
        layers = listOf(
            Layer(
                name = "Dream Level 1",
                description = "The first level of the dream, where everything seems real.",
                difficulty = "Easy",
                startDate = "2024-12-18T08:00",
                kickDate = "2024-12-18T09:00"
            ),
            Layer(
                name = "Dream Level 2",
                description = "The second level, where time moves slower and dreams within dreams begin.",
                difficulty = "Medium",
                startDate = "2024-12-19T08:00",
                kickDate = "2024-12-19T09:00"
            ),
            Layer(
                name = "Dream Level 3",
                description = "The deepest dream level, where gravity and the environment are distorted.",
                difficulty = "Hard",
                startDate = "2024-12-20T08:00",
                kickDate = "2024-12-20T09:00"
            )
        )
    ),
    Hike(
        _id = "2",
        name = "Mountain Adventure",
        description = "An exhilarating mountain hike with breathtaking views.",
        isComplete = false,
        createdBy = "John Doe",
        invitedFriends = emptyList(),
        layers = emptyList()
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