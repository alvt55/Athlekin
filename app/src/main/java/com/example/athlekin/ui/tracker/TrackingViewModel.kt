package com.example.athlekin.ui.tracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlekin.data.AuthRepository
import com.example.athlekin.data.WorkoutsRepo

import com.example.athlekin.model.Exercise
import com.example.athlekin.model.WorkoutDoc
import com.example.athlekin.room.ExerciseEntity
import com.example.athlekin.room.OfflineExercisesRepo
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


// state about the entire workout
data class TrackerUiState(
    val workoutName: String = "",
    val errorMessage: String? = null
)

// the current exercise fields, NOT added yet
data class CurrExerciseState(
    val name: String = "",
    val reps: Int = 1,
    val sets: Int = 1,
    val isEntryValid: Boolean = false,
)


@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val workoutsRepo: WorkoutsRepo,
    private val offlineExercisesRepo: OfflineExercisesRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    var currExerciseState by mutableStateOf(CurrExerciseState())
        private set

    // Exercise objects, created from the room database ExerciseEntity -> Exercise
    val exercises: StateFlow<List<Exercise>> =
        offlineExercisesRepo
            .getAllExercisesStream()
            .map { list -> list.map { it.toExercise() } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )


    // updating the current exercise fields
    fun updateCurrentExerciseState(details: CurrExerciseState) {
        currExerciseState = details.copy(isEntryValid = validateInput(details))
    }

    // validating the current exercise fields
    fun validateInput(details: CurrExerciseState = currExerciseState): Boolean {
        return with(details) {
            name.isNotBlank() &&
                    reps > 0 &&
                    sets > 0
        }
    }


    // update workout name field
    fun updateWorkoutName(name: String) {

        _uiState.update { currentState ->
            currentState.copy(
                workoutName = name
            )
        }
    }


    // given that reps, sets and exerciseName is filled, add exercise to UiState
    // add this exercise to the offline_db Room DB for local persistence
    fun addExercise() {

        if (currExerciseState.isEntryValid) {
            val currExercise = ExerciseEntity(
                name = currExerciseState.name,
                reps = currExerciseState.reps,
                sets = currExerciseState.sets
            )


            viewModelScope.launch {
                offlineExercisesRepo.createExercise(
                    currExercise
                )
            }


            // reset input fields
            currExerciseState = CurrExerciseState()
        }


    }

    // if user is authenticated, exercise list and name is not empty, add workout to Firestore
    //
    fun endWorkout() {
        val ownerId = authRepository.currentUser?.uid

        if (ownerId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "You must be signed in to save a workout.") }
            return
        }

        if (exercises.value.isNotEmpty() && _uiState.value.workoutName.isNotBlank()) {
            // add workout based on UI fields
            val workoutDocToAdd = WorkoutDoc(
                ownerId = ownerId,
                name = _uiState.value.workoutName,
                exercises = exercises.value.map {
                    Exercise(name = it.name, reps = it.reps, sets = it.sets) // room_id set to 0
                }
            )

            viewModelScope.launch {
                try {
                    workoutsRepo.createWorkout(workoutDocToAdd)

                    // reset UI state and local storage after adding
                    offlineExercisesRepo.deleteAllExercises()
                    _uiState.update { TrackerUiState() }
                    currExerciseState = CurrExerciseState()
                } catch (e: Exception) {
                    // error saving workout
                    _uiState.update { it.copy(errorMessage = "Failed to save workout: ${e.message}") }
                }
            }


        } else {
            // Missing workout name or exercises
            _uiState.update { it.copy(errorMessage = "Please enter a workout name and add at least one exercise.") }
        }
    }

    // delete exercise from Room based on room_id
    fun deleteExercise(roomId: Int) {
        viewModelScope.launch {
            offlineExercisesRepo.deleteExerciseById(roomId)
        }
    }


    fun signOut() {

        try {
            authRepository.signOut()
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Failed to sign out: ${e.message}") }
        }

    }

    internal fun setUiStateForTest(state: TrackerUiState) {
        _uiState.value = state
    }

    fun ExerciseEntity.toExercise(): Exercise {
        return Exercise(
            roomId = id,
            name = this.name,
            reps = this.reps,
            sets = this.sets
        )
    }


}



