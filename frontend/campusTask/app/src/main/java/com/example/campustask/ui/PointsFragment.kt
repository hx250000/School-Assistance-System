package com.example.campustask.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.adapter.PointsAdapter
import com.example.campustask.model.PointRecord
import com.example.campustask.repository.PointRepository

class PointsFragment : Fragment(R.layout.fragment_points) {

    private lateinit var adapter: PointsAdapter

    private var pointList = mutableListOf<PointRecord>()

    val pointRepo= PointRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //adapter = PointsAdapter(getMockData())
        adapter = PointsAdapter(pointList)
        recyclerView.adapter = adapter

        fetchRealData()

        // 返回
        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    // 模拟数据（后面换后端）
    private fun getMockData(): List<PointRecord> {
        return listOf(
            PointRecord("完成任务", "代买奶茶", 12, "2026-03-16 15:30"),
            PointRecord("兑换商品", "星巴克优惠券", -200, "2026-03-15 14:30"),
            PointRecord("完成任务", "帮忙取快递", 10, "2026-03-14 18:45"),
            PointRecord("兑换商品", "图书馆预约卡", -50, "2026-03-14 09:20")
        )
    }

    private fun fetchRealData() {
        // 调用 Repository，传入 context 和回调函数
        pointRepo.getMyPointsHistory(requireContext()) { success, data, errorMsg ->
            // 注意：Retrofit 的 enqueue 默认在主线程回调，所以可以直接操作 UI
            if (success && data != null) {
                // 清空旧数据并添加新数据
                pointList.clear()
                pointList.addAll(data)
                // 通知适配器刷新
                adapter.notifyDataSetChanged()
            } else {
                // 这里可以弹个 Toast 提示错误
                // Toast.makeText(requireContext(), errorMsg ?: "获取失败", Toast.LENGTH_SHORT).show()

                // 如果后端还没通，暂时保留模拟数据
                pointList.clear()
                pointList.addAll(getMockData())
                adapter.notifyDataSetChanged()
            }
        }
    }
}