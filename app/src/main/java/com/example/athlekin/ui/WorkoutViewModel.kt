package com.example.athlekin.ui

import android.R.attr.name
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.athlekin.models.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WorkoutViewModel : ViewModel(){

//    private val _exerciseList = MutableStateFlow<List<Exercise>>(emptyList())
//    val exerciseList : StateFlow<List<Exercise>> = _exerciseList.asStateFlow()

    // not a compose state, therefore needs .collectAsState in the Compose layer
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState : StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    var inputExerciseName by mutableStateOf("")
        private set

    fun updateExerciseName(name : String){
        inputExerciseName = name
    }

    fun addExercise() {
        val currExercise = Exercise(inputExerciseName)

        _uiState.update { currentState ->
            currentState.copy(
                exercises = currentState.exercises + currExercise
            )
        }
    }






}