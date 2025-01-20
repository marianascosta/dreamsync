package com.example.dreamsync

import AppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dreamsync.data.initialization.DatabaseInit
import com.example.dreamsync.ui.theme.DreamSyncTheme

class MainActivity : ComponentActivity() {
    private lateinit var  databaseInit: DatabaseInit

    override fun onCreate(savedInstanceState: Bundle?) {
        databaseInit = DatabaseInit()
        //databaseInit.initRealTimeDatabase() // uncomment to populate the database

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DreamSyncTheme {
                AppNavigation()
            }
        }
    }
}
