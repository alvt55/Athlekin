package com.example.athlekin.ui.workouts


import android.Manifest
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athlekin.ui.calendar.CalendarViewModel
import com.example.athlekin.ui.workouts.WorkoutsScreenViewModel
import com.example.athlekin.ui.components.WorkoutList
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


const val WORKOUTS_SCREEN_TAG: String = "CALENDAR"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
// list of exercises for the current workout
fun WorkoutsScreen(onToTrackingClicked : () -> Unit, onShareButton : (String) -> Unit, modifier: Modifier = Modifier) {

//    val workoutsViewModel : WorkoutsScreenViewModel = viewModel(factory= WorkoutsScreenViewModel.Factory)
//    val workoutsList by workoutsViewModel.workouts.collectAsState()

//    Log.i(WORKOUTS_SCREEN_TAG, workoutsList.toString())


    val calendarViewModel : CalendarViewModel = viewModel(factory= CalendarViewModel.factory(LocalContext.current))

    val calendarPermissionState = rememberPermissionState(
        Manifest.permission.READ_CALENDAR
    )

    // Auto-request permission on first load
    LaunchedEffect(Unit) {
        if (!calendarPermissionState.status.isGranted) {
            Log.d(WORKOUTS_SCREEN_TAG,"Auto-requesting calendar permission")
            calendarPermissionState.launchPermissionRequest()
        }
    }

    // Call API when permission is granted
    LaunchedEffect(calendarPermissionState.status.isGranted) {
        Log.d(WORKOUTS_SCREEN_TAG, "Permission status: ${calendarPermissionState.status.isGranted}")
        if (calendarPermissionState.status.isGranted) {
            Log.d(WORKOUTS_SCREEN_TAG, "Calling getAvailableSlots()")
            calendarViewModel.getAvailableSlots()
        }
    }

//
//
//    Column() {
//
//        WorkoutList(workoutsList)
//        Button(onClick=onToTrackingClicked) {
//            Text("To Tracking")
//        }
//
//        // share button placeholder
//        Button(onClick= { onShareButton("test summary to share") }) {
//            Text("Share Workouts")
//        }
//    }
}


