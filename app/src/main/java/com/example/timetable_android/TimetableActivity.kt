package com.example.timetable_android

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.media.TimedMetaData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timetable_android.databinding.ActivityEditBinding
import com.example.timetable_android.databinding.ActivityTimetableBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class TimetableActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private var binding: ActivityTimetableBinding? = null
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
        binding = ActivityTimetableBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val timetableDao = (application as TimetableApp).db.timetableDao()
        binding?.btnCreate?.setOnClickListener{
            intent = Intent(this, InsertActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding?.btnGoBack?.setOnClickListener{
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        lifecycleScope.launch{
            timetableDao.fetchAllTimetable().collect{
                val list = ArrayList(it)
                setUpDataInRecyclerView(list,timetableDao)
            }
        }

    }
    private fun setUpDataInRecyclerView(timetableList: ArrayList<TimetableEntity>, timetableDao: TimetableDao){
        if(timetableList.isNotEmpty()){
            val timetableAdapter = TimetableAdapter(timetableList, {editId -> editRecordDialog(editId, timetableDao)},
                {deleteId -> lifecycleScope.launch{
                    timetableDao.fetchTimetableById(deleteId).collect{
                        if(it != null){
                            deleteAlertDialog(deleteId, timetableDao,it)
                        }
                    }
                }})
            binding?.rvTimetableList?.layoutManager=LinearLayoutManager(this)
            binding?.rvTimetableList?.adapter = timetableAdapter
            binding?.rvTimetableList?.visibility = View.VISIBLE
            binding?.tvNoRecords?.visibility = View.GONE
        }
        else{
            binding?.rvTimetableList?.visibility = View.GONE
            binding?.tvNoRecords?.visibility = View.VISIBLE
        }

    }
    fun editRecordDialog(id:Int, timetableDao: TimetableDao)  {
        val editDialog = Dialog(this, com.google.android.material.R.style.Theme_AppCompat_Dialog)
        editDialog.setCancelable(false)
        // The resource will be inflated, adding all top-level views to the screen
        val binding = ActivityEditBinding.inflate(layoutInflater)
        editDialog.setContentView(binding.root)

        //Set the Text of the Desc in the Edit Dialog
        lifecycleScope.launch {
            timetableDao.fetchTimetableById(id).collect {
                binding.tvCurrentTime.setText("${it.hour}:${it.minute}")
                binding.etUpdateDescription.setText(it.description)
                binding.tvCurrentDate.setText("${it.year}/${it.month}/${it.day}")
            }
        }
        binding.btnSelectTime.setOnClickListener{
            pickDate()
        }
        binding.tvUpdate.setOnClickListener {
            val description = binding.etUpdateDescription.text.toString()
            val day = savedDay.toString()
            val year = savedYear.toString()
            val month = savedMonth.toString()
            val hour = savedHour.toString()
            val minute = savedMinute.toString()
            if (year != "0" && description.isNotEmpty()) {
                lifecycleScope.launch {
                    timetableDao.update(TimetableEntity(id, year = year, month= month, day= day,
                        hour= hour, minute = minute, description))
                    Toast.makeText(applicationContext, "Updated.", Toast.LENGTH_LONG)
                        .show()
                    editDialog.dismiss() // Dialog will be dismissed
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Date or Description cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.tvCancel.setOnClickListener{
            editDialog.dismiss()
        }
        //Start the dialog and display it on screen.
        editDialog.show()
    }
    fun deleteAlertDialog(id:Int,timetableDao: TimetableDao,timetable:TimetableEntity) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${timetable.description}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                timetableDao.delete(TimetableEntity(id))
                Toast.makeText(
                    applicationContext,
                    "Deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()

                dialogInterface.dismiss() // Dialog will be dismissed
            }

        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
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
        getDateTimeCalender()
        DatePickerDialog(this, this, year,month, day).show()
    }
    //When Finished Selecting Date
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        savedDay = day
        savedMonth = month
        savedYear = year
        getDateTimeCalender()
        TimePickerDialog(this, this, hour,minute, false).show()
    }
    //When Finished Selecting Time
    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        savedHour = hour
        savedMinute = minute
    }

}