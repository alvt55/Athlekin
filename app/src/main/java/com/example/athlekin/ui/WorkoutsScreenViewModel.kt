package com.example.athlekin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.athlekin.data.Workout
import com.example.athlekin.data.WorkoutsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutsScreenViewModel(
    private val workoutsRepo: WorkoutsRepo
) : ViewModel(){

    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts = _workouts.asStateFlow()


    init {
        loadWorkouts()
    }

     fun loadWorkouts() {
        viewModelScope.launch{
            _workouts.value = workoutsRepo.getWorkouts()
        }
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WorkoutsScreenViewModel(workoutsRepo = WorkoutsRepo())
            }
        }
    }
}