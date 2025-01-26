package com.example.dreamsync.data.initialization

import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.HikeDefaultImage
import com.example.dreamsync.data.models.Layer

val hikes = listOf(
    Hike(
        id = "1",
        name = "Dream Inception",
        description = "A hike inspired by the layers of dreams in Inception.",
        isComplete = false,
        createdBy = "Dom Cobb",
        invitedFriends = listOf(profilesSample[1].id, profilesSample[2].id, profilesSample[3].id),
        hikeDefaultImage = HikeDefaultImage.JOURNEY_HEART,
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
