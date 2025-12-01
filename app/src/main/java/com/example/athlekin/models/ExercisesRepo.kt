package com.example.athlekin.models

class ExercisesRepo {

    private var exercises = mutableListOf<Exercise>(
        Exercise(),
        Exercise(name= "ex1")
    )

    suspend fun getExercises() : List<Exercise>{
        kotlinx.coroutines.delay(1000)
        return exercises

    }

    suspend fun addExercise(ex: Exercise) : List<Exercise> {
        kotlinx.coroutines.delay(1000)
        exercises.add(ex)
        return exercises
    }

}