package com.example.athlekin.ui.workouts


import android.Manifest
import android.R.attr.text
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athlekin.ui.calendar.CalendarViewModel
import com.example.athlekin.ui.workouts.WorkoutsScreenViewModel
import com.example.athlekin.ui.components.WorkoutList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.compose


const val WORKOUTS_SCREEN_TAG: String = "CALENDAR"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
// list of exercises for the current workout
fun WorkoutsScreen(onToTrackingClicked : () -> Unit, onShareButton : (String) -> Unit, viewModel: WorkoutsScreenViewModel, modifier: Modifier = Modifier) {

    val workouts by viewModel.workouts.collectAsStateWithLifecycle(emptyList())

    val errorMessage = viewModel.errorMessage



    Column() {

        if (errorMessage.isNotBlank()) {
            Text(errorMessage)
        }


        WorkoutList(workouts, onDeleteClick = viewModel::deleteWorkout)
        Button(onClick=onToTrackingClicked) {
            Text("To Tracking")
        }

        // share button placeholder
        Button(onClick= { onShareButton("test summary to share") }) {
            Text("Share Workouts")
        }
    }
}


