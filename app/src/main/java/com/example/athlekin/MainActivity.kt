package com.example.athlekin


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.fillMaxSize


import androidx.compose.runtime.Composable

import androidx.compose.ui.tooling.preview.Preview



import com.example.athlekin.ui.theme.AthlekinTheme
import androidx.compose.ui.Modifier



import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AthlekinTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val windowSize = calculateWindowSizeClass(this)
                    AthlekinApp(
                        windowSize = windowSize.widthSizeClass
                    )
                }

            }
        }
    }
}
@Preview(showBackground = true)
@Composable
// preview of main page
fun CompactScreenPreview() {
    AthlekinTheme() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AthlekinApp(windowSize = WindowWidthSizeClass.Compact)
        }

    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
// preview of main page
fun MediumScreenPreview() {
    AthlekinTheme() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AthlekinApp(windowSize = WindowWidthSizeClass.Medium)
        }

    }
}

@Preview(showBackground = true, widthDp = 1000)
@Composable
// preview of main page
fun ExpandedScreenPreview() {
    AthlekinTheme() {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AthlekinApp(windowSize = WindowWidthSizeClass.Expanded)
        }

    }
}





