package com.example.campustask.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.model.Task

class PopupTaskAdapter(
    private val list: List<Task>,
    private val onClick: (Task) -> Unit
) : RecyclerView.Adapter<PopupTaskAdapter.VH>() {

    inner class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_popup_task, parent, false)
    ) {
        val name = itemView.findViewById<TextView>(R.id.task_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val task = list[position]
        holder.name.text = task.title
        holder.itemView.setOnClickListener {
            onClick(task)
        }
    }
}