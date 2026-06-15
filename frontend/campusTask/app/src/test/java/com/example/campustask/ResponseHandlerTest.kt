package com.example.campustask

import android.content.Context
import com.example.campustask.utils.ResponseHandler
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ResponseHandlerTest {

    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `isUnauthorized should return true for 401`() {
        assertTrue(ResponseHandler.isUnauthorized(401))
    }

    @Test
    fun `isUnauthorized should return false for 200`() {
        assertFalse(ResponseHandler.isUnauthorized(200))
    }

    @Test
    fun `isUnauthorized should return false for 400`() {
        assertFalse(ResponseHandler.isUnauthorized(400))
    }

    @Test
    fun `isUnauthorized should return false for 500`() {
        assertFalse(ResponseHandler.isUnauthorized(500))
    }

    @Test
    fun `handleResponse should return true for 200`() {
        val result = ResponseHandler.handleResponse<String>(
            mockContext,
            200,
            { _, _ -> },
            { _ -> }
        )

        assertTrue(result)
    }

    @Test
    fun `handleResponse should return false for 401`() {
        val result = ResponseHandler.handleResponse<String>(
            null,
            401,
            { _, _ -> },
            { _ -> }
        )

        assertFalse(result)
    }

    @Test
    fun `handleResponse should return false for 400`() {
        val result = ResponseHandler.handleResponse<String>(
            mockContext,
            400,
            { _, _ -> },
            { _ -> }
        )

        assertFalse(result)
    }
}
