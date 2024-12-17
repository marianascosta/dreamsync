package com.example.dreamsync.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NavigationDrawer(
    navController: NavController,
    selectedIndex: Int,
    onDrawerItemSelected: (Int) -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet {
        Text("Drawer Title", modifier = Modifier.padding(16.dp))
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
    }
}