package com.example.athlekin.ui.tracker

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.athlekin.R
import com.example.athlekin.model.Exercise
import com.example.athlekin.ui.theme.AthlekinTheme
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
    val trackerUiState = viewModel.trackerUiState
    val currExerciseState = trackerUiState.currExerciseState
    val exercises by viewModel.exercises.collectAsState()
    val latestExercises by viewModel.pastExercisesList.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    val workoutId = viewModel.workoutEditId
    LaunchedEffect(workoutId) {
        viewModel.initEditMode(workoutId)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Action Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.signOut()
                            onToSignIn()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Sign Out", fontSize = 12.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.seedData() },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Seed", fontSize = 12.sp)
                    }
                }
            }

            // Exercise Entry Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "New Exercise",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))

                        // Autofill Exercise Name
                        AutofillTextField(
                            exercises = latestExercises,
                            value = currExerciseState.name,
                            onValueChange = {
                                viewModel.updateCurrentExerciseState(currExerciseState.copy(name = it))
                            },
                            label = stringResource(R.string.exercise_input),
                            viewModel = viewModel
                        )

                        Spacer(Modifier.height(8.dp))

                        // Reps / Sets / Weight Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NumericOutlinedTextField(
                                value = currExerciseState.sets,
                                onValueChange = { viewModel.updateCurrentExerciseState(currExerciseState.copy(sets = it)) },
                                label = "Sets",
                                modifier = Modifier.weight(1f)
                            )
                            NumericOutlinedTextField(
                                value = currExerciseState.reps,
                                onValueChange = { viewModel.updateCurrentExerciseState(currExerciseState.copy(reps = it)) },
                                label = "Reps",
                                modifier = Modifier.weight(1f)
                            )
                            NumericOutlinedTextField(
                                value = currExerciseState.weight,
                                onValueChange = { viewModel.updateCurrentExerciseState(currExerciseState.copy(weight = it)) },
                                label = "Weight",
                                modifier = Modifier.weight(1.2f),
                                suffix = "lbs"
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // Comments
                        var isCommentsFocused by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = currExerciseState.comments,
                            onValueChange = { viewModel.updateCurrentExerciseState(currExerciseState.copy(comments = it)) },
                            label = { Text("Comments (optional)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .onFocusChanged { isCommentsFocused = it.isFocused },
                            minLines = if (isCommentsFocused) 4 else 1,
                            maxLines = 6
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = viewModel::addExercise,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            enabled = viewModel.validateCurrentExerciseState()
                        ) {
                            Text("Add Exercise")
                        }
                    }
                }
            }

            // Current Workout List Header
            if (exercises.isNotEmpty()) {
                item {
                    Text(
                        "Current Workout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(exercises) { exercise ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        ExerciseListItem(
                            exercise = exercise,
                            onDelete = { viewModel.deleteExercise(exercise.roomId) }
                        )
                    }
                }
            }

            // End Workout Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = trackerUiState.workoutName,
                            onValueChange = { viewModel.updateWorkoutName(it) },
                            label = { Text("Workout Name (e.g. Chest Day)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        if (trackerUiState.errorMessage != null) {
                            Text(
                                text = trackerUiState.errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        if (viewModel.plateauMessage.isNotEmpty()) {
                            Text(
                                text = viewModel.plateauMessage,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.endWorkout() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("End Workout", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Secondary Navigation
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onToWorkoutsClicked,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Workouts", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = onToCalendar,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Calendar", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseListItem(exercise: Exercise, onDelete: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(exercise.name, fontWeight = FontWeight.Bold)
                if (exercise.comments.isNotBlank()) {
                    Text(
                        text = if (isExpanded) " -" else " +",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                    )
                }
            }
            Text(
                "${exercise.sets} sets x ${exercise.reps} reps @ ${exercise.weight} lbs",
                style = MaterialTheme.typography.bodySmall
            )
            if (isExpanded && exercise.comments.isNotEmpty()) {
                Text(
                    exercise.comments,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun NumericOutlinedTextField(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    suffix: String? = null
) {
    OutlinedTextField(
        value = if (value == 0) "" else value.toString(),
        onValueChange = { input ->
            if (input.isEmpty()) {
                onValueChange(0)
            } else {
                input.filter { it.isDigit() }.toIntOrNull()?.let { onValueChange(it) }
            }
        },
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        suffix = suffix?.let { { Text(it) } },
        singleLine = true
    )
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
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (value.isNotEmpty()) {
            val filtered = exercises.filter { it.name.startsWith(value, ignoreCase = true) }
            if (filtered.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        filtered.take(3).forEach { exercise ->
                            Text(
                                text = exercise.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
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
                                    .padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
