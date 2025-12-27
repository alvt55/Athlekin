package com.example.athlekin.data



data class WorkoutUiState(
    val name: String = "",
    val exercises: List<Exercise> = emptyList()
)

