package com.example.campustask.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.adapter.PointsAdapter
import com.example.campustask.model.PointRecord
import com.example.campustask.repository.PointRepository
import okhttp3.internal.format

class PointsFragment : Fragment(R.layout.fragment_points) {

    private val TAG="PointFragment"

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
        var increasePoints =0
        var decreasePoints =0
        // 调用 Repository，传入 context 和回调函数
        pointRepo.getMyPointsHistory(requireContext()) { success, data, errorMsg ->
            if (success && data != null) {
                // 清空旧数据并添加新数据
                pointList.clear()
                pointList.addAll(data.pointsHistoryList)
                increasePoints=data.increasePoints
                decreasePoints=data.decreasePoints

                Log.d(TAG,"Data: "+data.toString())
                Log.d(TAG,"increase="+increasePoints+", decrease="+decreasePoints)

                updatePointsUI(increasePoints,decreasePoints)
                // 通知适配器刷新
                adapter.notifyDataSetChanged()
            } else {
                // 这里可以弹个 Toast 提示错误
                // Toast.makeText(requireContext(), errorMsg ?: "获取失败", Toast.LENGTH_SHORT).show()

                // 如果后端还没通，暂时保留模拟数据
                pointList.clear()
                pointList.addAll(getMockData())
                increasePoints=177
                decreasePoints=-700

                updatePointsUI(increasePoints,decreasePoints)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun updatePointsUI(increase: Int, decrease: Int) {
        // 使用 view?.findViewById 确保在回调触发时，如果用户已经退出了页面，程序不会崩溃
        Log.d(TAG, format("increase=%d, decrease=%d",increase,decrease))
        view?.let { root ->
            val incomeText = root.findViewById<TextView>(R.id.tv_income)
            val expenseText = root.findViewById<TextView>(R.id.tv_expense)

            // 使用字符串模板让显示更美观
            incomeText?.text = if (increase >= 0) "+$increase" else increase.toString()
            expenseText?.text = decrease.toString()
        }
    }
}