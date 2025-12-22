package com.example.athlekin.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.athlekin.R
import com.example.athlekin.ui.components.ExerciseList
import com.example.athlekin.ui.components.NumberStepper

import com.example.athlekin.ui.components.TextInput


@Composable
fun TrackingScreen(onToWorkoutsClicked : () -> Unit,  modifier: Modifier = Modifier, viewModel: WorkoutViewModel = viewModel()) {


    val inputExerciseName = viewModel.inputExerciseName
    val inputWorkoutName = viewModel.inputWorkoutName

    val workoutUiState by viewModel.uiState.collectAsState()


    Column(modifier = modifier
        .fillMaxSize()
        .padding(dimensionResource(R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {


        TextInput(
            value = inputWorkoutName,
            onValueChange = { viewModel.updateWorkoutName(it) },
            label = R.string.workout_input
        )

        TextInput(
            value = inputExerciseName,
            onValueChange = { viewModel.updateExerciseName(it) },
            label = R.string.exercise_input
        )

        Row () {
            NumberStepper(
                value = viewModel.inputSets,
                onValueChange = { viewModel.updateSets(it) },
                label="Sets"
            )
            NumberStepper(
                value = viewModel.inputReps,
                onValueChange = { viewModel.updateReps(it) },
                label="Reps"
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.addExercise()}
        ) {
            Text("Submit")
        }
        ExerciseList(workoutUiState.exercises)

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
            Text("To Workouts List")
        }
    }
}

