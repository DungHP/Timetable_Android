package com.example.timetable_android

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable-category-table")
data class TimetableCategoryEntity(
    @PrimaryKey(autoGenerate= true)
    val id: Int = 0,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image : ByteArray? = null,
    val description: String
)