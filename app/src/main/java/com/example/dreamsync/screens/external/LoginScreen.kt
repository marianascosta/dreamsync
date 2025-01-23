package com.example.dreamsync.screens.external

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
fun LoginScreen(
    onLoginSuccess: (Profile) -> Unit,
    onRegisterClick: () -> Unit
) {
    val emailState = remember { mutableStateOf(TextFieldValue("")) }
    val emailErrorState = remember { mutableStateOf(false) }
    val passwordState = remember { mutableStateOf(TextFieldValue("")) }
    val passwordErrorState = remember { mutableStateOf(false) }
    val accountHandler = AccountHandler()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Login",
                    fontSize = 32.sp,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { newValue ->
                    emailState.value = newValue
                },
                label = { Text("Enter your email") },
                placeholder = { Text("example@email.com") },
                isError = emailErrorState.value,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
            )

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { newValue ->
                    passwordState.value = newValue
                    passwordErrorState.value = false
                },
                label = { Text("Enter your password") },
                placeholder = { Text("Password") },
                isError = passwordErrorState.value,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            if (passwordErrorState.value and passwordState.value.text.isBlank()) {
                Text(
                    text = "Password cannot be empty.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else if (passwordErrorState.value and emailErrorState.value) {
                Text(
                    text = "Incorrect email or password.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (emailState.value.text.isNotBlank()) {
                        accountHandler.loginUser(
                            email = emailState.value.text,
                            password = passwordState.value.text,
                            onLoginResult = { profile ->
                                if (profile != null) {
                                    onLoginSuccess(profile)
                                } else {
                                    emailErrorState.value = true
                                    passwordErrorState.value = true
                                }
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = emailState.value.text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            ) {
                Text(text = "Login", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Any password will work",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Button(
                onClick = {
                    onRegisterClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            ) {
                Text(text = "Register", fontSize = 16.sp, color = Color.White)
            }

            // TODO DELETE THIS
            Text(
                text = "or",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = {
                    accountHandler.loginUser(
                        email = adminAccount.email,
                        password = adminAccount.password,
                        onLoginResult = { profile ->
                            if (profile != null) {
                                onLoginSuccess(profile)
                            } else {
                                Log.e("LoginScreen", "Login failed for admin account")
                            }
                        }
                    )

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            ) {
                Text(text = "Login as admin", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}