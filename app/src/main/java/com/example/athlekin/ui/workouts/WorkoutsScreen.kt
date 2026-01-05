package com.example.athlekin.ui.workouts


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.athlekin.ui.components.WorkoutList
import com.google.accompanist.permissions.ExperimentalPermissionsApi


const val WORKOUTS_SCREEN_TAG: String = "CALENDAR"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
// list of exercises for the current workout
fun WorkoutsScreen(
    onToTrackingClicked: () -> Unit,
    onShareButton: (String) -> Unit,
    viewModel: WorkoutsScreenViewModel,
    modifier: Modifier = Modifier
) {

    val workouts by viewModel.workouts.collectAsStateWithLifecycle(emptyList())

    val errorMessage = viewModel.errorMessage



    Column {

        if (errorMessage.isNotBlank()) {
            Text(errorMessage)
        }


        WorkoutList(workouts, onDeleteClick = viewModel::deleteWorkout)
        Button(onClick = onToTrackingClicked) {
            Text("To Tracking")
        }

        // share button placeholder
        Button(onClick = { onShareButton("test summary to share") }) {
            Text("Share Workouts")
        }
    }
}


