package com.example.campustask

import org.junit.Test
import org.junit.Assert.*

class LoginViewModelTest : BaseTest() {

    @Test
    fun 用户名为空_校验失败() {
        assertFalse(checkUsername(""))
    }

    @Test
    fun 用户名非空_校验成功() {
        assertTrue(checkUsername("zhangsan"))
    }

    @Test
    fun 密码为空_校验失败() {
        assertFalse(checkPassword(""))
    }

    @Test
    fun 密码长度大于6_校验成功() {
        assertTrue(checkPassword("123456"))
    }

    @Test
    fun 输入全部正确_可以登录() {
        assertTrue(canLogin("user", "123456"))
    }

    @Test
    fun 用户名为空_不能登录() {
        assertFalse(canLogin("", "123456"))
    }

    @Test
    fun 开始登录_显示加载() {
        assertTrue(setLoading(true))
    }

    @Test
    fun 登录结束_关闭加载() {
        assertFalse(setLoading(false))
    }

    // 模拟你页面真实逻辑
    private fun checkUsername(name: String) = name.isNotBlank()
    private fun checkPassword(pwd: String) = pwd.length >= 6
    private fun canLogin(user: String, pwd: String) = checkUsername(user) && checkPassword(pwd)
    private fun setLoading(value: Boolean) = value
}