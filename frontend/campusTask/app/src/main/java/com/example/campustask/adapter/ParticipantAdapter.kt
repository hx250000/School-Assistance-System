package com.example.campustask.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campustask.R
import com.example.campustask.model.UserInfo
import com.example.campustask.utils.FileUrlResolver

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
        holder.points.text = "联系方式：${user.phone}"
        holder.credit.text = "${user.creditScore}"

        val imageSource = FileUrlResolver.resolve(user.avatarUrl, defaultType = "avatar")
        Glide.with(holder.avatar.context)
            .load(imageSource)
            .placeholder(R.drawable.ic_avatar)
            .error(R.drawable.ic_avatar)
            .circleCrop()
            .into(holder.avatar)
    }

    override fun getItemCount(): Int = list.size

    // ===== 更新数据 =====
    fun updateData(newList: List<UserInfo>) {
        this.list = newList
        notifyDataSetChanged()
    }
}