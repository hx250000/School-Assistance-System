package com.example.campustask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.model.Task

class TaskAdapter(
    private var list: List<Task>,
    private val onItemClick: (Task) -> Unit   // 👉 回调
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val people: TextView = view.findViewById(R.id.people)
        val score: TextView = view.findViewById(R.id.score)
        val time: TextView = view.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = list[position]

        holder.title.text = task.title
        holder.people.text = task.people
        holder.score.text = task.score
        holder.time.text = task.time

        // 👉 点击事件
        holder.itemView.setOnClickListener {
            onItemClick(task)
        }
    }

    fun update(newList: List<Task>) {
        list = newList
        notifyDataSetChanged()
    }
}