import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.toRoute
import com.example.dreamsync.AppState
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.screens.internal.profile.CreateHikeScreen
import com.example.dreamsync.data.services.DreamService
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService
import com.example.dreamsync.navigation.BottomNavigationBar
import com.example.dreamsync.navigation.FriendsRoute
import com.example.dreamsync.navigation.FriendsRoute.FriendsHomeRoute
import com.example.dreamsync.navigation.FriendsRoute.FriendsProfileRoute
import com.example.dreamsync.navigation.NavigationDrawer
import com.example.dreamsync.screens.external.LoginScreen
import com.example.dreamsync.screens.external.RegisterScreen
import com.example.dreamsync.screens.internal.explore.ExploreScreen
import com.example.dreamsync.screens.internal.home.HomeScreen
import com.example.dreamsync.screens.internal.profile.HikeInfoScreen
import com.example.dreamsync.screens.internal.profile.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val selectedIndex = remember { mutableIntStateOf(1) } // Default: Home
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val roles = listOf("Manager", "Architect", "Chemist", "Extractor", "Forger")
    val dreamService = DreamService()
    val profileService = ProfileService()
    val hikeService = HikeService()

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
                profile = AppState.loggedInUser.collectAsState().value,
                roles = roles, // Pass roles here
                onNavigateToCreateHikeScreen = { navController.navigate("create_hike") },
                onHikeCreated = { newHike ->
                    navController.popBackStack()
                },
                onRoleSelected = { selectedRole ->
                    AppState.updateLoggedInUser(AppState.loggedInUser.value.copy(preferredRole = selectedRole))
                    Log.d("AppNavigation", "Role selected: $selectedRole")
                },
                onProfileUpdated = { updatedProfile ->
                    AppState.updateLoggedInUser(updatedProfile)
                },
                hikeService = hikeService
                    logged_in_user.value = updatedProfile
                },
                onNavigateToHikeInfoScreen = { hike ->
                    navController.navigate("hike_info/${hike.id}")
                }
            )
        }
        navigation<FriendsRoute>(startDestination = FriendsHomeRoute) {
            composable<FriendsHomeRoute> {
                    FriendsScreen(
                        onFriendClick = { friend ->
                            navController.navigate(route = FriendsProfileRoute(friend.userName))
                        },
                        profileService = profileService
                    )
            }
            composable<FriendsProfileRoute> { route ->
                val friendsProfileRoute : FriendsProfileRoute = route.toRoute()
                val profileId = friendsProfileRoute.profileId
                ProfileScreen (
                    profile = Profile(userName = profileId),
                    roles = roles,
                    onRoleSelected = { selectedRole ->
                        Log.d("AppNavigation", "Role selected: $selectedRole")
                    },
                    onProfileUpdated = { updatedProfile ->
                        AppState.updateLoggedInUser(updatedProfile)
                    },
                    onHikeCreated = {
                        // Do nothing
                    },
                    onNavigateToCreateHikeScreen = {
                        // Do nothing
                    },
                    hikeService = hikeService
                )
            }
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
        composable("create_hike") {
            CreateHikeScreen (
                onHikeCreated = { newHike ->
                    navController.popBackStack()
                },
                hikeService = hikeService,
                profileService = profileService
            )
        }
        composable("hike_info/{hikeId}") { backStackEntry ->
            val hikeId = backStackEntry.arguments?.getString("hikeId")
            val hike = logged_in_user.value.hikes.find { it.id == hikeId }
            if (hike != null) {
                HikeInfoScreen(hike = hike, onBack = { navController.popBackStack() })
            }
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
                                2 -> navController.navigate(FriendsRoute)
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
                    .fillMaxWidth()
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