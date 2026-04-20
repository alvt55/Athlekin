package com.example.athlekin.ui.tracker




import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.athlekin.model.Exercise

@Composable
fun TrackingScreen(
    onToWorkoutsClicked: () -> Unit,
    onToSignIn: () -> Unit,
    onToCalendar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrackingViewModel,
    contentType: AthelkinContentType
) {


    // Compose state from ViewModel
    val trackerUiState = viewModel.trackerUiState
    val currExerciseState = trackerUiState.currExerciseState
    val exercises by viewModel.exercises.collectAsState()

    // for autofill
    val latestExercises by viewModel.pastExercisesList.collectAsStateWithLifecycle()

    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current


    // TODO: add this
    val workoutId = viewModel.workoutEditId

    LaunchedEffect(workoutId) {
        viewModel.initEditMode(workoutId)
    }



    Row(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            }
    ) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_small))
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {

            Row {
                Button(
                    onClick = {
                        viewModel.signOut()
                        onToSignIn()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(2.dp),
                ) {
                    Text(text = "Sign Out")
                }

                Button(
                    onClick = { viewModel.seedData() },
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "Seed Data")
                }

                // for debug, delete later
                Text(text = viewModel.userId)
            }



            // Exercise Input fields
            Column {
                Row{
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


                OutlinedTextField(
                    value = currExerciseState.weight.toString(),
                    onValueChange = { input ->
                        val number = input.toIntOrNull()
                        if (number != null || input.isEmpty()) {
                            viewModel.updateCurrentExerciseState(
                                currExerciseState.copy(weight = number ?: 0)
                            )
                        }
                    },
                    label = { Text("Weight") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = currExerciseState.comments,
                    onValueChange = {
                        viewModel.updateCurrentExerciseState(
                            currExerciseState.copy(comments = it)
                        )
                    },
                    label = { Text("Comments") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .onFocusChanged { isFocused = it.isFocused },
                    minLines = if (isFocused) 4 else 1,
                    maxLines = 6
                )


            }


            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::addExercise,
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
                    value = trackerUiState.workoutName,
                    onValueChange = { viewModel.updateWorkoutName(it) },
                    label = { Text(stringResource(R.string.workout_input)) }
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.endWorkout() }
                ) {
                    Text("End Workout")
                }

                if (trackerUiState.errorMessage != null) {
                    Text(trackerUiState.errorMessage)
                }

                Text(viewModel.plateauMessage)

            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onToWorkoutsClicked
            ) {
                Text("To Workouts List")
            }

            Button(
                modifier = Modifier.fillMaxWidth().size(1.dp),
                onClick = onToCalendar
            ) {
                Text("To Calendar")
            }

            // TODO - put this in the middle of screen, dropdown doesn't ovelay
            AutofillTextField(
                value = currExerciseState.name,
                onValueChange = {
                    viewModel.updateCurrentExerciseState(
                        currExerciseState.copy(name = it)
                    )
                },
                exercises = latestExercises,
                label = stringResource(R.string.exercise_input),
                viewModel = viewModel
            )


        }

        if (contentType == AthelkinContentType.TRACKER_WITH_WORKOUTS) {
            Text("PLACEHOLDER FOR WORKOUT LIST")
        }
    }

}

@Composable
fun AutofillTextField(
    exercises: List<Exercise>,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    viewModel: TrackingViewModel,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )
            LazyColumn {
                items(exercises.filter {
                    it.name.startsWith(value, ignoreCase = true)
                }) { exercise ->
                    Text(
                        text = exercise.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                viewModel.updateCurrentExerciseState(
                                    CurrExerciseState(
                                        name = exercise.name,
                                        sets = exercise.sets,
                                        reps = exercise.reps,
                                        weight = exercise.weight,
                                        comments = exercise.comments
                                    )
                                )
                                viewModel.exercisePlateauMessage(exercise.name)
                            }
                            .padding(8.dp)
                    )

                }
            }

    }
}