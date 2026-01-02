package com.example.athlekin.injection

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseHiltModule {

    @Provides fun auth(): FirebaseAuth = Firebase.auth

    @Provides fun firestore(): FirebaseFirestore = Firebase.firestore


    // for emulation
//    @Provides fun auth(): FirebaseAuth {
//
//        Firebase.auth.useEmulator("10.0.2.2", 9099)
//        return Firebase.auth
//    }
//
//    @Provides fun firestore(): FirebaseFirestore {
//        Firebase.firestore.useEmulator("10.0.2.2", 8080)
//        return Firebase.firestore
//    }


}