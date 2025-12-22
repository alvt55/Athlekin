//package com.example.athlekin.models
//
//import androidx.compose.runtime.MutableState
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.update
//
//interface ExRepo {
//    suspend fun getAllExercises() :  StateFlow<List<Exercise>>
//    suspend fun addExercise(ex: Exercise) : List<Exercise>
//}
//class ExercisesRepo : ExRepo {
//
//    // mutablestateflow - all composables that collectAsState will recomp when exercises changes
//    private val _exercises: MutableStateFlow<List<Exercise>> = MutableStateFlow(
//        listOf(
//            Exercise("Squats", 10, 3),
//            Exercise("bench press", 5, 3)
//        )
//    )
//
//    val exercises: StateFlow<List<Exercise>> = _exercises
//
//    override suspend fun getAllExercises() : StateFlow<List<Exercise>>{
//        return exercises
//    }
//
//    override suspend fun addExercise(ex: Exercise) : List<Exercise> {
//        kotlinx.coroutines.delay(1000)
//        _exercises.update { currentList ->
//            currentList + ex // Creates a new list with the added exercise
//        }
//        return exercises.value
//    }
//
//}