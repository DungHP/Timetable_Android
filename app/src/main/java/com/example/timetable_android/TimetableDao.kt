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

    @Query("SELECT * FROM `timetable-table` WHERE id=:id AND categoryId=:categoryId")
    fun fetchTimetableById(id:Int, categoryId : Int): Flow<TimetableEntity>

    @Query("SELECT * FROM `timetable-table` WHERE year=:year AND month=:month AND day=:day AND categoryId=:categoryId")
    fun fetchAllTimetableOfCorrectDay(year: Int, month: Int, day: Int, categoryId: Int): Flow<List<TimetableEntity>>
}