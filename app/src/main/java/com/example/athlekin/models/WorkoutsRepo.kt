package com.example.athlekin.models


class WorkoutsRepo {

    private var workouts = mutableListOf<Workout>(
        Workout(),
        Workout(name= "ex1")
    )

    suspend fun getExercises() : List<Workout>{
        kotlinx.coroutines.delay(1000)
        return workouts

    }

    suspend fun addExercise(ex: Workout) : List<Workout> {
        kotlinx.coroutines.delay(1000)
        workouts.add(ex)
        return workouts
    }

}