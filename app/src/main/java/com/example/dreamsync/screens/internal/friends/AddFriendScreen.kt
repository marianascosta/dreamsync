import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.dreamsync.AppState
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.ProfileService

@Composable
fun AddFriendScreen(
    profileService: ProfileService,
    onFriendAdded: (Profile) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var showNoResults by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    var inputError by remember { mutableStateOf("") }
    val loggedInUser by AppState.loggedInUser.collectAsState()

    fun performSearch(searchText: String) {
        if (searchText.isBlank()) {
            inputError = "Please enter a name to search."
            searchResults = emptyList()
            showNoResults = false
        } else {
            isSearching = true
            profileService.searchProfiles(searchText) { results ->
                isSearching = false
                if (results.isNotEmpty()) {
                    searchResults = results
                    showNoResults = false
                } else {
                    searchResults = emptyList()
                    showNoResults = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
                inputError = ""
            },
            label = { Text("Search by name") },
            placeholder = { Text("Enter a name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                performSearch(searchText)
            }),
            isError = inputError.isNotEmpty()
        )

        if (inputError.isNotEmpty()) {
            Text(
                text = inputError,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { performSearch(searchText) },
            enabled = searchText.isNotBlank()
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isSearching -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            showNoResults -> {
                Text(
                    text = "No profiles found matching \"$searchText\".",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (profile in searchResults) {
                        ProfileCard(
                            profile = profile,
                            isFriend = loggedInUser.friendsIds.contains(profile.id),
                            onAddFriend = {
                                profileService.addFriend(loggedInUser, profile.id)
                                onFriendAdded(profile)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(
    profile: Profile,
    isFriend: Boolean,
    onAddFriend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .clickable { /* Optionally handle profile clicks */ }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(profile.profilePicture),
            contentDescription = "${profile.userName}'s profile picture",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(profile.userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(profile.userBio, fontSize = 14.sp, color = Color.Gray)
        }
        if (!isFriend) {
            Button(onClick = { onAddFriend() }) {
                Text("Add")
            }
        } else {
            Text(
                text = "Friend",
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}