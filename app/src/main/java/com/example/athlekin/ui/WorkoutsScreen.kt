package com.example.athlekin.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
// list of exercises for the current workout
fun WorkoutsScreen(onToTrackingClicked : () -> Unit, onShareButton : (String) -> Unit, modifier: Modifier = Modifier) {

    Column() {
        Text("hello")
        Button(onClick=onToTrackingClicked) {
            Text("To Tracking")
        }

        // share button placeholder
        Button(onClick= { onShareButton("test summary to share") }) {
            Text("Share Workouts")
        }
    }


}


@Composable
fun WorkoutList(modifier: Modifier = Modifier) {

    Text("PLACEHOLDER FOR WORKOUTS")
}
