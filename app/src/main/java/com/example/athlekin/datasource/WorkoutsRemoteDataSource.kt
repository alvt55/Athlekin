package com.example.athlekin.datasource

import com.example.athlekin.model.WorkoutDoc
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WorkoutsRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWorkouts(currentUserIdFlow: Flow<String?>): Flow<List<WorkoutDoc>> {
        return currentUserIdFlow.flatMapLatest { ownerId ->
            firestore
                .collection(WORKOUTS_COLLECTION)
                .whereEqualTo(OWNER_ID, ownerId)
                .orderBy("createdAt", )
                .dataObjects()
        }
    }

    suspend fun createWorkout(workoutDoc: WorkoutDoc): String {
        return firestore.collection(WORKOUTS_COLLECTION).add(workoutDoc).await().id
    }

    suspend fun delete(id: String) {
        firestore.collection(WORKOUTS_COLLECTION).document(id).delete().await()
    }


    companion object {
        private const val OWNER_ID = "ownerId"
        private const val WORKOUTS_COLLECTION = "workouts"
    }


}
