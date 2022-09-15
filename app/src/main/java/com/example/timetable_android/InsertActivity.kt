package com.example.timetable_android

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.timetable_android.databinding.ActivityInsertBinding
import kotlinx.coroutines.launch
import java.util.*


class InsertActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private var binding: ActivityInsertBinding? = null
    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0
    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0
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
        pickDate()
    }
    fun addRecord(timetableDao : TimetableDao){
        val date = savedDay.toString()
        val year = savedYear.toString()
        val month = savedMonth.toString()
        val time = "$savedHour:$savedMinute"
        val description = binding?.etDescription?.text.toString()

        if (date != "0:0" && description.isNotEmpty()){
            lifecycleScope.launch{
                timetableDao.insert(TimetableEntity(year= year, month = month,
                    time=time, date=date, description= description))
                Toast.makeText(applicationContext,"Record Saved Successfully", Toast.LENGTH_LONG).show()
                binding?.etDescription?.text?.clear()
            }
            intent = Intent(this, TimetableActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            Toast.makeText(applicationContext, "Failed Added", Toast.LENGTH_LONG).show()
        }

    }
    fun getDateTimeCalender(){
        val calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
    }
    fun pickDate(){
        binding?.btnPicktime?.setOnClickListener{
            getDateTimeCalender()
            DatePickerDialog(this, this, year,month, day).show()
        }
    }
    //When Finished Selecting Date
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        savedDay = day
        savedMonth = month
        savedYear = year
        getDateTimeCalender()
        TimePickerDialog(this, this, hour,minute, true).show()
    }
    //When Finished Selecting Time
    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        savedHour = hour
        savedMinute = minute
        binding?.tvDateLabel?.text = "$savedDay/$savedMonth/$savedYear - $savedHour:$savedMinute"
    }
}