package com.example.timetable_android

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {

    @Insert
    suspend fun insert(timetableEntity : TimetableEntity)

    @Update
    suspend fun update(timetableEntity : TimetableEntity)

    @Delete
    suspend fun delete(timetableEntity: TimetableEntity)

    @Query("SELECT * FROM `timetable-table`")
    fun fetchAllTimetable(): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM `timetable-table` WHERE id=:id")
    fun fetchTimetableById(id:Int): Flow<List<TimetableEntity>>
}