package com.example.athlekin.models

import android.R.attr.name
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.sql.Date

class WorkoutsViewModel : ViewModel() {

    private val _workouts = MutableStateFlow<List<Workout>>(
        listOf(
            Workout(name="Workout1"),
            Workout(name="Workout2")
        )
    )
    val workouts : StateFlow<List<Workout>> = _workouts

    fun addWorkout(workout : Workout) {
        _workouts.value = _workouts.value + workout
    }






}