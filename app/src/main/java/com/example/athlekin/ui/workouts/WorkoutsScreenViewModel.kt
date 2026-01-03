package com.example.athlekin.ui.workouts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlekin.data.AuthRepository
import com.example.athlekin.data.WorkoutsRepo
import com.example.athlekin.model.Workout
import com.example.athlekin.model.WorkoutDoc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WorkoutsScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val workoutsRepo: WorkoutsRepo
) : ViewModel() {

    var errorMessage by mutableStateOf("")
        private set


    val workouts = workoutsRepo
        .getWorkouts(authRepository.currentUserIdFlow)
        .map { workoutDocs -> workoutDocs.map { it.toWorkout() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    fun deleteWorkout(id : String) {

        viewModelScope.launch {

            try {
                workoutsRepo.deleteWorkout(id)
            } catch (e: Exception) {
                errorMessage = "Failed to delete workout: ${e.message}"
            }
        }

    }

}

fun WorkoutDoc.toWorkout(): Workout {
    return Workout(
        id = this.id,
        ownerId = this.ownerId,
        createdAt = this.createdAt,
        name = this.name,
        exercises = this.exercises
    )
}