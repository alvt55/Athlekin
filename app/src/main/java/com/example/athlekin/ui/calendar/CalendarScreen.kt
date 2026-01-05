package com.example.athlekin.ui.calendar

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.athlekin.ui.workouts.WORKOUTS_SCREEN_TAG
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CalendarScreen(
    onToTrackingClicked: () -> Unit,
    viewModel: CalendarViewModel
) {


    val calendarPermissionState = rememberPermissionState(
        Manifest.permission.READ_CALENDAR
    )

    // Auto-request permission on first load
    LaunchedEffect(Unit) {
        if (!calendarPermissionState.status.isGranted) {
            Log.d(WORKOUTS_SCREEN_TAG, "Auto-requesting calendar permission")
            calendarPermissionState.launchPermissionRequest()
        }
    }

//    // Call API when permission is granted
//    LaunchedEffect(calendarPermissionState.status.isGranted) {
//        Log.d(WORKOUTS_SCREEN_TAG, "Permission status: ${calendarPermissionState.status.isGranted}")
//        if (calendarPermissionState.status.isGranted) {
//            Log.d(WORKOUTS_SCREEN_TAG, "Calling getAvailableSlots()")
//            calendarViewModel.getAvailableSlots()
//        }
//    }

    if (viewModel.showDateRangeModal) {
        DateRangePickerModal(
            onDateRangeSelected = {
                viewModel.datePickerState = it
                viewModel.getAvailableSlots()
            },
            onDismiss = { viewModel.showDateRangeModal = false }
        )
    }

    Column {
        Button(
            onClick = { viewModel.showDateRangeModal = true }
        ) {
            Text("Pick date range")
        }

        Button(
            onClick = { viewModel.getAvailableSlots() }
        ) {
            Text("Generate time slots")
        }

        viewModel.errorMessage?.let {
            Text(text = it, color = Color.Red)
        }

        Button(
            onClick = onToTrackingClicked
        ) {
            Text("To Tracking")
        }
    }


}

@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}