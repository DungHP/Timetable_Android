package com.example.timetable_android

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimetableEntity::class], version = 1)
abstract class TimetableDatabase:RoomDatabase() {
    abstract fun timetableDao(): TimetableDao

    companion object{
        @Volatile
        private var INSTANCE : TimetableDatabase? = null

        fun getInstance(context: Context):TimetableDatabase{

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, TimetableDatabase::class.java,
                        "timetable_database"
                    ).fallbackToDestructiveMigration() //Wipe Migration and Rebuild
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}