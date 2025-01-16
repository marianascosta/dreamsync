package com.example.dreamsync.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.dreamsync.navigation.IS_BOTTOM_BAR_VISIBLE

var IS_BOTTOM_BAR_VISIBLE = true

fun toggleBottomBarVisibility() {
    IS_BOTTOM_BAR_VISIBLE = !IS_BOTTOM_BAR_VISIBLE
}

data class BottomNavItem(
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)

private val navItems = listOf(
    BottomNavItem(
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    ),
    BottomNavItem(
        label = "Explore",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    ),
    BottomNavItem(
        label = "Hikes",
        selectedIcon = Icons.AutoMirrored.Default.List,
        unselectedIcon = Icons.AutoMirrored.Default.List
    ),
    BottomNavItem(
        label = "Friends",
        selectedIcon = Icons.Filled.PersonSearch,
        unselectedIcon = Icons.Outlined.PersonSearch
    )
)

@Composable
fun BottomNavigationBar(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    if (!IS_BOTTOM_BAR_VISIBLE) {
        return
    }
    NavigationBar {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selectedItemIndex == index) item.selectedIcon
                            else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selectedItemIndex == index,
                onClick = {
                    onItemSelected(index) // Notify parent about the selection
                }
            )
        }
    }
}