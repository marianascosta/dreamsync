import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.ProfileService
import com.example.dreamsync.navigation.BottomNavigationBar
import com.example.dreamsync.navigation.NavigationDrawer
import com.example.dreamsync.screens.external.LoginScreen
import com.example.dreamsync.screens.internal.FriendsScreen
import com.example.dreamsync.screens.internal.HomeScreen
import com.example.dreamsync.screens.internal.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val selectedIndex = remember { mutableIntStateOf(1) } // Default: Home
    val logged_in_user = remember { mutableStateOf(Profile(userName = "")) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val roles = listOf("Manager", "Architect", "Chemist", "Extractor", "Forger")

    val navGraph = navController.createGraph(startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { profile ->
                    logged_in_user.value = profile
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                profile = logged_in_user.value,
                roles = roles, // Pass roles here
                onNavigateToFriendsScreen = { navController.navigate("friends") },
                onRoleSelected = { selectedRole ->
                    logged_in_user.value = logged_in_user.value.copy(preferredRole = selectedRole)
                    Log.d("AppNavigation", "Role selected: $selectedRole")
                }
            )
        }

        composable("friends") {
            FriendsScreen(
                profile = logged_in_user.value,
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }
        composable("home") {
            HomeScreen(
                profile = logged_in_user.value,
                onNavigateToFriendsScreen = { navController.navigate("friends") }
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                navController,
                selectedIndex.value,
                { selectedIndex.value = it },
                { coroutineScope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (navController.currentBackStackEntryAsState().value?.destination?.route != "login") {
                    TopAppBar(
                        title = { Text("DreamSync") },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
                            }
                        }
                    )
                }
            },
            bottomBar = {
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
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                NavHost(
                    navController = navController,
                    graph = navGraph
                )
            }
        }
    }
}
