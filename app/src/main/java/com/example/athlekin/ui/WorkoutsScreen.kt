package com.example.athlekin.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
// list of exercises for the current workout
fun WorkoutsScreen(onToTrackingClicked : () -> Unit, modifier: Modifier = Modifier) {

    Column() {
        Text("hello")
        Button(onClick=onToTrackingClicked) {
            Text("To Tracking")
        }
    }


}
