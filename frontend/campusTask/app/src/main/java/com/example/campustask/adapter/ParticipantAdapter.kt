package com.example.campustask.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.model.UserInfo

class ParticipantAdapter(
    private var list: List<UserInfo>
) : RecyclerView.Adapter<ParticipantAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.img_avatar)
        val username: TextView = view.findViewById(R.id.tv_username)
        val level: TextView = view.findViewById(R.id.tv_level)
        val points: TextView = view.findViewById(R.id.tv_points)
        val credit: TextView = view.findViewById(R.id.tv_credit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participant, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = list[position]

        holder.username.text = user.username
        holder.level.text = "Lv.${user.level}"
        holder.points.text = "积分：${user.points}"
        holder.credit.text = "${user.creditScore}"

        // 简单头像处理（没有Glide也能用）
        if (user.avatarUrl.isNotBlank()) {
            // 如果你后面接 Glide，可以替换这里
            // Glide.with(holder.avatar).load(user.avatarUrl).into(holder.avatar)
            holder.avatar.setImageResource(R.mipmap.ic_launcher_round)
        } else {
            holder.avatar.setImageResource(R.mipmap.ic_launcher_round)
        }
    }

    override fun getItemCount(): Int = list.size

    // ===== 更新数据 =====
    fun updateData(newList: List<UserInfo>) {
        this.list = newList
        notifyDataSetChanged()
    }
}