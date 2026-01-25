package com.example.athlekin.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey



// room entity model
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val reps: Int = 1,
    val sets: Int = 1,
    @ColumnInfo(defaultValue = "0") // <- default value for existing rows
    val weight: Int = 1,
    @ColumnInfo(defaultValue = "")
    val comments: String = ""
)


//// V1 that was not tracked in schemas
//@Entity(tableName = "exercises")
//data class ExerciseEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
//    val name: String = "",
//    val reps: Int = 1,
//    val sets: Int = 1,
//)