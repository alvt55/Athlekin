package com.example.athlekin.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [ExerciseEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true)
abstract class ExerciseDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao

}
