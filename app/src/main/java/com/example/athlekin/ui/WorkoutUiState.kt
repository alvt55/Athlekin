package com.example.athlekin.ui

import com.example.athlekin.models.Exercise


data class WorkoutUiState(
    val name: String = "",
    val exercises: List<Exercise> = emptyList()
)
