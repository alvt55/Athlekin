package com.example.athlekin.ui.tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.athlekin.R
import com.example.athlekin.ui.components.ExerciseList
import com.example.athlekin.ui.components.NumberStepper
import com.example.athlekin.ui.components.TextInput
import com.example.athlekin.ui.utils.AthelkinContentType


@Composable
fun TrackingScreen(
    onToWorkoutsClicked: () -> Unit,
    onToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrackingViewModel,
    contentType: AthelkinContentType
) {

    // StateFlow screen state
    val workoutUiState by viewModel.uiState.collectAsState()

    // Compose state from ViewModel
    val currExerciseState = viewModel.currExerciseState



    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_small))
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {

            Button(
                onClick = { viewModel.signOut()
                            onToSignIn()},
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(text = "Sign Out")
            }

            // Exercise name
            TextInput(
                value = currExerciseState.name,
                onValueChange = {
                    viewModel.updateCurrentExerciseState(
                        currExerciseState.copy(name = it)
                    )
                },
                label = R.string.exercise_input
            )

            // Sets + Reps
            Row {
                NumberStepper(
                    value = currExerciseState.sets,
                    onValueChange = {
                        viewModel.updateCurrentExerciseState(
                            currExerciseState.copy(sets = it)
                        )
                    },
                    label = "Sets"
                )

                NumberStepper(
                    value = currExerciseState.reps,
                    onValueChange = {
                        viewModel.updateCurrentExerciseState(
                            currExerciseState.copy(reps = it)
                        )
                    },
                    label = "Reps"
                )
            }


            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::addExercise,
                enabled = currExerciseState.isEntryValid
            ) {
                Text("Submit")
            }


            ExerciseList(workoutUiState.exercises)


            // end workout section
            Column() {
                TextInput(
                    value = workoutUiState.workoutName,
                    onValueChange = { viewModel.updateWorkoutName(it) },
                    label = R.string.workout_input
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.endWorkout() }
                ) {
                    Text("End Workout")
                }

                if (workoutUiState.errorMessage != null) {
                    Text("${workoutUiState.errorMessage}")
                }

            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onToWorkoutsClicked
            ) {
                Text("To Workouts List")
            }




        }

        if (contentType == AthelkinContentType.TRACKER_WITH_WORKOUTS) {
            Text("PLACEHOLDER FOR WORKOUT LIST")
        }
    }

}

