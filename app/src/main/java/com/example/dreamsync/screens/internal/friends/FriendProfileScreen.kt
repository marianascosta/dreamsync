package com.example.dreamsync.screens.internal.friends

import androidx.compose.runtime.Composable
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService
import com.example.dreamsync.screens.internal.profile.ProfileScreen

@Composable
fun FriendProfileScreen(
    profileService: ProfileService,
    profileId: String
) {
    ProfileScreen(
        profileService = profileService,
        profileId = profileId,
        onNavigateToCreateHikeScreen = {},
        onNavigateToHikeInfoScreen = {},
        onHikeCreated = {},
        onRoleSelected = {},
        onProfileUpdated = {},
        hikeService = HikeService(),
        onHikeClicked = {}
    )
}
