package com.example.athlekin.ui.workouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.athlekin.model.Exercise
import com.example.athlekin.model.Workout
import com.example.athlekin.ui.theme.AthlekinTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val WORKOUTS_SCREEN_TAG: String = "CALENDAR"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WorkoutsScreen(
    onToTrackingClicked: () -> Unit,
    onShareButton: (String) -> Unit,
    viewModel: WorkoutsScreenViewModel,
    modifier: Modifier = Modifier
) {
    val workouts by viewModel.workouts.collectAsStateWithLifecycle(emptyList())
    val errorMessage = viewModel.errorMessage

    var workoutToDelete by remember { mutableStateOf<Workout?>(null) }

    if (workoutToDelete != null) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deleteWorkout(workoutToDelete!!.id)
                workoutToDelete = null
            },
            onDismiss = { workoutToDelete = null }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            items(workouts) { workout ->
                WorkoutCard(
                    workout = workout,
                    onDelete = { workoutToDelete = workout }
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = onToTrackingClicked, modifier = Modifier.fillMaxWidth()) {
                Text("To Tracking")
            }
            Button(
                onClick = { onShareButton("Check out my workout history on Athlekin!") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share Workouts")
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Are you sure you would like to delete?",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Yes")
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("No")
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutCard(workout: Workout, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = SimpleDateFormat(
                    "MMM d, yyyy",
                    Locale.getDefault()
                ).format(workout.createdAt.toDate()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            workout.exercises.forEach { exercise ->
                ExerciseItem(exercise)
            }

            Button(
                onClick = onDelete,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun ExerciseItem(exercise: Exercise) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${exercise.name}: ${exercise.sets} x ${exercise.reps} @ ${exercise.weight}lbs",
                style = MaterialTheme.typography.bodyMedium
            )
            if (exercise.comments.isNotBlank()) {
                Text(
                    text = if (isExpanded) " -" else " +",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )
            }
        }

        if (isExpanded && exercise.comments.isNotBlank()) {
            Text(
                text = exercise.comments,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkoutListPreview() {
    val sampleWorkouts = listOf(
        Workout(
            id = "1",
            name = "Morning Push",
            createdAt = Timestamp.now(),
            exercises = listOf(
                Exercise(name = "Bench Press", reps = 10, sets = 3, weight = 135),
                Exercise(
                    name = "Incline Flys",
                    reps = 12,
                    sets = 3,
                    weight = 25,
                    comments = "Felt a bit of a stretch in the shoulders today."
                )
            )
        ),
        Workout(
            id = "2",
            name = "Heavy Leg Day",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 86400000)),
            exercises = listOf(
                Exercise(
                    name = "Squats",
                    reps = 5,
                    sets = 5,
                    weight = 225,
                    comments = "This is a really long comment to test the see more functionality. I felt really strong today and decided to push the weight a bit. The form was consistent throughout all sets, but the last rep was tough. I should focus on core stability next time to ensure I don't lean too far forward during the ascent."
                )
            )
        ),
        Workout(
            id = "3",
            name = "Back & Bis",
            createdAt = Timestamp(Date(System.currentTimeMillis() - 172800000)),
            exercises = listOf(
                Exercise(name = "Deadlifts", reps = 5, sets = 1, weight = 315),
                Exercise(name = "Hammer Curls", reps = 12, sets = 3, weight = 30)
            )
        )
    )

    AthlekinTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(sampleWorkouts) { workout ->
                    WorkoutCard(workout = workout, onDelete = {})
                }
            }
        }
    }
}
