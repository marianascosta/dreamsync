package com.example.dreamsync.screens.external

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dreamsync.data.handlers.AccountHandler
import com.example.dreamsync.data.initialization.adminAccount
import com.example.dreamsync.data.models.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (Profile) -> Unit,
    onClickLogin: () -> Unit
) {
    val nameState = remember { mutableStateOf(TextFieldValue("")) }
    val emailState = remember { mutableStateOf(TextFieldValue("")) }
    val passwordState = remember { mutableStateOf(TextFieldValue("")) }
    val confirmPasswordState = remember { mutableStateOf(TextFieldValue("")) }
    val emailErrorState = remember { mutableStateOf(false) }
    val nameErrorState = remember { mutableStateOf(false) }
    val passwordMatchErrorState = remember { mutableStateOf(false) }

    val accountHandler = AccountHandler()

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isNameValid(name: String): Boolean {
        val nameRegex = Regex("^[a-zA-Z0-9]+$")
        return nameRegex.matches(name)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Center-aligned TopAppBar
        CenterAlignedTopAppBar(
            title = {
                Text(text = "Register", fontSize = 32.sp, style = MaterialTheme.typography.titleMedium)
            },
            navigationIcon = {
                IconButton(onClick = { onClickLogin() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(32.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nameState.value,
                onValueChange = { newValue ->
                    nameState.value = newValue
                    nameErrorState.value = !isNameValid(newValue.text)
                },
                label = { Text("Enter your name") },
                placeholder = { Text("Username123") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                isError = nameErrorState.value,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
            if (nameErrorState.value) {
                Text(
                    text = "Invalid name. Use only letters and numbers, no spaces.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { newValue ->
                    emailState.value = newValue
                    emailErrorState.value = !isEmailValid(newValue.text)
                },
                label = { Text("Enter your email") },
                placeholder = { Text("example@mail.com") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                isError = emailErrorState.value,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
            if (emailErrorState.value) {
                Text(
                    text = "Invalid email format.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { newValue ->
                    passwordState.value = newValue
                    passwordMatchErrorState.value = false
                },
                label = { Text("Enter your password") },
                placeholder = { Text("Password") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )

            OutlinedTextField(
                value = confirmPasswordState.value,
                onValueChange = { newValue ->
                    confirmPasswordState.value = newValue
                    passwordMatchErrorState.value = newValue.text != passwordState.value.text
                },
                label = { Text("Confirm your password") },
                placeholder = { Text("Confirm Password") },
                singleLine = true,
                isError = passwordMatchErrorState.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
            if (passwordMatchErrorState.value) {
                Text(
                    text = "Passwords do not match.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    accountHandler.registerUser(
                        email = emailState.value.text,
                        password = passwordState.value.text,
                        userName = nameState.value.text
                    ) { success, profile ->
                        if (success) {
                            onRegisterSuccess(profile!!)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !nameErrorState.value &&
                        !emailErrorState.value &&
                        !passwordMatchErrorState.value &&
                        nameState.value.text.isNotBlank() &&
                        emailState.value.text.isNotBlank() &&
                        passwordState.value.text.isNotBlank() &&
                        confirmPasswordState.value.text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            ) {
                Text(text = "Register", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}