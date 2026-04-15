package com.example.campustask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.model.Achievement

class AchievementAdapter(private val list: List<Achievement>) :
    RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imgIcon)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val desc: TextView = view.findViewById(R.id.tvDesc)
        val progress: ProgressBar = view.findViewById(R.id.itemProgress)
        val count: TextView = view.findViewById(R.id.tvCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.desc.text = item.desc
        holder.count.text = "${item.current}/${item.total}"
        holder.progress.max = item.total
        holder.progress.progress = item.current
        holder.icon.setImageResource(item.iconRes)
    }

    override fun getItemCount() = list.size
}