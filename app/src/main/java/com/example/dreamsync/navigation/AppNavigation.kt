import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.dreamsync.screens.FriendsListScreen
import com.example.dreamsync.screens.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data class Profile(val name: String)

@Serializable
object FriendsList

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val navGraph = navController.createGraph(startDestination = "profile") {
        composable("profile") {
            ProfileScreen(
                profile = Profile(name = "John Doe"),
                onNavigateToFriendsList = { navController.navigate("friendsList") }
            )
        }
        composable("friendsList") {
            FriendsListScreen(
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }
    }

    NavHost(
        navController = navController,
        graph = navGraph
    )
}