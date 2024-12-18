import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.screens.internal.hikes.HikesListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikesScreen(
    hikeService: HikeService,
    onHikeSelected: (Hike) -> Unit, // Callback when a hike is clicked
    onAddHike: () -> Unit, // Callback when the "plus" button is clicked
    onBackPressed: () -> Unit // Callback for handling back navigation
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Hikes") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Add Hike Button
                    IconButton(onClick = onAddHike) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Hike"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Removed unnecessary Box, use paddingValues for correct layout
        Column(
            modifier = Modifier
                .padding(paddingValues) // Apply padding to the entire content
                .fillMaxHeight() // Ensures the content fills the screen correctly
        ) {
            HikesListScreen(
                hikeService = hikeService,
                onHikeClicked = onHikeSelected
            )
        }
    }
}