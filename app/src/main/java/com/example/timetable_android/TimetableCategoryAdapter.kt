package com.example.timetable_android

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timetable_android.databinding.TimetableCategoryRowBinding
import java.sql.Time

class TimetableCategoryAdapter(private var items: ArrayList<TimetableCategoryEntity>,
                               private val editListener: (id: Int) -> Unit,
                               private val deleteListener: (id: Int) -> Unit)
    :RecyclerView.Adapter<TimetableCategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TimetableCategoryRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }
    //Bind Item of the ViewHolder in a specific position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.category.text = item.description
        val byteArray = item.image
        val ImageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        holder.image.setImageBitmap(ImageBitmap)

        holder.llTimetableCategoryRow.setOnClickListener {
            val context = holder.llTimetableCategoryRow.context
            val intent = Intent(context, TimetableActivity::class.java)
            intent.putExtra(Constants.CATEGORY_ID, item.id)
            context.startActivity(intent)
        }
        holder.edit.setOnClickListener {
            editListener.invoke(item.id)
        }
        holder.delete.setOnClickListener{
            deleteListener.invoke(item.id)
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(binding: TimetableCategoryRowBinding): RecyclerView.ViewHolder(binding.root){
        val llTimetableCategoryRow = binding.llTimetableCategoryRow
        val category = binding.tvCategory
        val image = binding.ivCategoryImage
        val edit = binding.ivEdit
        val delete = binding.ivDelete
    }

}