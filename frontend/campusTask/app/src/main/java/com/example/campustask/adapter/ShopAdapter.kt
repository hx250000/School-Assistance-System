package com.example.campustask.adapter

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

class ShopAdapter(private val list: List<ShopItem>) :
    RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgProduct)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val points = view.findViewById<TextView>(R.id.tvPoints)
        val btn = view.findViewById<Button>(R.id.btnExchange)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.points.text = "${item.points}积分"
        holder.img.setImageResource(item.imageRes)

        holder.btn.setOnClickListener {
            Toast.makeText(holder.itemView.context, "兑换成功！", Toast.LENGTH_SHORT).show()
        }
    }
}