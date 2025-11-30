package com.example.athlekin.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WorkoutsViewModel : ViewModel() {

    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts : StateFlow<List<Workout>> = _workouts

    fun addWorkout(workout : Workout) {
        _workouts.value = _workouts.value + workout
    }

}