package com.example.dreamsync

import AppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dreamsync.data.services.StorageHandler
import com.example.dreamsync.ui.theme.DreamSyncTheme

class MainActivity : ComponentActivity() {
    private lateinit var  storageHandler: StorageHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        storageHandler = StorageHandler()
//        storageHandler.initRealTimeDatabase() TODO: uncomment to populate the database
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DreamSyncTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DreamSyncTheme {
        Greeting("Android")
    }
}
