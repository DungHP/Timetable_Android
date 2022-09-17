package com.example.timetable_android

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.timetable_android.databinding.ActivityMainBinding
import com.example.timetable_android.databinding.TimetableCategoryRowBinding
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val timetableCategoryDao = (application as TimetableApp).db2.timetableCategoryDao()
        val openGalleryLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                //Get Result then work with this result
                    result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        Uri.parse(result.data?.data.toString())
                    )
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
                    lifecycleScope.launch {
                        timetableCategoryDao.insert(
                            TimetableCategoryEntity(
                                image = outputStream.toByteArray(),
                                description = "HELLO"
                            ))
                    }
                }
            }
                super.onCreate(savedInstanceState)
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding?.root)
//                openGalleryLauncher.launch(getPickIntent())
    }
    fun getPickIntent(): Intent {
        val pickIntent = Intent(Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        return pickIntent
    }
}