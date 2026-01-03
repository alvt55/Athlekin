package com.example.athlekin.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Workout(
    @DocumentId val id: String = "",
    val ownerId : String? = "",
    val createdAt: Timestamp = Timestamp.now(),
    val name: String = "",
    val exercises: List<Exercise> = emptyList()
)


data class Exercise(
    val roomId: Int = 0,
    val name: String = "",
    val reps: Int = 1,
    val sets: Int = 1,
)

data class TimeSlot(
    val startTime: Long = 0,
    val endTime: Long = 0,
    val available : Boolean = false
)

