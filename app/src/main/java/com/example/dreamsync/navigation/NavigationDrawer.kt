package com.example.dreamsync.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NavigationDrawer(
    navController: NavController,
    selectedIndex: Int,
    onDrawerItemSelected: (Int) -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet {
        Text(text = "Explore", fontWeight = FontWeight.Bold, fontSize = 24.sp,modifier = Modifier.padding(16.dp))
        NavigationDrawerItem(
            label = { Text("Home") },
            selected = selectedIndex == 1,
            onClick = {
                onDrawerItemSelected(1)
                navController.navigate("home")
                onCloseDrawer()
            }
        )
        NavigationDrawerItem(
            label = { Text("Profile") },
            selected = selectedIndex == 0,
            onClick = {
                onDrawerItemSelected(0)
                navController.navigate("profile")
                onCloseDrawer()
            }
        )
        NavigationDrawerItem(
            label = { Text("Friends") },
            selected = selectedIndex == 2,
            onClick = {
                onDrawerItemSelected(2)
                navController.navigate("friends")
                onCloseDrawer()
            }
        )
    }
}