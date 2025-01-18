import android.util.Log
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

@Composable
fun FriendsScreen(
    profileService: ProfileService,
    onFriendClick: (Profile) -> Unit,
) {
    val friends = remember { mutableStateOf<List<Profile>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        Log.d("FriendsScreen", "Fetched friends: ${loggedInUser.value.id} - ${loggedInUser.value.friendsIds}")
        profileService.getFriendsList(loggedInUser.value.id) { friendsList ->
            friends.value = friendsList
            isLoading.value = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (friends.value.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("You have no friends yet.", fontSize = 18.sp, color = Color.Gray)
            }
        }
        for (friend in friends.value) {
            FriendCard(
                friend = friend,
                onClick = { onFriendClick(friend) }
            )
        }
    }
}

@Composable
fun FriendCard(friend: Profile, onClick: ()-> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
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