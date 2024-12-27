package com.example.dreamsync.screens.internal.hikes.create

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.data.models.Layer
import com.example.dreamsync.screens.components.AppOutlinedTextField
import com.example.dreamsync.screens.components.forms.AppDatePickerField
import com.example.dreamsync.screens.components.forms.AppDropdownMenuField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepCreateLayers(
    hike: Hike,
    onClickContinue: (Hike) -> Unit
) {
    var layers by remember { mutableStateOf(listOf(Layer())) }
    val difficulties = listOf("Easy", "Medium", "Hard")

    // Add a new layer when button is clicked
    fun addNewLayer() {
        val newLayer = Layer()
        layers = layers + newLayer
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        // Form list inside a LazyColumn
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            layers.forEachIndexed{ index, layer ->
                LayerForm(
                    layer = layer,
                    onNameChange = { name -> layers = layers.toMutableList().apply { this[index] = layer.copy(name = name) } },
                    onDescriptionChange = { description -> layers = layers.toMutableList().apply { this[index] = layer.copy(description = description) } },
                    onStartDateChange = { startDate -> layers = layers.toMutableList().apply { this[index] = layer.copy(startDate = startDate) } },
                    onDifficultyChange = { difficulty -> layers = layers.toMutableList().apply { this[index] = layer.copy(difficulty = difficulty) } },
                    difficulties = difficulties,
                    layerNumber = index + 1 // Display layer number
                )
            }
        }

        // Add new layer button at the bottom
        Button(
            onClick = { addNewLayer() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        ) {
            Text("Add New Layer")
        }

        // Continue button
        Button(
            onClick = { onClickContinue(
                hike.copy(layers = layers)
            ) },
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 16.dp)
        ) {
            Text("Continue")
        }
    }
}

@Composable
fun LayerForm(
    layer: Layer,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onDifficultyChange: (String) -> Unit,
    difficulties: List<String>,
    layerNumber: Int // Receive the layer number to display in the title
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Layer Title with number
            Text(
                text = "Layer $layerNumber",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Name
            AppOutlinedTextField(
                value = layer.name,
                onValueChange = onNameChange,
                label = "Layer Name",
                placeholder = "Enter layer name"
            )

            // Description
            AppOutlinedTextField(
                value = layer.description,
                onValueChange = onDescriptionChange,
                label = "Description",
                placeholder = "Enter description"
            )

            // Start Date
            AppDatePickerField(
                label = "Start Date",
                onDateSelected = onStartDateChange
            )

            // Difficulty
            AppDropdownMenuField(
                label = "Difficulty",
                placeholder = "Select difficulty",
                options = difficulties,
                onOptionSelected = onDifficultyChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StepCreateLayerPreview() {
    StepCreateLayers(
        hike = Hike(),
        onClickContinue = {}
    )
}