package com.example.dreamsync.navigation;

import kotlinx.serialization.Serializable;


@Serializable data object FriendsRoute {
    @Serializable data object FriendsHomeRoute
    @Serializable data class FriendsProfileRoute (val profileId: String)
}

@Serializable data class HikeDetailsRoute(val hikeId: String)


