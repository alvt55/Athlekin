package com.example.athlekin.data

import com.example.athlekin.datasource.WorkoutsRemoteDataSource
import com.example.athlekin.model.Exercise
import com.example.athlekin.model.Workout
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface WorkoutPageRepo {
    fun getWorkouts(currentUserIdFlow: Flow<String?>): Flow<List<Workout>>
    suspend fun createWorkout(workout: Workout) : String
}

class WorkoutsRepo @Inject constructor(
    private val workoutsRemoteDataSource: WorkoutsRemoteDataSource
) : WorkoutPageRepo{

    override fun getWorkouts(currentUserIdFlow: Flow<String?>): Flow<List<Workout>> {
        return workoutsRemoteDataSource.getWorkouts(currentUserIdFlow)
    }

    override suspend fun createWorkout(workout: Workout) : String {
        return workoutsRemoteDataSource.createWorkout(workout)
    }

}