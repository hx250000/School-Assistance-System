package com.example.campustask.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.campustask.ui.MainActivity
import com.example.campustask.R
import com.example.campustask.repository.UserRepository
import com.example.campustask.utils.AuthTokenStore

class LoginActivity : AppCompatActivity() {

    private var isLogining = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tabLogin = findViewById<TextView>(R.id.tab_login)
        val tabRegister = findViewById<TextView>(R.id.tab_register)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPhone = findViewById<EditText>(R.id.et_phone)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val tvSub = findViewById<TextView>(R.id.tv_sub)

        val userRepo = UserRepository()

        // 登录tab
        tabLogin.setOnClickListener {
            isLogining = true
            etUsername.visibility = View.GONE

            tabLogin.setBackgroundResource(R.drawable.bg_tab_selected)
            tabRegister.setBackgroundResource(0)

            tvTitle.text = "欢迎回来"
            tvSub.text = "登录你的账号继续探索"
            btnSubmit.text = "登录"
        }

        // 注册tab
        tabRegister.setOnClickListener {
            isLogining = false
            etUsername.visibility = View.VISIBLE

            tabRegister.setBackgroundResource(R.drawable.bg_tab_selected)
            tabLogin.setBackgroundResource(0)

            tvTitle.text = "开始冒险"
            tvSub.text = "创建账号开启新旅程"
            btnSubmit.text = "注册"
        }

        btnSubmit.setOnClickListener {
            val username = etUsername.text.toString()
            val phone = etPhone.text.toString()
            val password = etPassword.text.toString()

            if (isLogining) {
                userRepo.login(phone, password) { success, tokenOrMsg ->
                    if (success) {
                        val jwt = tokenOrMsg
                        if (!jwt.isNullOrBlank()) {
                            AuthTokenStore.saveToken(this, jwt)
                        }
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "登录失败: $tokenOrMsg", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                userRepo.register(username, phone, password) { success, msg ->
                    if (success) {
                        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
                        isLogining = true
                        tabLogin.performClick()
                    } else {
                        Toast.makeText(this, "注册失败: $msg", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
//override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//
//    val tv = TextView(this)
//    tv.text = "Hello"
//    setContentView(tv)
//}
}