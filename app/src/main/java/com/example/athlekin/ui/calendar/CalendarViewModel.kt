package com.example.athlekin.ui.calendar

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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

    fun getAvailableSlots() {
        val (start, end) = datePickerState
        if (start == null || end == null) {
            errorMessage = "Please select a start and end date"
            return
        }
        errorMessage = null
        viewModelScope.launch {
            calendarRepo.queryCalendars(start, end)
        }

    }


}