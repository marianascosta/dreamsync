import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.dreamsync.navigation.BottomNavigationBar
import com.example.dreamsync.screens.FriendsScreen
import com.example.dreamsync.screens.HomeScreen
import com.example.dreamsync.screens.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data class Profile(val name: String)

@Serializable
object FriendsList

@Composable
fun AppNavigation() {
    var initialSelectedIndex = 0
    val navController = rememberNavController()
    val selectedIndex = remember { mutableStateOf(initialSelectedIndex) }

    // Set up the navigation graph
    val navGraph = navController.createGraph(startDestination = "profile") {
        composable("profile") {
            ProfileScreen(
                profile = Profile(name = "John Doe"),
                onNavigateToFriendsScreen = { navController.navigate("friends") }
            )
        }
        composable("friends") {
            FriendsScreen (
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }
        composable("home") {
            HomeScreen (
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