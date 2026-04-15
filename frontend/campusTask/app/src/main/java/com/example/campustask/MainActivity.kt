package com.example.campustask

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // 默认首页
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_publish -> loadFragment(PublishFragment())
                R.id.nav_task -> loadFragment(MyTaskFragment())
                R.id.nav_shop -> loadFragment(ShopFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }

        // 🔥 悬浮按钮逻辑
        val floatingBtn = findViewById<ImageView>(R.id.floatingBtn)

        floatingBtn.setOnClickListener {

            val popupView = LayoutInflater.from(this)
                .inflate(R.layout.popup_task_list, null)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.isOutsideTouchable = true

            val recyclerView =
                popupView.findViewById<RecyclerView>(R.id.recyclerTasks)

            val taskList = listOf(
                "帮拿外卖",
                "代取快递",
                "一起打游戏"
            )

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = SmallTaskAdapter(taskList) { taskName ->

                Toast.makeText(this, "打开任务：$taskName", Toast.LENGTH_SHORT).show()

                // 👉 跳转详情页
                val intent = Intent(this, TaskDetailFragment::class.java)
                intent.putExtra("task_name", taskName)
                startActivity(intent)

                popupWindow.dismiss()
            }

            popupWindow.showAsDropDown(floatingBtn, -250, -500)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}