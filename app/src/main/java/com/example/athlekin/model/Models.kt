package com.example.athlekin.model

import android.R.attr.name
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId


// firebase
data class WorkoutDoc(
    @DocumentId val id: String = "",
    val ownerId: String? = "",
    val createdAt: Timestamp = Timestamp.now(),
    val name: String = "",
    val exercises: List<Exercise> = emptyList()
)


data class PlateauAnalysis(
    val exerciseName: String,
    val historySize: Int,
    val volumes: List<Double>,
    val recentVolumes: List<Double>,
    val initialVolume: Double,
    val finalVolume: Double,
    val percentageChange: Double,
    val recentComments: List<String>
)

// firebase and UI
data class Exercise(
    val roomId: Int = 0, // will be set to 0 when saved to Firebase, and reassigned when being updated
    val name: String = "",
    val reps: Int = 1,
    val sets: Int = 1,
    val weight: Int = 1,
    val comments: String = ""
)


// UI 

data class Workout(
    val id: String = "",
    val ownerId: String? = "",
    val createdAt: Timestamp = Timestamp.now(),
    val name: String = "",
    val exercises: List<Exercise> = emptyList()
)


