package com.example.campustask.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.model.ShopItem
import com.example.campustask.repository.ShopRepository

class ShopAdapter(
    private val list: List<ShopItem>,
    private val isMock: Boolean=false,
    private val onExchangeListener: OnExchangeListener? = null
) :
    RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    interface OnExchangeListener {
        fun onExchangeSuccess()
        fun onExchangeFailure(message: String)
    }

    private lateinit var shopRepository: ShopRepository
    private val TAG="ShopAdapter"

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgProduct)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val points = view.findViewById<TextView>(R.id.tvPoints)
        val stock = view.findViewById<TextView>(R.id.tvStock)
        val btn = view.findViewById<Button>(R.id.btnExchange)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop, parent, false)
        shopRepository=ShopRepository()
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.name
        holder.points.text = "${item.price}积分"
        holder.stock.text = "库存：${item.stock}件"
        
        // 根据图片名称动态获取资源ID
        val context = holder.itemView.context
        val resourceId = context.resources.getIdentifier(
            item.imageRes,
            "drawable",
            context.packageName
        )
        if (resourceId != 0) {
            holder.img.setImageResource(resourceId)
        } else {
            // 如果找不到对应的图片资源，使用默认图片
            holder.img.setImageResource(R.drawable.ic_avatar)
        }

        // 如果是 Mock 数据，直接禁用按钮并调低透明度
        if (isMock) {
            holder.btn.isEnabled = false
            holder.btn.alpha = 0.5f // 置灰视觉效果
            holder.btn.text = "暂不可兑换" // 让提示更清晰
            holder.btn.setOnClickListener(null) // 移除点击事件
        } else {
            // 恢复正常状态，防止 RecyclerView 复用 ViewHolder 导致按钮状态混乱
            holder.btn.isEnabled = true
            holder.btn.alpha = 1.0f
            holder.btn.text = "兑换"

            holder.btn.setOnClickListener {
                holder.btn.isEnabled = false
                holder.btn.alpha = 0.5f
                Log.d(TAG, "btn enabled = ${holder.btn.isEnabled}")
                shopRepository.exchangeItem(context, item.id) { success, _, error ->
                    holder.btn.post {
                        holder.btn.isEnabled = true
                        holder.btn.alpha = 1f
                        Log.d(TAG, "btn enabled = ${holder.btn.isEnabled}")
                        if (success) {
                            Toast.makeText(holder.itemView.context, "兑换成功！", Toast.LENGTH_SHORT).show()
                            onExchangeListener?.onExchangeSuccess()
                        } else {
                            Toast.makeText(holder.itemView.context, error ?: "兑换失败", Toast.LENGTH_SHORT).show()
                            onExchangeListener?.onExchangeFailure(error ?: "兑换失败")
                        }
                    }
                }
            }
        }
    }
}