package com.example.dreamsync.screens.components.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dreamsync.screens.components.AppOutlinedTextField

/**
 * A dropdown menu field that displays a list of options when clicked.
 * Source: https://developer.android.com/develop/ui/compose/components/datepickers
 */
@Composable
fun AppDropdownMenuField(
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String = "Select an option",
    options: List<String> = listOf("Edit", "Settings", "Send Feedback"),
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }

    AppOutlinedTextField(
        value = selectedOption,
        onValueChange = { },
        label = label,
        placeholder = placeholder,
        trailingIcon = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Dropdown menu")
            }
        },
        modifier = modifier
            .fillMaxWidth()
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                    selectedOption = option
                    onOptionSelected(option)
                    expanded = false
                }
            )
        }
    }
}


@Preview
@Composable
fun PreviewDropdownMenuFormField() {
    AppDropdownMenuField (
        label = "Select Action",
        onOptionSelected = { option -> println("Selected: $option") }
    )
}
