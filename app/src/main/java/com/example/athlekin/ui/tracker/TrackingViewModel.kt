package com.example.athlekin.ui.tracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.athlekin.data.AuthRepository

import com.example.athlekin.data.Exercise
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


const val TRACKERVM_TAG: String = "TRACKER_VM"

// the entire workout, what has ALREADY been added
data class TrackerUiState(
    val workoutName: String = "",
    val exercises: List<Exercise> = emptyList(),
)

// the current exercise, NOT added yet
data class CurrExerciseState(
    val name: String = "",
    val reps: Int = 1,
    val sets: Int = 1,
    val isEntryValid : Boolean = false,
)


@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    // not a compose state, therefore needs .collectAsState in the Compose layer
    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState : StateFlow<TrackerUiState> = _uiState.asStateFlow()

    var currExerciseState by mutableStateOf(CurrExerciseState())
        private set


    // updating the current exercise fields
    fun updateCurrentExerciseState(details: CurrExerciseState) {
        currExerciseState = details.copy(isEntryValid = validateInput(details))
    }

    // validating the current exercise fields
    fun validateInput(details : CurrExerciseState = currExerciseState) : Boolean {
        return with(details) {
            name.isNotBlank() &&
                    reps > 0 &&
                    sets > 0
        }
    }


//    // TODO: change this to debounce callback
    fun updateWorkoutName(name : String){

        _uiState.update { currentState ->
            currentState.copy(
                workoutName = name
            )
        }
    }


    // given that reps, sets and exerciseName is filled, add exercise to UiState
    fun addExercise() {

        if (currExerciseState.isEntryValid) {
            val currExercise = Exercise(currExerciseState.name, currExerciseState.reps, currExerciseState.sets)

            _uiState.update { currentState ->
                currentState.copy(
                    exercises = currentState.exercises + currExercise
                )
            }

            // reset input fields
            currExerciseState = CurrExerciseState()
        }


    }

    // TODO: complete this when data source is added
    // given that the exercise list and name is not empty, reset the Ui data object
    fun endWorkout() {

        if (_uiState.value.exercises.isNotEmpty() && _uiState.value.workoutName.isNotBlank()) {
            _uiState.update { TrackerUiState() }
            currExerciseState = CurrExerciseState()
        }


    }

    fun signOut() {
        authRepository.signOut()
    }

    internal fun setUiStateForTest(state: TrackerUiState) {
        _uiState.value = state
    }



}



