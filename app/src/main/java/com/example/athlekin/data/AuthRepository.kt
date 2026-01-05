package com.example.athlekin.data

import com.example.athlekin.datasource.AuthRemoteDataSource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {
    val currentUser: FirebaseUser? = authRemoteDataSource.currentUser
    val currentUserIdFlow: Flow<String?> = authRemoteDataSource.currentUserIdFlow


    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return authRemoteDataSource.signIn(email, password)
    }

    suspend fun createAccount(email: String, password: String): Result<FirebaseUser> {
        return authRemoteDataSource.createAccount(email, password)
    }

    fun signOut() : Result<FirebaseUser>{
        return authRemoteDataSource.signOut()
    }


}