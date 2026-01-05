package com.example.athlekin.ui.createAccount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun CreateAccountScreen(
    openTrackerScreen: () -> Unit,
    onToSignIn: () -> Unit,
    viewModel: CreateAccountViewModel
) {
    val shouldGoTracker by viewModel.shouldGoTracker.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    if (shouldGoTracker) {
        openTrackerScreen()
    } else {
        CreateAccountScreenContent(
            signUp = viewModel::createAccount,
            onToSignIn,
            errorMessage = errorMessage
        )
    }


}


@Composable
fun CreateAccountScreenContent(
    signUp: (String, String, String) -> Unit,
    onToSignIn: () -> Unit, errorMessage: String?
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Create Account")

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            label = { Text("Repeat Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        errorMessage?.let {
            Text(text = it)
        }

        Button(
            onClick = {
                signUp(email, password, repeatPassword)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }

        Button(
            onClick = onToSignIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }
    }
}
