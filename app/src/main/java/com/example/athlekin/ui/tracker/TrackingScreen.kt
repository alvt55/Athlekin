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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.athlekin.R
import com.example.athlekin.ui.components.ExerciseList
import com.example.athlekin.ui.components.NumberStepper
import com.example.athlekin.ui.utils.AthelkinContentType


@Composable
fun TrackingScreen(
    onToWorkoutsClicked: () -> Unit,
    onToSignIn: () -> Unit,
    onToCalendar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrackingViewModel,
    contentType: AthelkinContentType
) {

    // StateFlow screen state
    val workoutUiState by viewModel.uiState.collectAsState()

    // Compose state from ViewModel
    val currExerciseState = viewModel.currExerciseState
    val exercises by viewModel.exercises.collectAsState()



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
                onClick = {
                    viewModel.signOut()
                    onToSignIn()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(text = "Sign Out")
            }

            // Exercise name
            TextField(
                value = currExerciseState.name,
                onValueChange = {
                    viewModel.updateCurrentExerciseState(
                        currExerciseState.copy(name = it)
                    )
                },
                label = { Text(stringResource(R.string.exercise_input)) }
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


            ExerciseList(
                exercises,
                onExerciseDelete = viewModel::deleteExercise
            )


            // end workout section
            Column {
                TextField(
                    value = workoutUiState.workoutName,
                    onValueChange = { viewModel.updateWorkoutName(it) },
                    label = { Text(stringResource(R.string.workout_input)) }
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

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onToCalendar
            ) {
                Text("To Calendar")
            }


        }

        if (contentType == AthelkinContentType.TRACKER_WITH_WORKOUTS) {
            Text("PLACEHOLDER FOR WORKOUT LIST")
        }
    }

}

