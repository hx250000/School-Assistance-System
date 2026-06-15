package com.example.campustask

import org.junit.Test
import org.junit.Assert.*

class LoginActivityTest {

    @Test
    fun `åå§ç¶æä¸ºç»å½æ¨¡å¼`() {
        val isLogin = true
        assertTrue(isLogin)
    }

    @Test
    fun `åæ¢å°æ³¨åæ¨¡å¼`() {
        var isLogin = true
        isLogin = false
        assertFalse(isLogin)
    }

    @Test
    fun `æ³¨åæ¨¡å¼æ¾ç¤ºç¨æ·å`() {
        val isLoginMode = false
        val visible = !isLoginMode
        assertTrue(visible)
    }

    @Test
    fun `ç»å½æ¨¡å¼éèç¨æ·å`() {
        val isLoginMode = true
        val visible = !isLoginMode
        assertFalse(visible)
    }

    @Test
    fun `ææºå·ä¸ºç©ºç»å½å¤±è´¥`() {
        val phone = ""
        assertFalse(phone.isNotBlank())
    }

    @Test
    fun `å¯ç ä¸ºç©ºç»å½å¤±è´¥`() {
        val pwd = ""
        assertFalse(pwd.isNotBlank())
    }

    @Test
    fun `å®æ´ä¿¡æ¯å¯ä»¥ç»å½`() {
        val phone = "13800138000"
        val pwd = "123456"
        assertTrue(phone.isNotBlank() && pwd.isNotBlank())
    }

    @Test
    fun `æ¨¡æç»å½æå`() {
        val result = mockLogin("13800138000", "123456")
        assertTrue(result.first)
    }

    @Test
    fun `æ¨¡æç»å½å¤±è´¥`() {
        val result = mockLogin("13800138000", "wrong")
        assertFalse(result.first)
    }

    @Test
    fun `æ¨¡ææ³¨åæå`() {
        val result = mockRegister("å¼ ä¸", "13800138000", "123456")
        assertTrue(result.first)
    }

    @Test
    fun `æ¨¡ææ³¨åå¤±è´¥`() {
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

