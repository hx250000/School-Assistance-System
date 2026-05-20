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
import com.example.campustask.repository.PointRepository
import com.example.campustask.repository.ShopRepository

class ShopFragment : Fragment() {

    private  val TAG="ShopFragment"
    private lateinit var recyclerView: RecyclerView
    private lateinit var shopRepository: ShopRepository
    private lateinit var pointRepository: PointRepository
    private lateinit var tvMypoints: TextView
    private lateinit var tvExchangeCount: TextView

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
        tvExchangeCount = view.findViewById(R.id.exchanged)
        recyclerView = view.findViewById(R.id.recyclerShop)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        shopRepository = ShopRepository()
        pointRepository= PointRepository()

        // 从后端获取数据
        fetchShopItems()
        fetchMyPoints()
        fetchMyExchangeCount()

        return view
    }

    private fun fetchShopItems() {
        shopRepository.getShopItems { success, items, error ->
            if (success && items != null ) {
                Log.d(TAG,"items="+items)
                if(items.isEmpty()){
                    Toast.makeText(requireContext(), "商店里还没有商品哦", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = ShopAdapter(items, false,object : ShopAdapter.OnExchangeListener {
                    override fun onExchangeSuccess() {
                        fetchShopItems()
                        fetchMyPoints()
                        fetchMyExchangeCount()
                    }

                    override fun onExchangeFailure(message: String) {
                        Log.d(TAG,message)
                        Toast.makeText(requireContext(), "商品兑换失败，请再试一次", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // 网络请求失败，继续使用Mock数据
                Log.d(TAG,"items="+items+", error="+error)
                Toast.makeText(requireContext(), "网络连接失败，使用默认数据", Toast.LENGTH_SHORT).show()
                recyclerView.adapter = ShopAdapter(mockList, true,object : ShopAdapter.OnExchangeListener {
                    override fun onExchangeSuccess() {
                        fetchShopItems()
                        fetchMyPoints()
                        fetchMyExchangeCount()
                    }

                    override fun onExchangeFailure(message: String) {
                        Log.d(TAG,message)
                        Toast.makeText(requireContext(), "商品兑换失败，请再试一次", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
    private fun fetchMyPoints(){
        pointRepository.getMyCurrentPoints(requireContext()) { success, points, error ->
            if (success && points != null) {
                tvMypoints.text = "$points"
            } else {
                Toast.makeText(requireContext(), "无法获取积分信息", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchMyExchangeCount(){
        shopRepository.getMyExchangeCount(requireContext()) { success, counts, error ->
            if (success && counts != null) {
                tvExchangeCount.text = "${counts}次"
            } else {
                Toast.makeText(requireContext(), "无法获取兑换次数信息", Toast.LENGTH_SHORT).show()
            }
        }
    }

}