package com.example.timetable_android

import android.content.Intent
import android.media.TimedMetaData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timetable_android.databinding.ActivityTimetableBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TimetableActivity : AppCompatActivity() {
    private var binding: ActivityTimetableBinding? = null

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

        lifecycleScope.launch{
            timetableDao.fetchAllTimetable().collect{
                val list = ArrayList(it)
                setUpDataInRecyclerView(list,timetableDao)
            }
        }
    }
    private fun setUpDataInRecyclerView(timetableList: ArrayList<TimetableEntity>, timetableDao: TimetableDao){
        if(timetableList.isNotEmpty()){
            val timetableAdapter = TimetableAdapter(timetableList)
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
}