package com.example.dreamsync

import AppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
