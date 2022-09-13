package com.example.timetable_android

import android.app.Application

class TimetableApp:Application() {
    val db by lazy{
        TimetableDatabase.getInstance(this)
    }
}