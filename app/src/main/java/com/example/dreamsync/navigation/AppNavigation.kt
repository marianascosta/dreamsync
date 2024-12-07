import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.navigation.BottomNavigationBar
import com.example.dreamsync.screens.external.LoginScreen
import com.example.dreamsync.screens.internal.FriendsScreen
import com.example.dreamsync.screens.internal.HomeScreen
import com.example.dreamsync.screens.internal.ProfileScreen
import kotlinx.serialization.Serializable

@Composable
fun AppNavigation() {
    var initialSelectedIndex = 0
    val navController = rememberNavController()
    val selectedIndex = remember { mutableStateOf(initialSelectedIndex) }
    val logged_in_user = remember { mutableStateOf(Profile(name = "")) }

    val navGraph = navController.createGraph(startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { profile ->
                    logged_in_user.value = profile // Store teh logged in user
                    navController.navigate("home") {
                        // Pop up the login screen so the user cannot navigate back
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                profile = logged_in_user.value,
                onNavigateToFriendsScreen = { navController.navigate("friends") }
            )
        }
        composable("friends") {
            FriendsScreen (
                profile = logged_in_user.value,
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }
        composable("home") {
            HomeScreen (
                profile = logged_in_user.value,
                onNavigateToFriendsScreen = { navController.navigate("friends") }
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = navController,
                graph = navGraph
            )
        }

        // Only show the navbar in internal screens
        val isLoginScreen = navController.currentBackStackEntryAsState().value?.destination?.route == "login"
        if (!isLoginScreen) {
            BottomNavigationBar(
                selectedItemIndex = selectedIndex.value,
                onItemSelected = { index ->
                    selectedIndex.value = index
                    when (index) {
                        0 -> navController.navigate("profile")
                        1 -> navController.navigate("home")
                        2 -> navController.navigate("friends")
                    }
                }
            )
        }
    }
}