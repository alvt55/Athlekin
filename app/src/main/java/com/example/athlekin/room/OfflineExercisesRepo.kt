package com.example.athlekin.room

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineExercisesRepo @Inject constructor(private val exerciseDao : ExerciseDao) {

    fun getAllExercisesStream(): Flow<List<ExerciseEntity>> = exerciseDao.getAllExercises()

    suspend fun createExercise(ex : ExerciseEntity) = exerciseDao.insert(ex)
    suspend fun deleteAllExercises() = exerciseDao.deleteAll()
    suspend fun deleteExerciseById(id: Int) = exerciseDao.deleteById(id)
}