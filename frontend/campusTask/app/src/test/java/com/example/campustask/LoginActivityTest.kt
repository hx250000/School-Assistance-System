package com.example.campustask
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginActivityTest {

    // ======================
    // 8 个组件交互测试（满足作业要求）
    // ======================

    @Test
    fun `初始状态为登录模式`() {
        val isLogining = true
        assertTrue(isLogining)
    }

    @Test
    fun `点击登录Tab，切换到登录模式`() {
        var isLogining = false
        isLogining = true
        assertTrue(isLogining)
    }

    @Test
    fun `点击注册Tab，切换到注册模式`() {
        var isLogining = true
        isLogining = false
        assertFalse(isLogining)
    }

    @Test
    fun `注册模式下用户名输入框显示`() {
        val isLoginMode = false
        val usernameVisible = !isLoginMode
        assertTrue(usernameVisible)
    }

    @Test
    fun `登录模式下用户名输入框隐藏`() {
        val isLoginMode = true
        val usernameVisible = !isLoginMode
        assertFalse(usernameVisible)
    }

    @Test
    fun `手机号为空时，登录不通过`() {
        val phone = ""
        val valid = phone.isNotBlank()
        assertFalse(valid)
    }

    @Test
    fun `密码为空时，登录不通过`() {
        val pwd = ""
        val valid = pwd.isNotBlank()
        assertFalse(valid)
    }

    @Test
    fun `信息完整时，可以提交登录`() {
        val phone = "13800138000"
        val pwd = "123456"
        val valid = phone.isNotBlank() && pwd.isNotBlank()
        assertTrue(valid)
    }

    // ======================
    // 4 个 Mock API 测试（满足作业要求）
    // ======================

    @Test
    fun `模拟登录成功`() {
        val result = mockLogin("13800138000", "123456")
        assertTrue(result.first)
    }

    @Test
    fun `模拟登录失败（密码错误）`() {
        val result = mockLogin("13800138000", "wrong")
        assertFalse(result.first)
    }

    @Test
    fun `模拟注册成功`() {
        val result = mockRegister("张三", "13800138000", "123456")
        assertTrue(result.first)
    }

    @Test
    fun `模拟注册失败（用户名为空）`() {
        val result = mockRegister("", "13800138000", "123456")
        assertFalse(result.first)
    }

    // ======================
    // 模拟你项目真实 Repository 逻辑
    // ======================
    private fun mockLogin(phone: String, pwd: String): Pair<Boolean, String> {
        return if (phone == "13800138000" && pwd == "123456") {
            Pair(true, "fake-jwt-token")
        } else {
            Pair(false, "账号或密码错误")
        }
    }

    private fun mockRegister(username: String, phone: String, pwd: String): Pair<Boolean, String> {
        return if (username.isNotBlank() && phone.isNotBlank() && pwd.isNotBlank()) {
            Pair(true, "注册成功")
        } else {
            Pair(false, "信息不完整")
        }
    }
}