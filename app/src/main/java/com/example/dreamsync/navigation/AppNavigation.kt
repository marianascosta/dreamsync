import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.dreamsync.AppState
import com.example.dreamsync.data.models.HikeStatus
import com.example.dreamsync.data.services.DreamService
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.data.services.ProfileService
import com.example.dreamsync.navigation.BottomNavigationBar
import com.example.dreamsync.navigation.toggleBottomBarVisibility
import com.example.dreamsync.screens.external.LoginScreen
import com.example.dreamsync.screens.external.RegisterScreen
import com.example.dreamsync.screens.internal.explore.ExploreScreen
import com.example.dreamsync.screens.internal.home.HomeScreen
import com.example.dreamsync.screens.internal.hikes.HikeDetailScreen
import com.example.dreamsync.screens.internal.hikes.create.CreateHikeScreen
import com.example.dreamsync.screens.internal.hikes.insideHike.ConfirmationScreen
import com.example.dreamsync.screens.internal.hikes.insideHike.HikeScreensManager
import com.example.dreamsync.screens.internal.hikes.insideHike.HikeStage
import com.example.dreamsync.screens.internal.hikes.insideHike.WaitingForOthersScreen
import com.example.dreamsync.screens.internal.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val selectedIndex = remember { mutableIntStateOf(1) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val dreamService = DreamService()
    val profileService = ProfileService()
    val hikeService = HikeService()
    val loggedInUser = AppState.loggedInUser.collectAsState()

    val navGraph = navController.createGraph(startDestination = "login") {
        composable("register") {
            RegisterScreen (
                onRegisterSuccess = { profile ->
                    AppState.updateLoggedInUser(profile)
                    navController.navigate("explore") { // Default: Explore
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
                    navController.navigate("explore") { // Default: Explore
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("profile") {
            ProfileScreen(
                profileService = profileService,
                profileId = loggedInUser.value.id,
                onNavigateToCreateHikeScreen = { navController.navigate("create_hike") },
                onNavigateToHikeInfoScreen = { hike ->
                    navController.navigate("hike_info/${hike.id}")
                },
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
                hikeService = hikeService,
                onHikeClicked = { hike ->
                    navController.navigate("hike_info/${hike.id}")
                }

            )
        }
        composable("friends") {
                FriendsScreen(
                    onClickProfile = { friend ->
                        navController.navigate(route = "friends/${friend.id}")
                    },
                    profileService = profileService
                )
        }
        composable("friends/{friendId}") { route ->
            val friendId = route.arguments?.getString("friendId")
            ProfileScreen (
                profileService = profileService,
                profileId = friendId!!,
                onRoleSelected = { selectedRole ->
                    Log.d("AppNavigation", "Role selected: $selectedRole")
                },
                onProfileUpdated = { updatedProfile ->
                    AppState.updateLoggedInUser(updatedProfile)
                },
                onHikeCreated = {
                    // Do nothing
                },
                onHikeClicked = {
                    // Do nothing
                },
                onNavigateToCreateHikeScreen = {
                    // Do nothing
                },
                onNavigateToHikeInfoScreen = {
                    // Do nothing
                },
                hikeService = hikeService
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
        composable("hikes") {
            HikesScreen(
                hikeService = hikeService,
                onHikeSelected = { hike -> navController.navigate("hike_info/${hike.id}") },
                onAddHike = { navController.navigate("create_hike") },
            )
        }
        composable("create_hike") {
            CreateHikeScreen (
                hikeService = hikeService,
                profileService = profileService,
                onFinish = { navController.popBackStack() }
            )
        }
//        composable("hike_info/{hikeId}") { backStackEntry ->
//            val hikeId = backStackEntry.arguments?.getString("hikeId")
//            HikeDetailScreen(
//                hikeService = hikeService,
//                hikeId = hikeId!!,
//                onClickStartHike = {
//                    toggleBottomBarVisibility() // Hide bottom bar
//                    navController.navigate("hike_info/${hikeId}/start") }
//            )
//        }
        composable("hike_info/{hikeId}") { backStackEntry ->
            val hikeId = backStackEntry.arguments?.getString("hikeId")
            HikeDetailScreen(
                hikeService = hikeService,
                hikeId = hikeId!!,
                loggedUser = loggedInUser.value,
                onClickStartHike = {
                    toggleBottomBarVisibility() // Hide bottom bar
                    hikeService.updateHikeStatus(hikeId, HikeStatus.WAITING)
                    hikeService.updateHikeStage(hikeId, HikeStage.WAITING_FOR_OTHERS)
                    //navController.navigate("waiting_for_others/${hikeId}") }
                    navController.navigate("hike_info/${hikeId}/start") },
                onNavigateToConfirmation = {
                    navController.navigate("confirmation/${hikeId}")
                }
            )
        }
        composable("hike_info/{hikeId}/start") { backStackEntry ->
            val hikeId = backStackEntry.arguments?.getString("hikeId")
            HikeScreensManager(
                hikeId = hikeId!!,
                hikeService = hikeService,
                profileService = profileService,
                navController = navController,
                loggedUser = loggedInUser.value,
                onBackToHome = {
                    toggleBottomBarVisibility()
                    navController.popBackStack()
                },
                onStartHike = {
                    hikeService.updateHikeStatus(hikeId, HikeStatus.IN_PROGRESS)
                    navController.navigate("hike_info/${hikeId}/start")
                }
            )
        }
        composable("waiting_for_others/{hikeId}") { backStackEntry ->
            val hikeId = backStackEntry.arguments?.getString("hikeId")
            WaitingForOthersScreen(
                hikeId = hikeId!!,
                hikeService = hikeService,
                profileService = profileService,
                navController = navController,
                loggedUser = loggedInUser.value,
                onStartHike = {
                    hikeService.updateHikeStatus(hikeId, HikeStatus.IN_PROGRESS)
                    navController.navigate("hike_info/${hikeId}/start")
                }
            )
        }
        composable("confirmation/{hikeId}") { backStackEntry ->
            val hikeId = backStackEntry.arguments?.getString("hikeId")
            ConfirmationScreen(
                hikeId = hikeId!!,
                hikeService = hikeService,
                //profileService = profileService,
                navController = navController,
                loggedUser = loggedInUser.value
            )
        }
        composable("add_friend") {
            AddFriendScreen(
                profileService = profileService,
                onFriendAdded = {navController.popBackStack()}
            )
        }

    }

    Scaffold(
        topBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            val title = when (currentRoute) {
                "profile" -> "Profile"
                "friends" -> "Friends"
                "explore" -> "Explore"
                "hikes" -> "Hikes"
                else -> "DreamSync"
            }
            if (currentRoute != "login" && currentRoute != "register") {
                TopAppBar(
                    title = { Text(title) },
                    actions = {
                        if (currentRoute == "friends") {
                            IconButton(onClick = { navController.navigate("add_friend") }) {
                                Icon(Icons.Default.PersonAddAlt1, contentDescription = "Add a Friend")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != "login" && currentRoute != "register") {
                BottomNavigationBar(
                    selectedItemIndex = selectedIndex.intValue,
                    onItemSelected = { index ->
                        selectedIndex.intValue = index
                        when (index) {
                            0 -> navController.navigate("profile")
                            1 -> navController.navigate("explore")
                            2 -> navController.navigate("hikes")
                            3 -> navController.navigate("friends")
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