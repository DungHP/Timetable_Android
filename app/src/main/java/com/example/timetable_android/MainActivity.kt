package com.example.timetable_android

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timetable_android.databinding.ActivityCategoryAddBinding
import com.example.timetable_android.databinding.ActivityEditBinding
import com.example.timetable_android.databinding.ActivityMainBinding
import com.example.timetable_android.databinding.TimetableCategoryRowBinding
import com.google.android.material.R
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    val outputStream = ByteArrayOutputStream()
    val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //Get Result then work with this result
                result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    Uri.parse(result.data?.data.toString())
                )
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        val timetableCategoryDao = (application as TimetableApp).db2.timetableCategoryDao()
                super.onCreate(savedInstanceState)
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding?.root)
                binding?.btnCreate?.setOnClickListener{
                    addNewCategory(timetableCategoryDao)
                }
        lifecycleScope.launch{
            timetableCategoryDao.fetchAllTimetableCategory().collect{
                val list = ArrayList(it)
                setUpDataInRecyclerView(list)
            }
        }

            val start = intent.getStringExtra("startActivity").toString()
            if(start == "start"){
                intent = Intent(this, TimetableActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
    fun getPickIntent(): Intent {
        val pickIntent = Intent(Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        return pickIntent
    }
    fun addNewCategory(timetableCategoryDao : TimetableCategoryDao){
        val addCategoryDialog = Dialog(this, R.style.Theme_AppCompat_Dialog)
        addCategoryDialog.setCancelable(false)
        // The resource will be inflated, adding all top-level views to the screen
        val binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        addCategoryDialog.setContentView(binding.root)

        binding.btnImage.setOnClickListener{
            openGalleryLauncher.launch(getPickIntent())
        }
        binding.btnSubmit.setOnClickListener{
            addCategoryDialog.dismiss()
            val description = binding.etDescription.text.toString()
            if(description.isNotEmpty()){
                lifecycleScope.launch{
                    timetableCategoryDao.insert(TimetableCategoryEntity(image = outputStream.toByteArray(),
                        description = description))
                }
            }
            else {
                Toast.makeText(
                    applicationContext,
                    "Description cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        addCategoryDialog.show()
    }
    private fun setUpDataInRecyclerView(timetableCategoryList: ArrayList<TimetableCategoryEntity>){
        if (timetableCategoryList.isNotEmpty()) {
            val TimetableCategoryAdapter = TimetableCategoryAdapter(timetableCategoryList, )
            binding?.rvTimetableCategoryList?.layoutManager = LinearLayoutManager(this)
            binding?.rvTimetableCategoryList?.adapter = TimetableCategoryAdapter

        } else {
            //Implement Later!
        }
    }
}