package com.example.campustask.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.campustask.R
import com.example.campustask.repository.UserRepository
import com.example.campustask.utils.AuthTokenStore

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val userRepo = UserRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUserInfo(view)
        initClick(view)
        initQuickEntry(view)
    }

    // 用户数据
    private fun initUserInfo(view: View) {
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvId = view.findViewById<TextView>(R.id.tv_id)
        val tvPoint = view.findViewById<TextView>(R.id.tv_point)
        val tvCredit = view.findViewById<TextView>(R.id.tv_credit)

        userRepo.getMyInfo(requireContext()) { success, userInfo, error ->
            if (success && userInfo != null) {
                // 收到后端数据后更新 UI
                tvName.text = userInfo.username
                tvId.text = "ID: ${userInfo.id}"
                tvPoint.text = userInfo.points.toString()
                tvCredit.text = userInfo.creditScore.toString()
            } else {
                Toast.makeText(context, "加载个人信息失败: $error", Toast.LENGTH_SHORT).show()
            }
        }
//
//        tvName.text = "张三 ⭐"
//        tvId.text = "ID: user1"
//        tvPoint.text = "580"
//        tvCredit.text = "95"
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
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AchievementFragment())
                .addToBackStack(null)
                .commit()
        }

        // 兑换记录
        itemExchange.setOnClickListener {
            Toast.makeText(context, "兑换记录", Toast.LENGTH_SHORT).show()
            // 👉 这里以后可以跳 ExchangeFragment
        }

        // 主题
        itemTheme.setOnClickListener {

            val sp = requireContext().getSharedPreferences("theme", Context.MODE_PRIVATE)

            val isNight = sp.getBoolean("night", false)
            val newMode = !isNight

            // 保存状态
            sp.edit().putBoolean("night", newMode).apply()

            // 切换模式
            if (newMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(context, "已切换为夜间模式 🌙", Toast.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(context, "已切换为日间模式 ☀️", Toast.LENGTH_SHORT).show()
            }

            // 刷新界面（Fragment 必须）
            requireActivity().recreate()
        }

        // 设置
        itemSetting.setOnClickListener {
            Toast.makeText(context, "进入设置", Toast.LENGTH_SHORT).show()
        }

        // 退出
        btnLogout.setOnClickListener {
            AuthTokenStore.clearToken(requireContext()) // 清理 JWT
            Toast.makeText(context, "已退出登录", Toast.LENGTH_SHORT).show()
            // 跳转回 LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    // 快捷入口
    private fun initQuickEntry(view: View) {

        val btnHistory = view.findViewById<LinearLayout>(R.id.btn_history)
        val btnPoints = view.findViewById<LinearLayout>(R.id.btn_points)
        val btnAchievement = view.findViewById<LinearLayout>(R.id.btn_achievement)
        val btnDark = view.findViewById<LinearLayout>(R.id.btn_dark)

        // 任务历史
        // 任务历史
        btnHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyTaskFragment.newInstance("FINISHED"))
                .addToBackStack(null)
                .commit()
        }

        // 积分明细
        btnPoints.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PointsFragment())
                .addToBackStack(null)
                .commit()
        }

        // 我的成就
        btnAchievement.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AchievementFragment())
                .addToBackStack(null)
                .commit()
        }

        // 夜间模式
        btnDark.setOnClickListener {

            val sp = requireContext().getSharedPreferences("theme", Context.MODE_PRIVATE)

            val isNight = sp.getBoolean("night", false)
            val newMode = !isNight

            // 保存状态
            sp.edit().putBoolean("night", newMode).apply()

            // 切换模式
            if (newMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(context, "已切换为夜间模式 🌙", Toast.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(context, "已切换为日间模式 ☀️", Toast.LENGTH_SHORT).show()
            }

            // 刷新界面（Fragment 必须）
            requireActivity().recreate()
        }
    }
}