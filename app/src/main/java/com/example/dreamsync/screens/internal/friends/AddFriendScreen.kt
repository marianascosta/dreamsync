import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
    var inputError by remember { mutableStateOf("") } // For error message

    fun performSearch(searchText: String) {
        if (searchText.isBlank()) {
            inputError = "Type the user's name here"
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
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
                inputError = "" // Clear the error message when user starts typing
            },
            label = { Text("Search by name") },
            placeholder = { Text("Enter a name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                performSearch(searchText)
            }),
            isError = inputError.isNotEmpty() // Show error if there's a message
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
            onClick = {
                performSearch(searchText)
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