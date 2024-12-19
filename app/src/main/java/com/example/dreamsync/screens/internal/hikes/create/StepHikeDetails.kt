package com.example.dreamsync.screens.internal.hikes.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dreamsync.data.models.Hike
import com.example.dreamsync.screens.components.AppOutlinedTextField

@Composable
fun StepHikeDetails(
    hike: Hike,
    onClickContinue: (Hike) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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
                label = "Hike Name",
                placeholder = "Enter the name of your dream hike"
            )

            // Description
            AppOutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = "Hike Description",
                placeholder = "Enter a brief description of the hike"
            )
        }

        // Continue button
        Button(
            onClick = {
                onClickContinue (
                    hike.copy(
                        name = name,
                        description = description
                    )
                )},
            modifier = Modifier.align(Alignment.End).padding(bottom = 16.dp)
        ) {
            Text("Continue")
        }
    }
}

@Preview
@Composable
fun PreviewStepHikeDetails() {
    // Sample hike object for the preview
    val sampleHike = Hike(
        id = "1",
        name = "Sample Hike",
        description = "This is a sample description for a dream hike.",
        isComplete = false,
        createdBy = "user_1",
        invitedFriends = listOf(),
        layers = listOf()
    )
    StepHikeDetails(hike = sampleHike) { }
}