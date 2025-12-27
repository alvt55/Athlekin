package com.example.athlekin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.athlekin.R

import com.example.athlekin.ui.TrackingViewModel
import com.example.athlekin.ui.components.ExerciseList
import com.example.athlekin.ui.components.NumberStepper

import com.example.athlekin.ui.components.TextInput
import com.example.athlekin.ui.utils.AthelkinContentType
import kotlinx.coroutines.launch


@Composable
fun TrackingScreen(
    onToWorkoutsClicked : () -> Unit,
    onToEndWorkout : () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrackingViewModel = viewModel(),
    contentType: AthelkinContentType) {


    val inputExerciseName = viewModel.inputExerciseName
    val showDoExercise = viewModel.showDoExercise




    val workoutUiState by viewModel.uiState.collectAsState()



    LaunchedEffect(Unit) {
        launch{viewModel.runShowDoExercise()}
    }


    Row(modifier = Modifier.fillMaxSize()) {



        Column(modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_small))
            .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {


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
                onClick = onToEndWorkout
            ) {
                Text("Finish")
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onToWorkoutsClicked
            ) {
                Text("To Workouts List")
            }

            Text(showDoExercise.toString())
        }

        if (contentType == AthelkinContentType.TRACKER_WITH_WORKOUTS) {
            Text("PLACEHOLDER FOR WORKOUT LIST")
        }
    }

}

