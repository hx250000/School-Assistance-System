package com.example.campustask

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private var isLogin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tabLogin = findViewById<TextView>(R.id.tab_login)
        val tabRegister = findViewById<TextView>(R.id.tab_register)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val tvSub = findViewById<TextView>(R.id.tv_sub)

        // 👉 登录
        tabLogin.setOnClickListener {
            isLogin = true
            etUsername.visibility = View.GONE

            tabLogin.setBackgroundResource(R.drawable.bg_tab_selected)
            tabRegister.setBackgroundResource(0)

            tvTitle.text = "欢迎回来"
            tvSub.text = "登录你的账号继续探索"
            btnSubmit.text = "登录"
        }

        // 👉 注册
        tabRegister.setOnClickListener {
            isLogin = false
            etUsername.visibility = View.VISIBLE

            tabRegister.setBackgroundResource(R.drawable.bg_tab_selected)
            tabLogin.setBackgroundResource(0)

            tvTitle.text = "开始冒险"
            tvSub.text = "创建账号开启新旅程"
            btnSubmit.text = "注册"
        }

        // 👉 点击按钮
        btnSubmit.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}