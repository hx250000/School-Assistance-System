package com.example.campustask

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.Assert.*

class ApiMockTest {

    // 模拟 Retrofit 接口
    interface ApiService {
        fun login(username: String, password: String): Boolean
    }

    @Test
    fun `模拟接口调用成功`() {
        val api = mockk<ApiService>()
        every { api.login(any(), any()) } returns true
        assertTrue(api.login("a", "b"))
    }

    @Test
    fun `模拟登录失败`() {
        val api = mockk<ApiService>()
        every { api.login(any(), any()) } returns false
        assertFalse(api.login("a", "c"))
    }

    @Test
    fun `验证方法被调用一次`() {
        val api = mockk<ApiService>(relaxed = true)
        api.login("user", "123456")
        verify(exactly = 1) { api.login("user", "123456") }
    }

    @Test
    fun `空参数不触发真实请求`() {
        val api = mockk<ApiService>(relaxed = true)
        api.login("", "")
        verify(exactly = 0) { api.login("realUser", "realPwd") }
    }
}