package com.example.athlekin.ui.workouts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.athlekin.data.AuthRepository
import com.example.athlekin.model.Workout
import com.example.athlekin.data.WorkoutsRepo
import com.example.athlekin.datasource.WorkoutsRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WorkoutsScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val workoutsRepo: WorkoutsRepo
) : ViewModel() {

    var errorMessage by mutableStateOf("")
        private set
    val workouts = workoutsRepo.getWorkouts(authRepository.currentUserIdFlow)

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