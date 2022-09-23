package com.example.timetable_android

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.scale
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timetable_android.databinding.*
import com.google.android.material.R
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var bitmapImage : Bitmap ? = null
    var outputStream = ByteArrayOutputStream()
                    val openGalleryLauncher: ActivityResultLauncher<Intent> =
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        //Get Result then work with this result
                            result ->
                        if (result.resultCode == RESULT_OK && result.data != null) {
                            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                                contentResolver,
                                Uri.parse(result.data?.data.toString())
                            )
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
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
                setUpDataInRecyclerView(list, timetableCategoryDao)
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
                    timetableCategoryDao.insert(TimetableCategoryEntity(image = outputStream?.toByteArray(),
                        description = description))
                }
                //Refresh
                finish()
                overridePendingTransition(0, 0)
                startActivity(intent)
                overridePendingTransition(0, 0)
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
    private fun setUpDataInRecyclerView(timetableCategoryList: ArrayList<TimetableCategoryEntity>, timetableCategoryDao: TimetableCategoryDao){
        if (timetableCategoryList.isNotEmpty()) {
            val TimetableCategoryAdapter = TimetableCategoryAdapter(timetableCategoryList, {editId -> editRecordDialog(editId, timetableCategoryDao)},
                {deleteId -> lifecycleScope.launch{
                    timetableCategoryDao.fetchTimetableById(deleteId).collect{
                        if(it != null){
                            deleteAlertDialog(deleteId, timetableCategoryDao,it)
                        }
                    }
                }})
            binding?.rvTimetableCategoryList?.layoutManager = LinearLayoutManager(this)
            binding?.rvTimetableCategoryList?.adapter = TimetableCategoryAdapter
            binding?.tvNoCategory?.visibility = View.GONE

        } else {
            binding?.tvNoCategory?.visibility = View.VISIBLE
        }
    }
    fun deleteAlertDialog(id:Int,timetableCategoryDao: TimetableCategoryDao,timetableCategory:TimetableCategoryEntity) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${timetableCategory.description}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                timetableCategoryDao.delete(TimetableCategoryEntity(id))
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

    fun editRecordDialog(id:Int, timetableCategoryDao: TimetableCategoryDao)  {
        val editDialog = Dialog(this, R.style.Theme_AppCompat_Dialog)
        editDialog.setCancelable(false)
        // The resource will be inflated, adding all top-level views to the screen
        val binding = ActivityCategoryEditBinding.inflate(layoutInflater)
        editDialog.setContentView(binding.root)

        //Set the Image and Desc in the Edit Dialog
        lifecycleScope.launch {
            timetableCategoryDao.fetchTimetableById(id).collect {
                bitmapImage = BitmapFactory.decodeByteArray(it.image, 0 , it.image!!.size)
                binding.ivCategoryImage.setImageBitmap(bitmapImage)
                binding.etUpdateDescription.setText(it.description)
            }
        }
        binding.btnSelectImage.setOnClickListener {
            openGalleryLauncher.launch(getPickIntent())
            bitmapImage = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0 ,
                outputStream.toByteArray().size)
            binding.ivCategoryImage.setImageBitmap(bitmapImage)
        }
        binding.tvUpdate.setOnClickListener {
            val description = binding.etUpdateDescription.text.toString()
                    if (description.isNotEmpty() && outputStream.toByteArray().isNotEmpty()) {
                        lifecycleScope.launch {
                                timetableCategoryDao.update(
                                    TimetableCategoryEntity(
                                        id, image = outputStream.toByteArray(),
                                        description = description
                                    )
                                )
                                Toast.makeText(applicationContext, "Updated.", Toast.LENGTH_LONG)
                                    .show()
                                editDialog.dismiss() // Dialog will be dismissed
                                //Refresh
                                finish()
                                overridePendingTransition(0, 0)
                                startActivity(intent)
                                overridePendingTransition(0, 0)
                            }
                    }
                    else {
                        Toast.makeText(
                            applicationContext,
                            "Description and Image cannot be blank",
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
}