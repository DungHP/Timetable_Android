package com.example.timetable_android

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "timetable-table")
data class TimetableEntity(
    @PrimaryKey(autoGenerate= true)
    val id: Int = 0,
    val year : String = "",
    val month : String = "",
    val day: String = "",
    val hour: String = "",
    val minute: String = "",
    val description: String = "",
    val categoryId : Int = 0
)
