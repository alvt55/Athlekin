package com.example.athlekin.ui.tracker

import android.R.attr.prompt
import android.util.Log.e
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athlekin.data.AuthRepository
import com.example.athlekin.data.DataSeeder
import com.example.athlekin.data.GeminiRepo
import com.example.athlekin.data.WorkoutsRepo
import com.example.athlekin.model.Exercise
import com.example.athlekin.model.PlateauAnalysis
import com.example.athlekin.model.WorkoutDoc
import com.example.athlekin.room.ExerciseEntity
import com.example.athlekin.room.OfflineExercisesRepo
import com.example.athlekin.ui.workouts.toWorkout
import com.google.common.io.Files.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
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


@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val workoutsRepo: WorkoutsRepo,
    private val offlineExercisesRepo: OfflineExercisesRepo,
    private val geminiRepo: GeminiRepo,
    private val dataSeeder: DataSeeder
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
                started = SharingStarted.Eagerly,
                initialValue = emptyList()

            )

    var workoutEditId by mutableStateOf("")
        private set

    val exercisesByName: StateFlow<Map<String, List<Exercise>>> = workoutsRepo
        .getExercisesByName(authRepository.currentUserIdFlow)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    // list of latest objects per exercise
    val pastExercisesList: StateFlow<List<Exercise>> =
        exercisesByName
            .map { map ->
                map.values.map { it.last() } // latest entry per exercise
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    var plateauMessage by mutableStateOf("")
        private set

    // userId is for debugging, delete later
    var userId by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            authRepository.currentUserIdFlow.collect { id ->
                userId = id ?: ""
            }
        }
    }
    //


    fun seedData() {
        viewModelScope.launch {
            try {
                dataSeeder.seedWorkouts()
                trackerUiState = trackerUiState.copy(errorMessage = "Seed successful!")
            } catch (e: Exception) {
                trackerUiState = trackerUiState.copy(errorMessage = "Seed failed: ${e.message}")
            }
        }


    }


    // REQUIRES: exercise name
    // EFFECTS: determines whether a plateau has occurred given an exercise name,
    // returns a personalized message based on plateau reason and improvements to be made
    // returns a general message using volume calculations if no plateau has occurred
    fun exercisePlateauMessage(name: String) {
        val history = exercisesByName.value[name] ?: return

        val volumes = history.map { it.weight.toDouble() * it.reps * it.sets }

        val recent = volumes.takeLast(3)
        val initialVolume = recent.first()
        val finalVolume = recent.last()

        val recentComments = history.takeLast(3).map { it.comments }
        val percentageChange = (finalVolume - initialVolume) / initialVolume

        // Need at least 3 valid sessions to determine a trend
        if (history.size < 3 || initialVolume <= 0.0) {
            println("DEBUG: Not enough data to determine plateau")
            return
        }

        // If increase is less than 5% over 3 sessions, consider it a plateau
        if (percentageChange < 0.05) {

            val analysis = PlateauAnalysis(
                exerciseName = name,
                historySize = history.size,
                volumes = volumes,
                recentVolumes = recent,
                initialVolume = initialVolume,
                finalVolume = finalVolume,
                percentageChange = percentageChange,
                recentComments = recentComments
            )

            viewModelScope.launch {
                val plateauMessage = geminiRepo.generatePlateauMessage(analysis)
                println("DEBUG: Plateau message: $plateauMessage")
                // TODO: set plateau message for UI
            }

        } else {
            plateauMessage =
                "Great job! Your $name volume is up by ${(percentageChange * 100).toInt()}%."
        }
    }


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
                    sets > 0
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
            trackerUiState =
                trackerUiState.copy(currExerciseState = CurrExerciseState(), errorMessage = null)
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

        viewModelScope.launch {
            val uid = authRepository.currentUserIdFlow.first()

            if (uid.isNullOrBlank()) {

                trackerUiState = trackerUiState.copy(errorMessage = "Please login first")
            } else {
                if (exercises.value.isNotEmpty() && trackerUiState.workoutName.isNotBlank()) {
                    // add workout based on UI fields
                    val workoutDocToAdd = WorkoutDoc(
                        ownerId = uid,
                        name = trackerUiState.workoutName,
                        exercises = exercises.value.map {
                            it.copy(roomId = 0)  // explicitly reset it to 0
                        }
                    )

                    try {

                        if (workoutEditId.isNotBlank() && workoutsRepo.getWorkout(workoutEditId) != null) {
                            workoutsRepo.updateWorkout(workoutDocToAdd.copy(id = workoutEditId))
                            workoutEditId = ""
                        } else {
                            workoutsRepo.createWorkout(workoutDocToAdd)
                        }

                        // reset UI state and local storage after adding
                        offlineExercisesRepo.deleteAllExercises()
                        trackerUiState = trackerUiState.copy(
                            workoutName = "",
                            currExerciseState = CurrExerciseState(),
                            errorMessage = null
                        )
                    } catch (e: Exception) {
                        // error saving workout
                        trackerUiState =
                            trackerUiState.copy(errorMessage = "Error saving workout to database")
                    }


                } else {
                    // Missing workout name or exercises
                    trackerUiState =
                        trackerUiState.copy(errorMessage = "Please enter a workout name and add at least one exercise")
                }
            }
        }

    }


    // MODIFIES: ExerciseDatabase (room)
    // EFFECTS: delete exercise from Room based on room_id
    // ERROR MESSAGE: issues with the Firebase deletion
    fun deleteExercise(roomId: Int) {
        viewModelScope.launch {
            try {
                offlineExercisesRepo.deleteExerciseById(roomId)
            } catch (e: Exception) {
                trackerUiState = trackerUiState.copy(errorMessage = "Error deleting exercise")
            }

        }
    }


    // EFFECTS: calls signout function for auth repo
    // ERROR MESSAGE: issues with the Firebase sign out
    fun signOut() {
        try {
            authRepository.signOut()
        } catch (e: Exception) {
            trackerUiState = trackerUiState.copy(errorMessage = "Error signing out")
        }

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
