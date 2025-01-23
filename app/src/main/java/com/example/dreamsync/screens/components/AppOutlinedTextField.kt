package com.example.dreamsync.screens.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * The default UI Design to be used in all fields of the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        isError = isError,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        trailingIcon = trailingIcon,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    )
}

@Composable
@Preview
fun PreviewAppTextFieldComponent() {
    var query by remember { mutableStateOf("") }

    AppOutlinedTextField(
        value = query,
        onValueChange = { newQuery -> query = newQuery },
        label = "Search",
        placeholder = "Type your search...",
        trailingIcon = {
            IconButton(onClick = { /* Handle the search icon click here */ }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}