import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.dreamsync.AppState.loggedInUser
import com.example.dreamsync.data.models.Profile
import com.example.dreamsync.data.services.ProfileService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    profileService: ProfileService,
    onFriendClick: (Profile) -> Unit,
) {
    val friends = remember { mutableStateOf<List<Profile>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        profileService.getFriendsList(
            profileId = loggedInUser.value.id
        ) { fetchedFriends ->
            friends.value = fetchedFriends
            isLoading.value = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    )  {
        if (friends.value.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("You have no friends yet.", fontSize = 18.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(friends.value.size) { index ->
                    val friend = friends.value[index]
                    FriendCard(friend = friend, onFriendClick = onFriendClick)
                }
            }
        }
    }

}

@Composable
fun FriendCard(friend: Profile, onFriendClick: (Profile) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFriendClick(friend) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(friend.profilePicture),
            contentDescription = "${friend.userName}'s profile picture",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(friend.userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Tap to view profile", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FriendsScreenPreview() {
    val mockFriends = remember {
        listOf(
            Profile(userName = "Alice Smith", profilePicture = "https://via.placeholder.com/150"),
            Profile(userName = "Bob Johnson", profilePicture = "https://via.placeholder.com/150"),
            Profile(userName = "Charlie Brown", profilePicture = "https://via.placeholder.com/150")
        )
    }

    class MockProfileService : ProfileService() {
        override fun getFriendsList(loggedInUserId: String, onFriendsFetched: (List<Profile>) -> Unit) {
            onFriendsFetched(mockFriends)
        }
    }

    FriendsScreen(
        profileService = MockProfileService(),
        onFriendClick = { friend ->
            println("Clicked on: ${friend.userName}")
        }
    )
}