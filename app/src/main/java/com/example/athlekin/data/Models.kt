package com.example.athlekin.data

data class Workout(
    val name: String = "",
    val exercises: List<Exercise> = emptyList()
)

data class Exercise(
    val name: String = "",
    val reps: Int = 1,
    val sets: Int = 1,
)