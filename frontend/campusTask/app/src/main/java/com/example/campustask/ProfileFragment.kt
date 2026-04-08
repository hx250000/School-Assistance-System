package com.example.campustask

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUserInfo(view)
        initClick(view)
        initQuickEntry(view) // ⭐ 新增
    }

    // 👉 用户数据
    private fun initUserInfo(view: View) {
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvId = view.findViewById<TextView>(R.id.tv_id)
        val tvPoint = view.findViewById<TextView>(R.id.tv_point)
        val tvCredit = view.findViewById<TextView>(R.id.tv_credit)

        tvName.text = "张三 ⭐"
        tvId.text = "ID: user1"
        tvPoint.text = "580"
        tvCredit.text = "95"
    }

    // 👉 我的服务 + 设置
    private fun initClick(view: View) {

        val itemMsg = view.findViewById<LinearLayout>(R.id.item_msg)
        val itemAchievement = view.findViewById<LinearLayout>(R.id.item_achievement)
        val itemExchange = view.findViewById<LinearLayout>(R.id.item_exchange)

        val itemTheme = view.findViewById<LinearLayout>(R.id.item_theme)
        val itemSetting = view.findViewById<LinearLayout>(R.id.item_setting)

        val btnLogout = view.findViewById<TextView>(R.id.btn_logout)

        // 消息
        itemMsg.setOnClickListener {
            Toast.makeText(context, "点击了消息通知", Toast.LENGTH_SHORT).show()
        }

        // 成就
        itemAchievement.setOnClickListener {
            Toast.makeText(context, "查看我的成就", Toast.LENGTH_SHORT).show()
        }

        // ⭐ 兑换记录（你这里原来跳错了）
        itemExchange.setOnClickListener {
            Toast.makeText(context, "兑换记录", Toast.LENGTH_SHORT).show()
            // 👉 这里以后可以跳 ExchangeFragment
        }

        // 主题
        itemTheme.setOnClickListener {
            Toast.makeText(context, "切换主题（后面可做夜间模式）", Toast.LENGTH_SHORT).show()
        }

        // 设置
        itemSetting.setOnClickListener {
            Toast.makeText(context, "进入设置", Toast.LENGTH_SHORT).show()
        }

        // 退出
        btnLogout.setOnClickListener {
            Toast.makeText(context, "退出登录", Toast.LENGTH_SHORT).show()
        }
    }

    // ⭐⭐ 快捷入口（重点）
    private fun initQuickEntry(view: View) {

        val btnHistory = view.findViewById<LinearLayout>(R.id.btn_history)
        val btnPoints = view.findViewById<LinearLayout>(R.id.btn_points)
        val btnAchievement = view.findViewById<LinearLayout>(R.id.btn_achievement)
        val btnDark = view.findViewById<LinearLayout>(R.id.btn_dark)

        // 任务历史
        btnHistory.setOnClickListener {
            Toast.makeText(context, "进入任务历史", Toast.LENGTH_SHORT).show()
        }

        // ⭐ 积分明细（核心）
        btnPoints.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PointsFragment())
                .addToBackStack(null)
                .commit()
        }

        // 我的成就
        btnAchievement.setOnClickListener {
            Toast.makeText(context, "进入我的成就", Toast.LENGTH_SHORT).show()
        }

        // 夜间模式
        btnDark.setOnClickListener {
            Toast.makeText(context, "切换夜间模式（待实现）", Toast.LENGTH_SHORT).show()
        }
    }
}