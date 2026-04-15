package com.example.campustask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.model.PointRecord

class PointsAdapter(private var list: List<PointRecord>) :
    RecyclerView.Adapter<PointsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvDesc: TextView = view.findViewById(R.id.tv_desc)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
        val tvScore: TextView = view.findViewById(R.id.tv_score)
        val tvIcon: TextView = view.findViewById(R.id.tv_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_points, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvTitle.text = item.title
        holder.tvDesc.text = item.desc
        holder.tvTime.text = item.time

        holder.tvScore.text =
            if (item.score > 0) "+${item.score}" else "${item.score}"

        if (item.score > 0) {
            holder.tvScore.setTextColor(holder.itemView.resources.getColor(android.R.color.holo_green_dark))
            holder.tvIcon.text = "↑"
            holder.tvIcon.setBackgroundResource(R.drawable.bg_circle_green)
        } else {
            holder.tvScore.setTextColor(holder.itemView.resources.getColor(android.R.color.holo_red_dark))
            holder.tvIcon.text = "↓"
            holder.tvIcon.setBackgroundResource(R.drawable.bg_circle_orange)
        }
    }

    override fun getItemCount() = list.size

    // 🔥 更新数据（后端用这个！）
    fun updateData(newList: List<PointRecord>) {
        list = newList
        notifyDataSetChanged()
    }
}