package com.example.dreamsync.data.initialization

import com.example.dreamsync.data.models.Dream
import com.example.dreamsync.data.models.DreamCategory

val dreamsSample = listOf(
    Dream(
        title = "Escape to the Unknown",
        description = "An adventure across a vast, mysterious landscape with hidden treasures and dangers.",
        date = "2024-12-15",
        dreamCategories = listOf(DreamCategory.ADVENTURE, DreamCategory.MYSTERY)
    ),
    Dream(
        title = "Love in the Stars",
        description = "A beautiful, romantic journey where love blossoms amidst the backdrop of a sci-fi universe.",
        date = "2024-12-16",
        dreamCategories = listOf(DreamCategory.ROMANCE, DreamCategory.SCI_FI)
    ),
    Dream(
        title = "Haunting Shadows",
        description = "A spine-chilling horror story where the protagonist must survive a night in a haunted house.",
        date = "2024-12-17",
        dreamCategories = listOf(DreamCategory.HORROR, DreamCategory.MYSTERY)
    ),
    Dream(
        title = "The Last Enchantment",
        description = "A fantasy dream of magical beings and enchanted lands, where the balance of the world is in the hands of a hero.",
        date = "2024-12-18",
        dreamCategories = listOf(DreamCategory.FANTASY, DreamCategory.INSPIRATIONAL)
    ),
    Dream(
        title = "Chronicles of Time",
        description = "A historical drama that takes place in ancient Egypt, where a young pharaoh must protect his kingdom.",
        date = "2024-12-19",
        dreamCategories = listOf(DreamCategory.HISTORICAL, DreamCategory.INSPIRATIONAL)
    ),
    Dream(
        title = "Cosmic Wonders",
        description = "A journey through space, encountering alien civilizations and experiencing the beauty of the universe.",
        date = "2024-12-20",
        dreamCategories = listOf(DreamCategory.SCI_FI, DreamCategory.ADVENTURE)
    ),
    Dream(
        title = "A Journey to the Heart",
        description = "An inspirational tale of overcoming personal struggles and finding strength in oneself.",
        date = "2024-12-21",
        dreamCategories = listOf(DreamCategory.INSPIRATIONAL, DreamCategory.ROMANCE)
    ),
    Dream(
        title = "The Haunted Forest",
        description = "A gripping horror tale where a group of friends explores an ancient, cursed forest.",
        date = "2024-12-22",
        dreamCategories = listOf(DreamCategory.HORROR, DreamCategory.ADVENTURE)
    ),
    Dream(
        title = "The Mystery of the Missing Artifact",
        description = "A thrilling mystery about a missing artifact that holds the key to an ancient civilization's secrets.",
        date = "2024-12-23",
        dreamCategories = listOf(DreamCategory.MYSTERY, DreamCategory.ADVENTURE)
    ),
    Dream(
        title = "The Lost City of Atlantis",
        description = "An expedition to find the fabled lost city, full of dangers, ancient puzzles, and untold treasures.",
        date = "2024-12-24",
        dreamCategories = listOf(DreamCategory.ADVENTURE, DreamCategory.HISTORICAL)
    )
)