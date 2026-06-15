package com.example.campustask

import android.content.Context
import com.example.campustask.utils.AuthTokenStore
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class AuthTokenStoreTest {

    private lateinit var mockContext: Context
    private lateinit var mockSharedPreferences: android.content.SharedPreferences
    private lateinit var mockEditor: android.content.SharedPreferences.Editor

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockSharedPreferences = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)

        every { mockContext.applicationContext } returns mockContext
        every { mockContext.getSharedPreferences(any(), any()) } returns mockSharedPreferences
        every { mockSharedPreferences.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.remove(any()) } returns mockEditor
        every { mockEditor.apply() } just runs
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ===== ä¿å­Tokenæµè¯ =====

    @Test
    fun `saveToken should save token to SharedPreferences`() {
        val token = "test-token-123"

        AuthTokenStore.saveToken(mockContext, token)

        verify { mockSharedPreferences.edit() }
        verify { mockEditor.putString("jwt_token", token) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `saveToken should handle empty token`() {
        val emptyToken = ""

        AuthTokenStore.saveToken(mockContext, emptyToken)

        verify { mockEditor.putString("jwt_token", emptyToken) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `saveToken should handle null token`() {
        val nullToken: String? = null

        // åºè¯¥ä¸ä¼å´©æº
        AuthTokenStore.saveToken(mockContext, nullToken ?: "")

        verify { mockEditor.putString("jwt_token", "") }
        verify { mockEditor.apply() }
    }

    // ===== è·åTokenæµè¯ =====

    @Test
    fun `getToken should return saved token`() {
        val savedToken = "saved-token-456"
        every { mockSharedPreferences.getString("jwt_token", null) } returns savedToken

        val token = AuthTokenStore.getToken(mockContext)

        assertEquals(savedToken, token)
        verify { mockSharedPreferences.getString("jwt_token", null) }
    }

    @Test
    fun `getToken should return null when no token saved`() {
        every { mockSharedPreferences.getString("jwt_token", null) } returns null

        val token = AuthTokenStore.getToken(mockContext)

        assertNull(token)
        verify { mockSharedPreferences.getString("jwt_token", null) }
    }

    @Test
    fun `getToken should handle empty token`() {
        every { mockSharedPreferences.getString("jwt_token", null) } returns ""

        val token = AuthTokenStore.getToken(mockContext)

        assertEquals("", token)
    }

    // ===== æ¸é¤Tokenæµè¯ =====

    @Test
    fun `clearToken should remove token from SharedPreferences`() {
        AuthTokenStore.clearToken(mockContext)

        verify { mockSharedPreferences.edit() }
        verify { mockEditor.remove("jwt_token") }
        verify { mockEditor.apply() }
    }

    @Test
    fun `clearToken should be callable multiple times`() {
        AuthTokenStore.clearToken(mockContext)
        AuthTokenStore.clearToken(mockContext)
        AuthTokenStore.clearToken(mockContext)

        verify(exactly = 3) { mockEditor.remove("jwt_token") }
    }

    // ===== Authorization Headeræµè¯ =====

    @Test
    fun `authorizationHeader should return Bearer token`() {
        val rawToken = "raw-token-789"
        every { mockSharedPreferences.getString("jwt_token", null) } returns rawToken

        val header = AuthTokenStore.authorizationHeader(mockContext)

        assertEquals("Bearer $rawToken", header)
        assertEquals("Bearer raw-token-789", header)
    }

    @Test
    fun `authorizationHeader should return null when no token`() {
        every { mockSharedPreferences.getString("jwt_token", null) } returns null

        val header = AuthTokenStore.authorizationHeader(mockContext)

        assertNull(header)
    }

    @Test
    fun `authorizationHeader should handle empty token`() {
        every { mockSharedPreferences.getString("jwt_token", null) } returns ""

        val header = AuthTokenStore.authorizationHeader(mockContext)

        assertEquals("Bearer ", header)
    }

    // ===== Tokençå½å¨ææµè¯ =====

    @Test
    fun `token lifecycle should work correctly`() {
        val token = "lifecycle-token"

        // ä¿å­token
        AuthTokenStore.saveToken(mockContext, token)
        verify { mockEditor.putString("jwt_token", token) }

        // è·åtoken
        every { mockSharedPreferences.getString("jwt_token", null) } returns token
        val retrievedToken = AuthTokenStore.getToken(mockContext)
        assertEquals(token, retrievedToken)

        // çæheader
        val header = AuthTokenStore.authorizationHeader(mockContext)
        assertEquals("Bearer $token", header)

        // æ¸é¤token
        AuthTokenStore.clearToken(mockContext)
        verify { mockEditor.remove("jwt_token") }

        // åæ¬¡è·ååºè¯¥ä¸ºnull
        every { mockSharedPreferences.getString("jwt_token", null) } returns null
        val clearedToken = AuthTokenStore.getToken(mockContext)
        assertNull(clearedToken)
    }

    @Test
    fun `authorizationHeader should return null after clearToken`() {
        val token = "test-token"
        every { mockSharedPreferences.getString("jwt_token", null) } returns token

        val headerBeforeClear = AuthTokenStore.authorizationHeader(mockContext)
        assertNotNull(headerBeforeClear)

        AuthTokenStore.clearToken(mockContext)

        every { mockSharedPreferences.getString("jwt_token", null) } returns null
        val headerAfterClear = AuthTokenStore.authorizationHeader(mockContext)
        assertNull(headerAfterClear)
    }

    // ===== SharedPreferencesåç§°éªè¯ =====

    @Test
    fun `should use correct SharedPreferences name`() {
        val expectedName = "campus_task_auth"

        AuthTokenStore.saveToken(mockContext, "test")

        verify { mockContext.getSharedPreferences(expectedName, Context.MODE_PRIVATE) }
    }

    // ===== Tokenæ ¼å¼éªè¯ =====

    @Test
    fun `authorizationHeader should have correct format`() {
        val token = "format-test-token"
        every { mockSharedPreferences.getString("jwt_token", null) } returns token

        val header = AuthTokenStore.authorizationHeader(mockContext)

        assertTrue(header!!.startsWith("Bearer "))
        assertTrue(header.contains(token))
        assertEquals("Bearer format-test-token", header)
    }

    @Test
    fun `authorizationHeader should not have extra spaces`() {
        val token = "no-space-token"
        every { mockSharedPreferences.getString("jwt_token", null) } returns token

        val header = AuthTokenStore.authorizationHeader(mockContext)

        assertEquals("Bearer no-space-token", header)
        assertFalse(header!!.contains("  "))
    }
}

