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
    }

    // 👉 模拟用户数据
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

    // 👉 点击事件
    private fun initClick(view: View) {

        val itemMsg = view.findViewById<LinearLayout>(R.id.item_msg)
        val itemAchievement = view.findViewById<LinearLayout>(R.id.item_achievement)
        val itemExchange = view.findViewById<LinearLayout>(R.id.item_exchange)

        val itemTheme = view.findViewById<LinearLayout>(R.id.item_theme)
        val itemSetting = view.findViewById<LinearLayout>(R.id.item_setting)

        val btnLogout = view.findViewById<TextView>(R.id.btn_logout)

        itemMsg.setOnClickListener {
            Toast.makeText(context, "点击了消息通知", Toast.LENGTH_SHORT).show()
        }

        itemAchievement.setOnClickListener {
            Toast.makeText(context, "查看我的成就", Toast.LENGTH_SHORT).show()
        }

        itemExchange.setOnClickListener {
            Toast.makeText(context, "兑换记录", Toast.LENGTH_SHORT).show()
        }

        itemTheme.setOnClickListener {
            Toast.makeText(context, "切换主题（后面可做夜间模式）", Toast.LENGTH_SHORT).show()
        }

        itemSetting.setOnClickListener {
            Toast.makeText(context, "进入设置", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            Toast.makeText(context, "退出登录", Toast.LENGTH_SHORT).show()
        }
    }
}