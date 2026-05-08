package com.example.campustask.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.model.ShopItem
import com.example.campustask.adapter.ShopAdapter
import com.example.campustask.repository.ShopRepository

class ShopFragment : Fragment() {

    private  val TAG="ShopFragment"
    private lateinit var recyclerView: RecyclerView
    private lateinit var shopRepository: ShopRepository
    private lateinit var tvMypoints: TextView
    private lateinit var tvExchangedCount: TextView

    // Mock数据，保留作为备用
    private val mockList = listOf(
        ShopItem(1,"星巴克咖啡券", 200, 100,"星巴克咖啡券","sample_coffee"),
        ShopItem(2,"图书馆座位卡", 50, 100,"图书馆座位卡","sample_book"),
        ShopItem(3,"奶茶券", 100,100,"奶茶券", "sample_drink"),
        ShopItem(4,"校园代跑券", 80, 100,"校园跑","sample_run")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        tvMypoints = view.findViewById(R.id.mypoints)
        tvExchangedCount = view.findViewById(R.id.exchanged)
        recyclerView = view.findViewById(R.id.recyclerShop)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        shopRepository = ShopRepository()

        // 初始显示Mock数据

        // 从后端获取商品数据
        fetchShopItems()

        return view
    }

    private fun fetchShopItems() {
        shopRepository.getShopItems { success, items, error ->
            if (success && items != null && items.isNotEmpty()) {
                recyclerView.adapter = ShopAdapter(items)
            } else {
                // 网络请求失败，继续使用Mock数据
                Toast.makeText(requireContext(), "网络连接失败，使用默认数据", Toast.LENGTH_SHORT).show()
                recyclerView.adapter = ShopAdapter(mockList)
            }
        }
    }
}