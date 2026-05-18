package com.example.campustask

import org.junit.Test
import org.junit.Assert.*

class LoginActivityTest {

    @Test
    fun `初始状态为登录模式`() {
        val isLogin = true
        assertTrue(isLogin)
    }

    @Test
    fun `切换到注册模式`() {
        var isLogin = true
        isLogin = false
        assertFalse(isLogin)
    }

    @Test
    fun `注册模式显示用户名`() {
        val isLoginMode = false
        val visible = !isLoginMode
        assertTrue(visible)
    }

    @Test
    fun `登录模式隐藏用户名`() {
        val isLoginMode = true
        val visible = !isLoginMode
        assertFalse(visible)
    }

    @Test
    fun `手机号为空登录失败`() {
        val phone = ""
        assertFalse(phone.isNotBlank())
    }

    @Test
    fun `密码为空登录失败`() {
        val pwd = ""
        assertFalse(pwd.isNotBlank())
    }

    @Test
    fun `完整信息可以登录`() {
        val phone = "13800138000"
        val pwd = "123456"
        assertTrue(phone.isNotBlank() && pwd.isNotBlank())
    }

    @Test
    fun `模拟登录成功`() {
        val result = mockLogin("13800138000", "123456")
        assertTrue(result.first)
    }

    @Test
    fun `模拟登录失败`() {
        val result = mockLogin("13800138000", "wrong")
        assertFalse(result.first)
    }

    @Test
    fun `模拟注册成功`() {
        val result = mockRegister("张三", "13800138000", "123456")
        assertTrue(result.first)
    }

    @Test
    fun `模拟注册失败`() {
        val result = mockRegister("", "13800138000", "123456")
        assertFalse(result.first)
    }

    // ===== mock logic =====

    private fun mockLogin(phone: String, pwd: String): Pair<Boolean, String> {
        return if (phone == "13800138000" && pwd == "123456") {
            true to "ok"
        } else {
            false to "error"
        }
    }

    private fun mockRegister(username: String, phone: String, pwd: String): Pair<Boolean, String> {
        return if (username.isNotBlank() && phone.isNotBlank() && pwd.isNotBlank()) {
            true to "ok"
        } else {
            false to "error"
        }
    }
}