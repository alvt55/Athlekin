package com.example.athlekin.ui.calendar

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.MaterialTheme
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

        viewModel.errorMessage?.let {
            Text(text = it, color = Color.Red)
        }

//        viewModel.suggestedSlot?.let { slot ->
//            val startTime = SimpleDateFormat("EEEE, MMM d @ h:mm a", Locale.getDefault())
//                .format(Date(slot.first))
//
//            Card(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth()
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Text(
//                        "AI Suggestion:",
//                        style = MaterialTheme.typography.labelLarge
//                    )
//                    Text(
//                        startTime,
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                    Text("Does this time work for you?")
//
//                    Row {
//                        TextButton(onClick = { viewModel.onFeedback(true) }) { Text("Yes") }
//                        TextButton(onClick = { viewModel.onFeedback(false) }) { Text("No") }
//                    }
//                }
//            }
//        }

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
