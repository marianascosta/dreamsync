import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.dreamsync.AppState
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.DreamService
import com.example.dreamsync.navigation.BottomNavigationBar
import com.example.dreamsync.navigation.NavigationDrawer
import com.example.dreamsync.screens.external.LoginScreen
import com.example.dreamsync.screens.external.RegisterScreen
import com.example.dreamsync.screens.internal.explore.ExploreScreen
import com.example.dreamsync.screens.internal.friends.FriendsScreen
import com.example.dreamsync.screens.internal.home.HomeScreen
import com.example.dreamsync.screens.internal.profile.ProfileScreen
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
    val dreamService = DreamService()

    fun updateProfile(updatedProfile: Profile) {
        logged_in_user.value = updatedProfile
    }

    val navGraph = navController.createGraph(startDestination = "login") {
        composable("register") {
            RegisterScreen (
                onRegisterSuccess = { profile ->
                    AppState.updateLoggedInUser(profile)
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onClickLogin = { navController.navigate("login") }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = { profile ->
                    AppState.updateLoggedInUser(profile)
                    println("Logged in user: ${AppState.loggedInUser.value}")
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("register") }
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
                },
                onProfileUpdated = { updatedProfile ->
                    updateProfile(updatedProfile) // Update the profile with the new information
                }
            )
        }
        composable("friends") {
            FriendsScreen(
                onNavigateToProfile = {
                    navController.navigate("profile")
                    selectedIndex.intValue = 0
                }
            )
        }
        composable("home") {
            HomeScreen(
                dreamService = dreamService
            )
        }
        composable("explore") {
            ExploreScreen(
                dreamService = dreamService
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { NavigationDrawer(
            navController,
            selectedIndex.intValue,
            { selectedIndex.intValue = it },
            { coroutineScope.launch { drawerState.close() } }
        ) }
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        Scaffold(
            topBar = {
                val title = when (currentRoute) {
                    "home" -> "Home"
                    "profile" -> "Profile"
                    "friends" -> "Friends"
                    "explore" -> "Explore"
                    else -> "DreamSync"
                }
                if (currentRoute != "login" && currentRoute != "register") {
                    TopAppBar(
                        title = { Text(title) },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Open Drawer")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (currentRoute != "login" && currentRoute != "register") {
                    BottomNavigationBar(
                        selectedItemIndex = selectedIndex.intValue,
                        onItemSelected = { index ->
                            selectedIndex.intValue = index
                            when (index) {
                                0 -> navController.navigate("profile")
                                1 -> navController.navigate("home")
                                2 -> navController.navigate("friends")
                                3 -> navController.navigate("explore")
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                NavHost(
                    navController = navController,
                    graph = navGraph
                )
            }
        }
    }
}

