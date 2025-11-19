package com.example.athlekin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.athlekin.ui.theme.AthlekinTheme
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth



class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        // Now check user
        val currentUser = auth.currentUser
        Log.d(TAG, "currentUser")


        if (currentUser == null) {
            Log.d(TAG, "Not signed in, redirect to OAuth sign-in")
            // User not signed in, launch FirebaseUI
            startActivity(Intent(this, FirebaseUIActivity::class.java))
            finish() // Close MainActivity
            return
        }

        // User is signed in, show the main app
        Log.d(TAG, "Signed in, redirect to main page")
        enableEdgeToEdge()
        setContent {
            AthlekinTheme {
                AthlekinApp()
            }
        }
    }
}

