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
fun SignUpScreen(
    openTrackerScreen: () -> Unit,
//    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: SignUpViewModel
) {
    val shouldGoTracker by viewModel.shouldGoTracker.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    if (shouldGoTracker) {
        openTrackerScreen()
    } else {
        SignUpScreenContent(
            signUp = viewModel::signUp,
            errorMessage = errorMessage
        )
    }


}



@Composable
fun SignUpScreenContent(signUp : (String, String, String) -> Unit, errorMessage : String?) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Sign Up")

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
            Text("Sign Up")
        }
    }
}
//
//@Composable
//@OptIn(ExperimentalMaterial3Api::class)
//fun SignUpScreenContent(
//    signUp: (String, String, String, (ErrorMessage) -> Unit) -> Unit,
//    showErrorSnackbar: (ErrorMessage) -> Unit
//) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var repeatPassword by remember { mutableStateOf("") }
//    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
//
//    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
//    ) { innerPadding ->
//        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
//            val (appLogo, form) = createRefs()
//
//            Column(
//                modifier = Modifier
//                    .constrainAs(appLogo) {
//                        top.linkTo(parent.top)
//                        start.linkTo(parent.start)
//                        end.linkTo(parent.end)
//                    },
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Spacer(Modifier.size(24.dp))
//
//                Image(
//                    modifier = Modifier.size(88.dp),
//                    painter = painterResource(id = R.mipmap.ic_launcher_round),
//                    contentDescription = "App logo"
//                )
//
//                Spacer(Modifier.size(24.dp))
//            }
//
//            Column(
//                modifier = Modifier
//                    .constrainAs(form) {
//                        top.linkTo(appLogo.bottom)
//                        start.linkTo(parent.start)
//                        end.linkTo(parent.end)
//                    },
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Spacer(Modifier.size(24.dp))
//
//                OutlinedTextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 24.dp),
//                    value = email,
//                    onValueChange = { email = it },
//                    label = { Text(stringResource(R.string.email)) }
//                )
//
//                Spacer(Modifier.size(16.dp))
//
//                OutlinedTextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 24.dp),
//                    value = password,
//                    onValueChange = { password = it },
//                    label = { Text(stringResource(R.string.password)) },
//                    visualTransformation = PasswordVisualTransformation()
//                )
//
//                Spacer(Modifier.size(16.dp))
//
//                OutlinedTextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 24.dp),
//                    value = repeatPassword,
//                    onValueChange = { repeatPassword = it },
//                    label = { Text(stringResource(R.string.repeat_password)) },
//                    visualTransformation = PasswordVisualTransformation()
//                )
//
//                Spacer(Modifier.size(32.dp))
//
//                StandardButton(
//                    label = R.string.sign_up_with_email,
//                    onButtonClick = {
//                        signUp(
//                            email,
//                            password,
//                            repeatPassword,
//                            showErrorSnackbar
//                        )
//                    }
//                )
//
//                Spacer(Modifier.size(16.dp))
//
//                //TODO: Uncomment line below when Google Authentication is implemented
//                //AuthWithGoogleButton(R.string.sign_up_with_google) { }
//            }
//        }
//    }
//}
//
//@Composable
//@Preview(showSystemUi = true)
//fun SignUpScreenPreview() {
//    MakeItSoTheme(darkTheme = true) {
//        SignUpScreenContent(
//            signUp = { _, _, _, _ -> },
//            showErrorSnackbar = {}
//        )
//    }
//}