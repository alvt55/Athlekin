package com.example.athlekin.ui.calendar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.athlekin.data.CalendarRepo
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val calendarRepo: CalendarRepo
) : ViewModel() {

    fun getAvailableSlots() {

        viewModelScope.launch {
            calendarRepo.queryCalendars()
        }

    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CalendarViewModel(
                    calendarRepo = CalendarRepo(context)
                )
            }
        }
    }


}