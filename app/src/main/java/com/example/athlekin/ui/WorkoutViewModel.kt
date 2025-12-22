package com.example.athlekin.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.athlekin.data.Exercise
import com.example.athlekin.data.WorkoutUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WorkoutViewModel : ViewModel(){

    // mutable list?
//    private val _exerciseList = MutableStateFlow<List<Exercise>>(emptyList())
//    val exerciseList : StateFlow<List<Exercise>> = _exerciseList.asStateFlow()

    // not a compose state, therefore needs .collectAsState in the Compose layer
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState : StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    var inputExerciseName by mutableStateOf("")
        private set

    var inputWorkoutName by mutableStateOf("")
        private set

    var inputReps by mutableIntStateOf(1)
        private set
    var inputSets by mutableIntStateOf(1)
        private set

    fun updateExerciseName(name : String){
        inputExerciseName = name
    }


    // TODO: change this to debounce callback
    fun updateWorkoutName(name : String){
        inputWorkoutName = name

        _uiState.update { currentState ->
            currentState.copy(
                name = name
            )
        }
    }



    fun updateReps(reps : Int){
        inputReps = reps
    }

    fun updateSets(sets : Int){
        inputSets = sets
    }


    // given that reps, sets and exerciseName is filled, add exercise to UiState
    fun addExercise() {

        if (inputReps > 0 && inputSets > 0 && inputExerciseName != "") {
            val currExercise = Exercise(inputExerciseName, inputReps, inputSets)

            _uiState.update { currentState ->
                currentState.copy(
                    exercises = currentState.exercises + currExercise
                )
            }

            // reset input fields
            updateExerciseName("")
            updateSets(1)
            updateReps(1)
        }


    }

    // TODO: complete this when data source is added
    // given that the exercise list and name is not empty, reset the Ui data object
    fun endWorkout() {

        if (_uiState.value.exercises.size != 0 && _uiState.value.name != "") {
            _uiState.value = WorkoutUiState()
            updateWorkoutName("")
        }
    }

    internal fun setUiStateForTest(state: WorkoutUiState) {
        _uiState.value = state
    }






}