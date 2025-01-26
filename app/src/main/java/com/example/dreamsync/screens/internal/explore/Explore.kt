package com.example.dreamsync.screens.internal.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.dreamsync.AppState.loggedInUser
import com.example.dreamsync.data.models.Category
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService

@Composable
fun ExploreScreen(
    hikeService: HikeService,
) {
    val allCategories = Category.entries
    var selectedCategories by remember { mutableStateOf(setOf<Category>()) }
    var searchText by remember { mutableStateOf("") }
    var hikes by remember { mutableStateOf<List<Hike>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        hikeService.getAllHikes { fetchedHikes ->
            hikes = fetchedHikes
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Dreams") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        CategoriesRow(
            categories = allCategories,
            selectedCategories = selectedCategories
        ) { category ->
            selectedCategories = if (selectedCategories.contains(category)) {
                selectedCategories - category
            } else {
                selectedCategories + category
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val filteredDreams = hikes.filter { hike ->
                    (selectedCategories.isEmpty() || selectedCategories.all { it in hike.categories }) &&
                            (searchText.isEmpty() || hike.name.contains(searchText, ignoreCase = true))
                }

                filteredDreams.forEach { hike ->
                    HikePost(hike = hike)
                }
            }
        }
    }
}

@Composable
fun CategoriesRow(
    categories: List<Category>,
    selectedCategories: Set<Category>,
    onCategorySelected: (Category) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .wrapContentHeight()
    ) {
        categories.forEach { category ->
            FilterChip(
                selected = category in selectedCategories,
                onClick = { onCategorySelected(category) },
                label = { Text(text = category.displayName) },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.height(36.dp)
            )
        }
    }
}

@Composable
fun HikePost(
    hike: Hike,
    hikeService: HikeService = HikeService(),
    profileService: ProfileService = ProfileService()
) {
    var isLiked by remember { mutableStateOf(hike.likedByProfiles.contains(loggedInUser.value.id)) }
    var amountLikes by remember { mutableIntStateOf(hike.likedByProfiles.size) }
    var author by remember { mutableStateOf(Profile()) }

    LaunchedEffect(hike.createdBy) {
        profileService.getProfileById(hike.createdBy) { fetchedProfile ->
            author = fetchedProfile
        }
    }

    fun onLikeButtonClicked() {
        if (hike.likedByProfiles.contains(loggedInUser.value.id)) {
            hike.likedByProfiles = hike.likedByProfiles.toMutableList().apply {
                remove(loggedInUser.value.id)
            }
        } else {
            hike.likedByProfiles = hike.likedByProfiles.toMutableList().apply {
                add(loggedInUser.value.id)
            }
        }
        hikeService.updateHike(hike, onUpdateComplete = {
            amountLikes = hike.likedByProfiles.size
            isLiked = hike.likedByProfiles.contains(loggedInUser.value.id)

        })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Image(
                    painter =rememberAsyncImagePainter(model = "file:///android_asset/${author.avatarImage.fileName}"),
                    contentDescription = "${author.userName}'s profile picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(author.userName, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterVertically))
            }

            Image(
                painter =rememberAsyncImagePainter(model = "file:///android_asset/${hike.hikeDefaultImage.fileName}"),
                contentDescription = "${hike.name} Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = hike.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = hike.description,
                style = MaterialTheme.typography.bodyMedium,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Time: 10:00 PM",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )

                Text(
                    text = "Location: New York",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { onLikeButtonClicked() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.Gray
                    )
                }
                Text(
                    text = "$amountLikes likes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
        }
    }
}