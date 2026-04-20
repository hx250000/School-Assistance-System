package com.example.campustask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.model.Task
import java.text.SimpleDateFormat
import java.util.*

class MyTaskAdapter(
    private var list: List<Task>,
    private val onItemClick: (Task) -> Unit
) : RecyclerView.Adapter<MyTaskAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val people: TextView = view.findViewById(R.id.people)
        val score: TextView = view.findViewById(R.id.score)
        val time: TextView = view.findViewById(R.id.time)
        val status: TextView = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_task, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val task = list[position]

        holder.title.text = task.title

        holder.people.text = "${task.currentPeople}/${task.needPeople}人"

        holder.score.text = "+${task.rewardPoints}积分"

        holder.time.text = formatTime(task.deadline)

        holder.status.text = when (task.status) {
            "OPEN" -> "已发布"
            "IN_PROGRESS" -> "进行中"
            "FINISHED" -> "已完成"
            else -> ""
        }

        holder.itemView.setOnClickListener {
            onItemClick(task)
        }
    }

    fun update(newList: List<Task>) {
        list = newList
        notifyDataSetChanged()
    }

    private fun formatTime(time: Long): String {
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(time))
    }
}