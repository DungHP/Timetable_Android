package com.example.timetable_android

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableCategoryDao {
    @Insert
    suspend fun insert(timetableCategoryEntity : TimetableCategoryEntity)

    @Update
    suspend fun update(timetableCategoryEntity : TimetableCategoryEntity)

    @Delete
    suspend fun delete(timetableCategoryEntity: TimetableCategoryEntity)

    @Query("SELECT * FROM `timetable-category-table`")
    fun fetchAllTimetableCategory(): Flow<List<TimetableCategoryEntity>>

    @Query("SELECT * FROM `timetable-category-table` WHERE id=:id")
    fun fetchTimetableById(id:Int): Flow<TimetableCategoryEntity>
}