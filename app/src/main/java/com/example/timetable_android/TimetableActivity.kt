package com.example.timetable_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.timetable_android.databinding.ActivityTimetableBinding

class TimetableActivity : AppCompatActivity() {
    private var binding: ActivityTimetableBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimetableBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnCreate?.setOnClickListener{
            intent = Intent(this, InsertActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}