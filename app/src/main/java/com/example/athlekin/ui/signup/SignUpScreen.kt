package com.example.athlekin.ui.signup

import android.R.attr.end
import android.R.attr.top
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun SignUpScreen(
    openTrackerScreen: () -> Unit,
//    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: SignUpViewModel
) {
    val shouldRestartApp by viewModel.shouldRestartApp.collectAsStateWithLifecycle()


    if (shouldRestartApp) {
        openTrackerScreen()
    } else {
        SignUpScreenContent(
            signUp = viewModel::signUp,
        )
    }


}



@Composable
fun SignUpScreenContent(signUp : (String, String, String) -> Unit) {

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