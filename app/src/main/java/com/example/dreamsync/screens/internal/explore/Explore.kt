package com.example.dreamsync.screens.internal.explore

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Dream
import com.example.dreamsync.data.models.DreamCategory
import com.example.dreamsync.data.services.DreamService
import com.example.dreamsync.screens.internal.home.DreamPost

@Composable
fun ExploreScreen(dreamService: DreamService) {
    val allCategories = DreamCategory.entries
    var selectedCategories by remember { mutableStateOf(setOf<DreamCategory>()) }
    var searchText by remember { mutableStateOf("") }
    var dreams by remember { mutableStateOf<List<Dream>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        dreamService.getDreamsList { fetchedDreams ->
            dreams = fetchedDreams
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
                val filteredDreams = dreams.filter { dream ->
                    (selectedCategories.isEmpty() || dream.dreamCategories.all { it in selectedCategories }) &&
                            (searchText.isEmpty() || dream.title.contains(searchText, ignoreCase = true))
                }

                filteredDreams.forEach { dream ->
                    DreamPost(dream = dream)
                }
            }
        }
    }
}

@Composable
fun CategoriesRow(
    categories: List<DreamCategory>,
    selectedCategories: Set<DreamCategory>,
    onCategorySelected: (DreamCategory) -> Unit
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
