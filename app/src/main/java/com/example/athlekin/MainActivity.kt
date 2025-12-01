package com.example.athlekin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.example.athlekin.ui.theme.AthlekinTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAuth()
    }

    private fun checkAuth() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Log.d(TAG, "No user signed in, launching FirebaseUI")
            launchSignInUI()
        } else {
            Log.d(TAG, "User signed in: ${currentUser.email}")
            showMainApp()
        }
    }

    private fun launchSignInUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        handleSignInResult(res)
    }

    private fun handleSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            Log.d(TAG, "Sign-in successful: ${user?.email}")
            Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
            showMainApp()
        } else {
            Toast.makeText(this, "Sign-in canceled", Toast.LENGTH_SHORT).show()
            finish() // user must sign in to continue
        }
    }

    private fun showMainApp() {
        enableEdgeToEdge()
        setContent {
            AthlekinTheme {
                AthlekinApp() // Your Compose Navigation + UI
            }
        }
    }
}
