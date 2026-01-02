package com.example.athlekin.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ExerciseEntity::class], version = 1, exportSchema = false)
abstract class ExerciseDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao


//
//    companion object {
//        @Volatile
//        private var Instance: ExerciseDatabase? = null
//
//        fun getDatabase(context: Context): ExerciseDatabase {
//            // if the Instance is not null, return it, otherwise create a new database instance.
//            return Instance ?: synchronized(this) {
//                Room.databaseBuilder(context, ExerciseDatabase::class.java, "exercise_database")
//                    /**
//                     * Setting this option in your app's database builder means that Room
//                     * permanently deletes all data from the tables in your database when it
//                     * attempts to perform a migration with no defined migration path.
//                     */
//                    .fallbackToDestructiveMigration(false)
//                    .build()
//                    .also { Instance = it }
//            }
//        }
//    }
}
