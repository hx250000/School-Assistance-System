package com.example.campustask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShopFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        recyclerView = view.findViewById(R.id.recyclerShop)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val list = listOf(
            ShopItem("星巴克咖啡券", 200, R.drawable.sample_coffee),
            ShopItem("图书馆座位卡", 50, R.drawable.sample_book),
            ShopItem("奶茶券", 100, R.drawable.sample_drink),
            ShopItem("校园代跑券", 80, R.drawable.sample_run)
        )

        recyclerView.adapter = ShopAdapter(list)

        return view
    }
}