package com.example.campustask.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.adapter.PopupTaskAdapter
import com.example.campustask.repository.TaskRepository
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

        // 🔥 点击弹窗 → 已替换成新适配器，文字不竖排、不换行
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

            val recyclerView = popupView.findViewById<RecyclerView>(R.id.recyclerTasks)
            recyclerView.layoutManager = LinearLayoutManager(this)

            val taskList = TaskRepository.getAllTasks()

            // ✅ 使用新的简洁适配器
            val adapter = PopupTaskAdapter(taskList) { task ->
                val fragment = TaskDetailFragment.newInstance(task)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
                popupWindow.dismiss()
            }
            recyclerView.adapter = adapter

            // ✅ 弹窗位置优化，不偏移
            popupWindow.showAsDropDown(floatingBtn, -260, -220)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}