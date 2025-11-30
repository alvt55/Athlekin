package com.example.athlekin.models
import com.google.firebase.Timestamp

data class Workout(
    val date: Timestamp = Timestamp.now(),
    val name: String = "",
    val exercises: List<Exercise> = emptyList()
)

data class Exercise(
    val name: String = "",
    val reps: Int = 0,
    val sets: Int = 0
)