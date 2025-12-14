package com.example.athlekin.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athlekin.models.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ExercisesViewModel(private val repo: ExercisesRepo = ExercisesRepo()) : ViewModel() {


    object testData {
        val exerciseList = listOf(
            Exercise("Squats", 10, 3),
            Exercise("bench press", 5, 3) ,
            Exercise("Squats", 10, 3),
            Exercise("bench press", 5, 3),
            Exercise("Squats", 10, 3),
            Exercise("bench press", 5, 3),
            Exercise("Squats", 10, 3),
            Exercise("bench press", 5, 3)

        )
    }


//    private val _exercises = repo.getAllExercises()
    private val _exercises = MutableStateFlow(
    listOf(
        Exercise("Squats", 10, 3),
        Exercise("bench press", 5, 3)
    ))

    val exercises : StateFlow<List<Exercise>> = _exercises

    val exs = listOf(
        Exercise("Squats", 10, 3),
        Exercise("bench press", 5, 3)
    )


    // prac
//    val highVolExercises : List<Exercise> = exs.filter{
//        it.reps * it.sets > 60
//    }




    fun addExercise(ex: Exercise) {
        viewModelScope.launch {
            repo.addExercise(ex)
        }
    }


}