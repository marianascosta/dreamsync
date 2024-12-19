import android.R.attr.onClick
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.services.HikeService
import com.example.dreamsync.screens.internal.hikes.HikesListScreen

@Composable
fun HikesScreen(
    hikeService: HikeService,
    onHikeSelected: (Hike) -> Unit,
    onAddHike: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth() // Ensures the Row spans the full width of the screen
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Your Hikes",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
            Row(
                modifier = Modifier.wrapContentWidth()
                    .padding(horizontal = 8.dp)
                    .clickable(onClick = onAddHike),

                verticalAlignment = Alignment.CenterVertically,

            ) {
                IconButton(onClick = onAddHike) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Hike",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Add Hike",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        HikesListScreen(
            hikeService = hikeService,
            onHikeClicked = onHikeSelected
        )
    }
}