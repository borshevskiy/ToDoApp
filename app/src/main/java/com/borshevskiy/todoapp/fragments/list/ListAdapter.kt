package com.borshevskiy.todoapp.fragments.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.borshevskiy.todoapp.R
import com.borshevskiy.todoapp.data.models.Priority
import com.borshevskiy.todoapp.data.models.ToDoData
import com.borshevskiy.todoapp.databinding.RowLayoutBinding

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var dataList = emptyList<ToDoData>()

    class MyViewHolder(val binding: RowLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(RowLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentData = dataList[position]
        val context = holder.itemView.context
        with(holder.binding) {
            titleTxt.text = currentData.title
            descriptionTxt.text = currentData.description
            when(currentData.priority) {
                Priority.HIGH -> priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(
                    context, R.color.red))
                Priority.MEDIUM -> priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(
                    context, R.color.yellow))
                Priority.LOW -> priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(
                    context, R.color.green))
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(toDoData: List<ToDoData>) {
        dataList = toDoData
        notifyDataSetChanged()
    }

}