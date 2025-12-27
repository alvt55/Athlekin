package com.example.athlekin.data



interface WorkoutPageRepo {
    suspend fun getWorkouts(): List<Workout>
}

class WorkoutsRepo : WorkoutPageRepo{

    // test data, will use external source later

    val w1 = Workout("work1",
        listOf(Exercise("Test1", 1, 2),
                            Exercise("Test2", 3, 4)))

    val w2 = Workout("work2",
        listOf(Exercise("Test3", 10, 2),
            Exercise("Test4", 100, 4)))

    val workouts = listOf(w1, w2)

    override suspend fun getWorkouts(): List<Workout> {
        return workouts
    }
}