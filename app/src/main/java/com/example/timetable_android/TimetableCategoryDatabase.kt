package com.example.timetable_android

import android.content.Context
import androidx.room.*

@Database(entities = [TimetableCategoryEntity::class], version = 1)
abstract class TimetableCategoryDatabase : RoomDatabase() {
    abstract fun timetableCategoryDao(): TimetableCategoryDao

    companion object{
        @Volatile
        private var INSTANCE : TimetableCategoryDatabase? = null

        fun getInstance(context: Context):TimetableCategoryDatabase{

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, TimetableCategoryDatabase::class.java,
                        "timetable_category_database"
                    ).fallbackToDestructiveMigration() //Wipe Migration and Rebuild
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}