package com.example.athlekin.ui.tracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlekin.data.AuthRepository
import com.example.athlekin.data.WorkoutsRepo
import com.example.athlekin.model.Exercise
import com.example.athlekin.room.ExerciseEntity
import com.example.athlekin.room.OfflineExercisesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


// state about the entire workout
data class TrackerUiState(
    val workoutName: String = "",
    val errorMessage: String? = null,
    val currExerciseState: CurrExerciseState = CurrExerciseState(),
)

// the current exercise fields, NOT added yet
data class CurrExerciseState(
    val name: String = "",
    val reps: Int = 1,
    val sets: Int = 1,
    val weight: Int = 1,
    val comments: String = "",
)


// TODO: change TrackerUiState into a mutableStateOf
// TODO: remove isEntryValid, addExercise() should just check and return error message
// TODO: put existing exercises in WorkoutsRepo (don't implement)
// TODO: accommodate for update workout (most likely using this viewmodel but a new route)
// TODO: implementation comments for all functions, verify through ai

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val workoutsRepo: WorkoutsRepo,
    private val offlineExercisesRepo: OfflineExercisesRepo
) : ViewModel() {

    var trackerUiState by mutableStateOf(TrackerUiState())
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

    var workoutEditId by mutableStateOf("")
        private set


    // REQUIRES: workoutId is the Firebase id of the workout to edit, or empty string if not editing
    // MODIFIES: trackerUiState, workoutId, ExerciseDatabase (room)
    // EFFECTS: overwrites the TrackerUiState with the workout, overwrites the room database with the workout's exercises,
    //          remember the workoutId in this viewModel state
    fun initEditMode(workoutId: String) {

        if (workoutId.isNotBlank()) {




            viewModelScope.launch {
                val workout = workoutsRepo.getWorkout(workoutId)

                if (workout != null) {
                    workoutEditId = workoutId
                    trackerUiState = trackerUiState.copy(
                        workoutName = workout.name,
                        currExerciseState = CurrExerciseState()
                    )

                    offlineExercisesRepo.deleteAllExercises()

                    workout.exercises.map { exercise ->
                        offlineExercisesRepo.createExercise(
                            ExerciseEntity(
                                name = exercise.name,
                                reps = exercise.reps,
                                sets = exercise.sets,
                                weight = exercise.weight,
                                comments = exercise.comments
                            )
                        )

                    }
                } else {
                    trackerUiState = trackerUiState.copy(errorMessage = "Invalid Workout to Edit")
                }


            }
        }

    }


    // for autofill feature
    // move to workouts repo
//    val existingExercises = workoutsRepo.getWorkouts(authRepository.currentUserIdFlow)
//        .map { workouts ->
//            workouts.flatMap { it.exercises }   // get all exercises from all workouts
//                .map { it.name }                // extract their names
//                .toSet()                        // remove duplicates
//                .toList()                       // convert to list for autocomplete
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = emptyList()
//        )


    // MODIFIES: trackerUiState
    // EFFECTS: overwrite the viewmodel state to update the current exercise state
    fun updateCurrentExerciseState(details: CurrExerciseState) {
        trackerUiState = trackerUiState.copy(currExerciseState = details)
    }

    // REQUIRES: details is the exercise state to validate
    // EFFECTS: returns whether or not this exercise state can be inserted into the room database
    fun validateCurrentExerciseState(details: CurrExerciseState = trackerUiState.currExerciseState): Boolean {
        return with(details) {
            name.isNotBlank() &&
                    reps > 0 &&
                    sets > 0 &&
                    weight > 0
        }


    }


    // EFFECTS: updates the workout name for this session
    fun updateWorkoutName(name: String) {
        trackerUiState = trackerUiState.copy(workoutName = name)
    }


    // MODIFIES: trackerUiState, ExerciseDatabase (room)
    // EFFECTS: checks if the exercise is valid, and if so, adds to the room database, clears the current exercise ui state
    // ERROR MESSAGES: when exercise is invalid
    fun addExercise() {

        if (validateCurrentExerciseState(trackerUiState.currExerciseState)) {

            val currExerciseState = trackerUiState.currExerciseState

            val currExercise = ExerciseEntity(
                name = currExerciseState.name,
                reps = currExerciseState.reps,
                sets = currExerciseState.sets,
                weight = currExerciseState.weight,
                comments = currExerciseState.comments
            )


            viewModelScope.launch {
                offlineExercisesRepo.createExercise(
                    currExercise
                )
            }


            // reset input fields
            trackerUiState = trackerUiState.copy(currExerciseState = CurrExerciseState())
        } else {
            trackerUiState = trackerUiState.copy(errorMessage = "Invalid Exercise")
        }


    }

    // MODIFIES: trackerUiState, ExerciseDatabase (room), WorkoutDoc (Firebase)
    // EFFECTS: if authenticated and has a workout name and exercises, saves the workout to Firebase,
    //          clear the Room database and UI state
    //          if the workoutId exists, do update workout instead of create workout, clear the workoutId field
    // ERROR MESSAGES: workout is invalid, user is not signed in, workout fails to save/update
    fun endWorkout() {

//        viewModelScope.launch {
//            authRepository.currentUserIdFlow.collect { uid ->
//
//                println("uid: $uid")
//
//                if (uid.isNullOrBlank()) {
////                    _uiState.update { it.copy(errorMessage = "You must be signed in to save a workout.") }
//                } else {
//                    if (exercises.value.isNotEmpty() && _uiState.value.workoutName.isNotBlank()) {
//                        // add workout based on UI fields
//                        val workoutDocToAdd = WorkoutDoc(
//                            ownerId = uid,
//                            name = _uiState.value.workoutName,
//                            exercises = exercises.value.map {
//                                it.copy(roomId = 0)  // explicitly reset it to 0
//                            }
//                        )
//
//                        try {
//                            workoutsRepo.createWorkout(workoutDocToAdd)
//
//                            // reset UI state and local storage after adding
//                            offlineExercisesRepo.deleteAllExercises()
////                            _uiState.update { TrackerUiState() }
//                            currExerciseState = CurrExerciseState()
//                        } catch (e: Exception) {
//                            // error saving workout
////                            _uiState.update { it.copy(errorMessage = "Failed to save workout: ${e.message}") }
//                        }
//
//
//                    } else {
//                        // Missing workout name or exercises
////                        _uiState.update { it.copy(errorMessage = "Please enter a workout name and add at least one exercise.") }
//                    }
//                }
//            }
    }


    // MODIFIES: ExerciseDatabase (room)
    // EFFECTS: delete exercise from Room based on room_id
    // ERROR MESSAGE: issues with the Firebase deletion
    fun deleteExercise(roomId: Int) {
//        viewModelScope.launch {
//            offlineExercisesRepo.deleteExerciseById(roomId)
//        }
    }


    // EFFECTS: calls signout function for auth repo
    // ERROR MESSAGE: issues with the Firebase sign out
    fun signOut() {

//        try {
//            authRepository.signOut()
//        } catch (e: Exception) {
////            _uiState.update { it.copy(errorMessage = "Failed to sign out: ${e.message}") }
//        }

    }


    fun ExerciseEntity.toExercise(): Exercise {
        return Exercise(
            roomId = id,
            name = this.name,
            reps = this.reps,
            sets = this.sets,
            weight = this.weight,
            comments = this.comments
        )
    }


}






