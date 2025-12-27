package com.example.athlekin.ui.screens


import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athlekin.data.Workout
import com.example.athlekin.ui.TrackingViewModel
import com.example.athlekin.ui.WorkoutsScreenViewModel
import com.example.athlekin.ui.components.ExerciseItem
import com.example.athlekin.ui.components.WorkoutList


@Composable
// list of exercises for the current workout
fun WorkoutsScreen(onToTrackingClicked : () -> Unit, onShareButton : (String) -> Unit, modifier: Modifier = Modifier) {

    val workoutsViewModel : WorkoutsScreenViewModel = viewModel(factory= WorkoutsScreenViewModel.Factory)
    val workoutsList by workoutsViewModel.workouts.collectAsState()

    Log.i("WORKOUTS SCREEN", workoutsList.toString())
    Column() {

        WorkoutList(workoutsList)
        Button(onClick=onToTrackingClicked) {
            Text("To Tracking")
        }

        // share button placeholder
        Button(onClick= { onShareButton("test summary to share") }) {
            Text("Share Workouts")
        }
    }
}


