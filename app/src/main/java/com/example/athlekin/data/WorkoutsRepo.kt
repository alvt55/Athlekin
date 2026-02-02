package com.example.athlekin.data

import com.example.athlekin.datasource.WorkoutsRemoteDataSource
import com.example.athlekin.model.WorkoutDoc
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface WorkoutPageRepo {
    fun getWorkouts(currentUserIdFlow: Flow<String?>): Flow<List<WorkoutDoc>>
    suspend fun createWorkout(workoutDoc: WorkoutDoc): String
    suspend fun getWorkout(id: String): WorkoutDoc?
    suspend fun deleteWorkout(id: String)
    suspend fun updateWorkout(workoutDoc: WorkoutDoc)
}

class WorkoutsRepo @Inject constructor(
    private val workoutsRemoteDataSource: WorkoutsRemoteDataSource
) : WorkoutPageRepo {

    override fun getWorkouts(currentUserIdFlow: Flow<String?>): Flow<List<WorkoutDoc>> {
        return workoutsRemoteDataSource.getWorkouts(currentUserIdFlow)
    }

    override suspend fun getWorkout(id: String): WorkoutDoc? {
        return workoutsRemoteDataSource.getWorkout(id)
    }



    // TODO
//    override fun getUserExercises(currentUserIdFlow: Flow<String?>): Flow<List<WorkoutDoc>> {
//
//    }

    override suspend fun updateWorkout(workoutDoc: WorkoutDoc) {
        workoutsRemoteDataSource.update(workoutDoc)
    }


    override suspend fun createWorkout(workoutDoc: WorkoutDoc): String {
        return workoutsRemoteDataSource.createWorkout(workoutDoc)
    }

    override suspend fun deleteWorkout(id: String) {
        workoutsRemoteDataSource.delete(id)
    }


}