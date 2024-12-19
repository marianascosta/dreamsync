package com.example.dreamsync.screens.internal.hikes.create

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.screens.components.AppOutlinedTextField
import com.example.dreamsync.screens.components.forms.AppDatePickerField
import com.example.dreamsync.screens.components.forms.AppDropdownMenuField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepCreateLayers(
    hike: Hike,
    onClickContinue: (Hike) -> Unit)
{
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var kickDate by remember { mutableStateOf("") }
    val difficulties = listOf("Easy", "Medium", "Hard")
    val context = LocalContext.current

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        //Form
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name
            AppOutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                placeholder = "Enter the name of your dream hike"
            )

            // Description
            AppOutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                placeholder = "Enter a brief description of the hike"
            )

            // Start Date
            AppDatePickerField(
                label = "Start Date",
                onDateSelected = {
                    startDate = it
                }
            )

            // Kick Date
            AppDatePickerField(
                label = "Kick Date",
                onDateSelected = {
                    kickDate = it
                }
            )

            // Difficulty
            AppDropdownMenuField (
                label = "Difficulty",
                placeholder = "Select a difficulty",
                options = difficulties,
                onOptionSelected = {
                    difficulty = it
                }
            )
        }

        // Continue button
        Button(
            onClick = { onClickContinue(hike) },
            modifier = Modifier.align(Alignment.End).padding(bottom = 16.dp)
        ) {
            Text("Continue")
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