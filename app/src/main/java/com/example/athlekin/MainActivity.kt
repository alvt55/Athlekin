package com.example.athlekin


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel


import com.example.athlekin.ui.theme.AthlekinTheme
import androidx.compose.ui.Modifier


import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athlekin.ui.TrackingScreen
import com.example.athlekin.ui.WorkoutViewModel
import com.example.athlekin.ui.WorkoutsScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AthlekinTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AthlekinApp()
                }

            }
        }
    }
}
    @Preview
    @Composable
    // preview of main page
    fun ScreenPreview() {
        AthlekinTheme() {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                AthlekinApp()
            }

        }
    }





