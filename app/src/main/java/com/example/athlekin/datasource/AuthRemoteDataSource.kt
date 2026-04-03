package com.example.athlekin.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(private val auth: FirebaseAuth) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    val currentUserIdFlow: Flow<String?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { _ -> this.trySend(currentUser?.uid) }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {

        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(auth.currentUser!!)  // TODO: should this return the flow instead?
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun createAccount(email: String, password: String): Result<FirebaseUser> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(auth.currentUser!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() : Result<FirebaseUser>{

        return try {
            auth.signOut()
            Result.success(auth.currentUser!!)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

}