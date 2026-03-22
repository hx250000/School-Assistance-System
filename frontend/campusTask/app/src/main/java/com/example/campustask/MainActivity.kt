package com.example.campustask

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // 👉 默认加载首页（关键！）
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // 👉 点击底部栏切换页面
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.nav_home -> loadFragment(HomeFragment())

                R.id.nav_publish -> loadFragment(PublishFragment())

                R.id.nav_task -> loadFragment(MyTaskFragment())
//
//                R.id.nav_shop -> loadFragment(ShopFragment())
//
//                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}