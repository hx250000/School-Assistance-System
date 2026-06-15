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
    fun `æ¨¡ææ¥å£è°ç¨æå`() {
        val api = mockk<ApiService>()

        every { api.login(any(), any()) } returns true

        val result = api.login("a", "b")

        assertTrue(result)
    }

    @Test
    fun `æ¨¡æç»å½å¤±è´¥`() {
        val api = mockk<ApiService>()

        every { api.login(any(), any()) } returns false

        val result = api.login("a", "c")

        assertFalse(result)
    }

    @Test
    fun `éªè¯æ¹æ³è¢«è°ç¨ä¸æ¬¡`() {
        val api = mockk<ApiService>(relaxed = true)

        api.login("user", "123456")

        verify(exactly = 1) { api.login("user", "123456") }
    }

    @Test
    fun `ç©ºåæ°ä¸ä¼å½±åmockè¡ä¸º`() {
        val api = mockk<ApiService>(relaxed = true)

        every { api.login("", "") } returns false

        val result = api.login("", "")

        assertFalse(result)
    }
}

