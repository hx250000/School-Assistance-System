package com.example.campustask

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.Assert.*

class ApiMockTest {

    interface ApiService {
        fun login(username: String, password: String): Boolean
    }

    @Test
    fun `模拟接口调用成功`() {
        val api = mockk<ApiService>()

        every { api.login(any(), any()) } returns true

        val result = api.login("a", "b")

        assertTrue(result)
    }

    @Test
    fun `模拟登录失败`() {
        val api = mockk<ApiService>()

        every { api.login(any(), any()) } returns false

        val result = api.login("a", "c")

        assertFalse(result)
    }

    @Test
    fun `验证方法被调用一次`() {
        val api = mockk<ApiService>(relaxed = true)

        api.login("user", "123456")

        verify(exactly = 1) { api.login("user", "123456") }
    }

    @Test
    fun `空参数不会影响mock行为`() {
        val api = mockk<ApiService>(relaxed = true)

        every { api.login("", "") } returns false

        val result = api.login("", "")

        assertFalse(result)
    }
}