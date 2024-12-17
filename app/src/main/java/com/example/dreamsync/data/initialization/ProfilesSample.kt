package com.example.dreamsync.data.initialization

import com.example.dreamsync.data.models.Profile

val adminProfile = Profile(
    userName = "Admin",
    userEmail = "admin@email.com",
    userBio = "Admin user with full access to the app.",
    preferredRole = "Admin",
    profilePicture = "https://via.placeholder.com/150"
)

val profilesSample = listOf(
    Profile(
        userName = "Alice Smith",
        userEmail = "alice.smith@example.com",
        userBio = "A software engineer passionate about building mobile apps.",
        preferredRole = "Developer",
        profilePicture = "https://via.placeholder.com/150"
    ),
    Profile(
        userName = "Bob Johnson",
        userEmail = "bob.johnson@example.com",
        userBio = "UI/UX designer focused on user-centered design.",
        preferredRole = "Designer",
        profilePicture = "https://via.placeholder.com/150"
    ),
    Profile(
        userName = "Charlie Brown",
        userEmail = "charlie.brown@example.com",
        userBio = "Product manager who loves to collaborate on tech innovations.",
        preferredRole = "Product Manager",
        profilePicture = "https://via.placeholder.com/150"
    )
)