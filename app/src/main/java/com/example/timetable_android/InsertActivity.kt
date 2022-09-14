package com.example.timetable_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.timetable_android.databinding.ActivityInsertBinding
import kotlinx.coroutines.launch


class InsertActivity : AppCompatActivity() {
    private var binding: ActivityInsertBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val timetableDao = (application as TimetableApp).db.timetableDao()
        binding?.btnAdd?.setOnClickListener{
            addRecord(timetableDao)
        }
        binding?.btnGoBack?.setOnClickListener{
            intent = Intent(this, TimetableActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun addRecord(timetableDao : TimetableDao){
        val date = binding?.etDate?.text.toString()
        val description = binding?.etDescription?.text.toString()

        if (date.isNotEmpty() && description.isNotEmpty()){
            lifecycleScope.launch{
                timetableDao.insert(TimetableEntity(date=date, description= description))
                Toast.makeText(applicationContext,"Record Saved Successfully", Toast.LENGTH_LONG)
                binding?.etDate?.text?.clear()
                binding?.etDescription?.text?.clear()

            }
        }
        intent = Intent(this, TimetableActivity::class.java)
        startActivity(intent)
        finish()
    }
}