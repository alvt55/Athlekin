package com.example.athlekin

// TODO: Set up sign-in methods


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth


/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */class FirebaseUIActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "FirebaseUIActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        // Check if user is already signed in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Log.d(TAG, "User already signed in: ${currentUser.email}, redirecting to main")
            navigateToMainActivity()
            return
        }

        Log.d(TAG, "No user signed in, launching sign-in intent")
        createSignInIntent()
    }

    private fun createSignInIntent() {
        try {
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
            )

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()

            Log.d(TAG, "Launching sign-in intent")
            signInLauncher.launch(signInIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sign-in intent", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        Log.d(TAG, "Sign-in result received")
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        Log.d(TAG, "Result code: ${result.resultCode}, OK = $RESULT_OK")

        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            Log.d(TAG, "Sign-in successful: ${user?.email}")
            Toast.makeText(this, "Welcome ${user?.displayName ?: user?.email}", Toast.LENGTH_SHORT).show()
            navigateToMainActivity()
        } else {
            val response = result.idpResponse
            if (response == null) {
                Log.d(TAG, "User cancelled sign-in")
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "Sign-in error: ${response.error?.errorCode} - ${response.error?.message}")
                Toast.makeText(
                    this,
                    "Sign in failed: ${response.error?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            finish()
        }
    }

    private fun navigateToMainActivity() {
        Log.d(TAG, "Navigating to MainActivity")
        // Replace MainActivity::class.java with your actual main activity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}