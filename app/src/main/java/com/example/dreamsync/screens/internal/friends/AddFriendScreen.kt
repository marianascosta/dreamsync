import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.ProfileService

@Composable
fun AddFriendScreen(profileService: ProfileService) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var showNoResults by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search by name") },
            placeholder = { Text("Enter a name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                performSearch(
                    searchText = searchText,
                    profileService = profileService,
                    onResults = { results ->
                        searchResults = results
                        showNoResults = results.isEmpty()
                    },
                    onError = {
                        searchResults = emptyList()
                        showNoResults = true
                    }
                )
            })
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isSearching = true
                performSearch(
                    searchText = searchText,
                    profileService = profileService,
                    onResults = { results ->
                        isSearching = false
                        searchResults = results
                        showNoResults = results.isEmpty()
                    },
                    onError = {
                        isSearching = false
                        searchResults = emptyList()
                        showNoResults = true
                    }
                )
            },
            enabled = searchText.isNotBlank()
        ) {
            Text("Search")
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (isSearching) {
            CircularProgressIndicator()
        } else if (showNoResults) {
            Text("No results found.", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { profile ->
                    FriendCard(friend = profile)
                }
            }
        }
    }
}

private fun performSearch(
    searchText: String,
    profileService: ProfileService,
    onResults: (List<Profile>) -> Unit,
    onError: () -> Unit
) {
    profileService.getAllProfiles { profiles ->
        val filteredProfiles = profiles.filter { it.userName.contains(searchText, ignoreCase = true) }
        onResults(filteredProfiles)
    }
}
