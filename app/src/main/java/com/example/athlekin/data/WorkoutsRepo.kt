package com.example.athlekin.data

import com.example.athlekin.datasource.WorkoutsRemoteDataSource
import com.example.athlekin.model.WorkoutDoc
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface WorkoutPageRepo {
    fun getWorkouts(currentUserIdFlow: Flow<String?>): Flow<List<WorkoutDoc>>
    suspend fun createWorkout(workoutDoc: WorkoutDoc) : String

    suspend fun deleteWorkout(id: String)
}

class WorkoutsRepo @Inject constructor(
    private val workoutsRemoteDataSource: WorkoutsRemoteDataSource
) : WorkoutPageRepo{

    override fun getWorkouts(currentUserIdFlow: Flow<String?>): Flow<List<WorkoutDoc>> {
        return workoutsRemoteDataSource.getWorkouts(currentUserIdFlow)
    }

    override suspend fun createWorkout(workoutDoc: WorkoutDoc) : String {
        return workoutsRemoteDataSource.createWorkout(workoutDoc)
    }

    override suspend fun deleteWorkout(id: String) {
         workoutsRemoteDataSource.delete(id)
    }



}