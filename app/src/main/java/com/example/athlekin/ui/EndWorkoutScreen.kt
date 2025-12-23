package com.example.athlekin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athlekin.R
import com.example.athlekin.ui.components.TextInput


@Composable
fun EndWorkoutScreen(onToWorkoutsClicked : () -> Unit, onToTrackingClicked : () -> Unit, modifier: Modifier = Modifier, viewModel: WorkoutViewModel = viewModel()) {
    val inputWorkoutName = viewModel.inputWorkoutName

    Column() {
        TextInput(
            value = inputWorkoutName,
            onValueChange = { viewModel.updateWorkoutName(it) },
            label = R.string.workout_input
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.endWorkout()}
        ) {
            Text("End Workout")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onToWorkoutsClicked
        ) {
            Text("Workouts")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onToTrackingClicked
        ) {
            Text("Tracking")
        }
    }


}