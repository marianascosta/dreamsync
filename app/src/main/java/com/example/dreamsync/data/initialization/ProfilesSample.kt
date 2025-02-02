package com.example.dreamsync.data.initialization

import com.example.dreamsync.data.models.AvatarImage
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.models.Role


val adminProfile = Profile(
    userName = "John Doe",
    userEmail = "johndoe@example.com",
    userBio = "Master architect of dreams, expert in navigating the subconscious.",
    preferredRole = Role.Architect,
    avatarImage = AvatarImage.AVATAR_MALE_1
)

val profilesSample = listOf(
    Profile(
        userName = "Jane Smith",
        userEmail = "janesmith@example.com",
        userBio = "Point Man who plans the heist and makes sure everything goes according to plan.",
        preferredRole = Role.PointMan,
        avatarImage = AvatarImage.AVATAR_FEMALE_1
    ),
    Profile(
        userName = "David Brown",
        userEmail = "davidbrown@example.com",
        userBio = "Architect, responsible for designing the dream landscapes.",
        preferredRole = Role.Architect,
        avatarImage = AvatarImage.AVATAR_MALE_2

    ),
    Profile(
        userName = "Emily White",
        userEmail = "emilywhite@example.com",
        userBio = "The Dreamer, whose mind is being infiltrated in the mission.",
        preferredRole = Role.Dreamer,
        avatarImage = AvatarImage.AVATAR_FEMALE_2
    ),
    Profile(
        userName = "Michael Green",
        userEmail = "michaelgreen@example.com",
        userBio = "Forger, skilled at creating identities in the dream world.",
        preferredRole = Role.Forger,
        avatarImage = AvatarImage.AVATAR_MALE_1
    ),
    Profile(
        userName = "Sophia Black",
        userEmail = "sophiablack@example.com",
        userBio = "Chemist, who creates the powerful sedative used to keep people asleep during the dream.",
        preferredRole = Role.Chemist,
        avatarImage = AvatarImage.AVATAR_FEMALE_1
    )
)