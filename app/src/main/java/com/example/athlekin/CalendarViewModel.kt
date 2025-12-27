package com.example.athlekin

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.athlekin.data.CalendarRepo
import com.example.athlekin.data.WorkoutsRepo
import com.example.athlekin.ui.WorkoutsScreenViewModel
import kotlinx.coroutines.launch

class CalendarViewModel(
    private val calendarRepo: CalendarRepo
) : ViewModel() {

    fun getAvailableSlots() {

        viewModelScope.launch{
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