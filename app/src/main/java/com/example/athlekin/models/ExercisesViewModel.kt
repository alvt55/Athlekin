package com.example.athlekin.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExercisesViewModel(private val repo: ExercisesRepo = ExercisesRepo()) : ViewModel() {

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises : StateFlow<List<Exercise>> = _exercises

    fun loadExercises() {
        viewModelScope.launch {
            _exercises.value = repo.getExercises()
        }
    }

fun addExercise(ex: Exercise) {
    viewModelScope.launch {
        repo.addExercise(ex)
    }
}


}