package com.example.athlekin.data

import com.example.athlekin.datasource.WorkoutsRemoteDataSource
import com.example.athlekin.model.Exercise
import com.example.athlekin.model.WorkoutDoc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface WorkoutPageRepo {
    fun getWorkouts(currentUserIdFlow: Flow<String?>): Flow<List<WorkoutDoc>>
    suspend fun createWorkout(workoutDoc: WorkoutDoc): String
    suspend fun getWorkout(id: String): WorkoutDoc?
    suspend fun deleteWorkout(id: String)
    suspend fun updateWorkout(workoutDoc: WorkoutDoc)

    fun getExercisesByName(currentUserIdFlow: Flow<String?>): Flow<Map<String, List<Exercise>>>


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

    override fun getExercisesByName(
        currentUserIdFlow: Flow<String?>
    ): Flow<Map<String, List<Exercise>>> {
        return workoutsRemoteDataSource.getWorkouts(currentUserIdFlow)
            .map { workouts ->
                workouts
                    .flatMap { it.exercises }
                    .groupBy { it.name }
            }
    }


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