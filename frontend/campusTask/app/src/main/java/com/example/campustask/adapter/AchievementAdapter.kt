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
        holder.desc.text = item.description
        holder.count.text = "${item.currentProgress}/${item.totalProgress}"
        holder.progress.max = item.totalProgress
        holder.progress.progress = item.currentProgress
//        holder.icon.setImageResource(item.iconUrl)

        // 根据图片名称动态获取资源ID
        val context = holder.itemView.context
        val resourceId = context.resources.getIdentifier(
            item.iconUrl,
            "drawable",
            context.packageName
        )
        if (resourceId != 0) {
            holder.icon.setImageResource(resourceId)
        } else {
            // 如果找不到对应的图片资源，使用默认图片
            holder.icon.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    override fun getItemCount() = list.size
}