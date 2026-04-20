package com.example.athlekin.ui.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlekin.data.CalendarRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepo: CalendarRepo,
    private val authRepo: CalendarRepo
) : ViewModel() {

    var datePickerState by mutableStateOf<Pair<Long?, Long?>>(Pair(null, null))
    var showDateRangeModal by mutableStateOf<Boolean>(false)
    var errorMessage by mutableStateOf<String?>(null)

    var slot by mutableStateOf<Pair<Long, Long>?>(null)


    // for now, just println the best available slot based on previous workouts
    fun getAvailableSlots() {
        val (start, end) = datePickerState
        if (start == null || end == null) {
            errorMessage = "Please select a start and end date"
            return
        }
        errorMessage = null
        viewModelScope.launch {
            slot = calendarRepo.queryCalendars(start, end)
        }


    }


}