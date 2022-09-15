package com.example.timetable_android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timetable_android.databinding.TimetableRowBinding

class TimetableAdapter(private var items: ArrayList<TimetableEntity>,
                       private val editListener: (id: Int) -> Unit,
                       private val deleteListener: (id: Int) -> Unit):RecyclerView.Adapter<TimetableAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(TimetableRowBinding.inflate(
           LayoutInflater.from(parent.context),parent,false
       ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]
        holder.date.text = "${item.hour}:${item.minute}"
        holder.description.text = item.description

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
    class ViewHolder(binding: TimetableRowBinding): RecyclerView.ViewHolder(binding.root){
        val llTimetableRow = binding.llTimetableRow
        val date = binding.tvDate
        val description = binding.tvDescription
        val edit = binding.ivEdit
        val delete= binding.ivDelete
    }

}