package com.example.dreamsync.screens.internal.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Hike

@Composable
fun CreateHikeScreen(onHikeCreated: (Hike) -> Unit) {
    // State variables for input fields
    var hikeName by remember { mutableStateOf("") }
    var hikeDescription by remember { mutableStateOf("") }
    var selectedLayer by remember { mutableIntStateOf(1) }
    val layers = (1..10).toList()
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create a Dream Hike",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        // Input field for Hike Name
        OutlinedTextField(
            value = hikeName,
            onValueChange = { hikeName = it },
            label = { Text("Hike Name") },
            placeholder = { Text("Enter the name of your dream hike") },
            modifier = Modifier.fillMaxWidth()
        )

        // Input field for Hike Description
        OutlinedTextField(
            value = hikeDescription,
            onValueChange = { hikeDescription = it },
            label = { Text("Hike Description") },
            placeholder = { Text("Enter a brief description of the hike") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        // Dropdown menu for selecting layers
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Number of Layers: $selectedLayer",
                style = MaterialTheme.typography.bodyLarge
            )

            Box {
                Button(onClick = { dropdownExpanded = true }) {
                    Text(text = "Select Layers")
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    layers.forEach { layer ->
                        DropdownMenuItem(
                            text = { Text(layer.toString()) },
                            onClick = {
                                selectedLayer = layer
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create Hike Button
        Button(
            onClick = {
                if (hikeName.isNotEmpty() && hikeDescription.isNotEmpty()) {
                    val newHike = Hike(
                        name = hikeName,
                        description = hikeDescription,
                        layers = selectedLayer,
                        isComplete = false
                    )
                    onHikeCreated(newHike)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Hike")
        }
    }
}
