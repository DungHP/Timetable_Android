package com.example.timetable_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.timetable_android.databinding.ActivityTimetableBinding

class TimetableActivity : AppCompatActivity() {
    private var binding: ActivityTimetableBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimetableBinding.inflate(layoutInflater)
        setContentView(binding?.root)

    }
}