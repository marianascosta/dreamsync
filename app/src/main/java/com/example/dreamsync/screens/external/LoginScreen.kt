package com.example.dreamsync.screens.external

import android.provider.Telephony.Carriers.PASSWORD
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreamsync.data.models.Profile

@Composable
fun LoginScreen(onLoginSuccess: (Profile) -> Unit) {
    val nameState = remember { mutableStateOf(TextFieldValue("")) }
    val passwordState = remember { mutableStateOf(TextFieldValue("")) }
    val passwordErrorState = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text(text = "Login Screen", fontSize = 24.sp)

        TextField(
            value = nameState.value,
            onValueChange = { newValue ->
                nameState.value = newValue
            },
            label = { Text("Enter your name") },
            placeholder = { Text("John Doe") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        TextField(
            value = passwordState.value,
            onValueChange = { newValue ->
                passwordState.value = newValue
                passwordErrorState.value = false
            },
            label = { Text("Enter your password") },
            placeholder = { Text("Password") },
            isError = passwordErrorState.value,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation() // Hides the password text
        )

        Text(
            text = "Any password will work",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Button(
            onClick = {
                if (nameState.value.text.isNotBlank()) {
                    val profile = Profile(userName = nameState.value.text)
                    onLoginSuccess(profile)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nameState.value.text.isNotBlank()
        ) {
            Text("Login")
        }
    }
}