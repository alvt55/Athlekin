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
    val sets: Int = 0,
)

// // for practice
//enum class WorkoutType { WEIGHTS, CARDIO, FLEXIBILITY, OTHER }
//interface Workoutt {
//    val date: Timestamp
//    val name: String
//    val workoutType: WorkoutType
//}
//
//data class WeightWorkout(
//    override val date : Timestamp = Timestamp.now(),
//    override val name: String = "",
//    override val workoutType : WorkoutType = WorkoutType.WEIGHTS,
//    val exercises: List<Exercise> = emptyList()
//) : Workoutt
//
//data class CardioWorkout(
//    override val date : Timestamp = Timestamp.now(),
//    override val name: String = "",
//    override val workoutType : WorkoutType = WorkoutType.CARDIO,
//    val time : Double = 0.0
//) : Workoutt
//
//
//class Workout(val name : String, val cat : String) {
//
//    var isCardio = false
//
//    constructor(name: String, cat: String, cardio: Boolean) : this(name, cat) {
//        when (cat) {
//            "cardio" -> isCardio = true
//        }
//    }
//}