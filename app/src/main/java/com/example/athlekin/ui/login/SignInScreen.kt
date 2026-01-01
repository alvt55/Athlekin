import android.R.attr.onClick
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athlekin.ui.createAccount.CreateAccountViewModel
import com.example.athlekin.ui.login.SignInViewModel

@Composable
fun SignInScreen(
    openTrackerScreen: () -> Unit,
    onToCreateAccount: () -> Unit,
//    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: SignInViewModel
) {
    val shouldGoTracker by viewModel.shouldGoTracker.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    if (shouldGoTracker) {
        openTrackerScreen()
    } else {
        SignInScreenContent(
            signIn = viewModel::signIn,
            onToCreateAccount,
            errorMessage = errorMessage
        )
    }


}



@Composable
fun SignInScreenContent(signIn : (String, String) -> Unit, onToCreateAccount : () -> Unit, errorMessage : String?) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Sign In")

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


        errorMessage?.let {
            Text(text = it)
        }

        Button(
            onClick = {
                signIn(email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        Button(
            onClick = onToCreateAccount,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }
    }
}
//