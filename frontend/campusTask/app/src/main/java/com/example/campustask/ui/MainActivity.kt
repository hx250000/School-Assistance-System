package com.example.campustask.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.ui.MyTaskFragment
import com.example.campustask.ui.ProfileFragment
import com.example.campustask.ui.PublishFragment
import com.example.campustask.R
import com.example.campustask.ui.ShopFragment
import com.example.campustask.ui.TaskDetailFragment
import com.example.campustask.adapter.SmallTaskAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var selectedType = ""

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

        val floatingBtn = findViewById<ImageView>(R.id.floatingBtn)

        // 🔥 ========== 可拖动逻辑 ==========
        var dX = 0f
        var dY = 0f
        var isDragging = false

        floatingBtn.setOnTouchListener { view, event ->

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                    isDragging = false
                }

                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()

                    isDragging = true
                }

                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        view.performClick()
                    } else {
                        // 👉 吸附边缘
                        val screenWidth = resources.displayMetrics.widthPixels
                        val targetX =
                            if (view.x > screenWidth / 2)
                                (screenWidth - view.width).toFloat()
                            else 0f

                        view.animate()
                            .x(targetX)
                            .setDuration(200)
                            .start()
                    }
                }
            }
            true
        }

        // 🔥 点击事件（弹窗）
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

                // 🔥 正确方式：Fragment 跳转
                val fragment = TaskDetailFragment()
                val bundle = Bundle()
                bundle.putString("task_name", taskName)
                fragment.arguments = bundle

                loadFragment(fragment)

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