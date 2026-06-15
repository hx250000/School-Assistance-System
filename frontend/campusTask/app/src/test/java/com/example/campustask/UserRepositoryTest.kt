package com.example.campustask

import android.content.Context
import android.net.Uri
import com.example.campustask.model.UserInfo
import com.example.campustask.model.request.LoginRequest
import com.example.campustask.model.request.RegisterRequest
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.response.FileUploadResponse
import com.example.campustask.model.response.LoginResponse
import com.example.campustask.model.response.RegisterResponse
import com.example.campustask.network.UserApi
import com.example.campustask.repository.UserRepository
import com.example.campustask.utils.AuthTokenStore
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepositoryTest {

    private lateinit var userRepository: UserRepository
    private lateinit var mockUserApi: UserApi
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockUserApi = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)
        TestAndroidSupport.mockAndroidLog()

        mockkObject(com.example.campustask.network.RetrofitClient)
        every { com.example.campustask.network.RetrofitClient.userApi } returns mockUserApi

        userRepository = UserRepository()

        mockkObject(AuthTokenStore)
    }

    @After
    fun tearDown() {
        unmockkAll()
        TestAndroidSupport.unmockAndroidLog()
    }

    // ===== MockÂµÃÃÂ¼Ã¦ÂµÂÃ¨Â¯Â =====

    @Test
    fun `mockLogin should succeed with correct credentials`() {
        var successCalled = false
        var token: String? = null

        userRepository.mockLogin("18069801871", "123456") { success, result ->
            if (success) {
                successCalled = true
                token = result
            }
        }

        assertTrue(successCalled)
        assertEquals("mock-token-123", token)
    }

    @Test
    fun `mockLogin should fail with wrong credentials`() {
        var errorCalled = false
        var errorMessage: String? = null

        userRepository.mockLogin("wrong_phone", "wrong_password") { success, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("手机号或密码错误", errorMessage)
    }

    // ===== Mock注册测试 =====

    @Test
    fun `mockRegister should succeed with valid data`() {
        var successCalled = false

        userRepository.mockRegister("测试用户", "13800138000", "123456") { success, error ->
            if (success) {
                successCalled = true
            }
        }

        assertTrue(successCalled)
    }

    @Test
    fun `mockRegister should fail with empty username`() {
        var errorCalled = false
        var errorMessage: String? = null

        userRepository.mockRegister("", "13800138000", "123456") { success, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("注册信息不完整", errorMessage)
    }

    @Test
    fun `mockRegister should fail with empty phone`() {
        var errorCalled = false

        userRepository.mockRegister("测试用户", "", "123456") { success, error ->
            if (!success) {
                errorCalled = true
            }
        }

        assertTrue(errorCalled)
    }

    @Test
    fun `mockRegister should fail with empty password`() {
        var errorCalled = false

        userRepository.mockRegister("测试用户", "13800138000", "") { success, error ->
            if (!success) {
                errorCalled = true
            }
        }

        assertTrue(errorCalled)
    }

    // ===== ÃÃ¸ÃÃ§APIÂµÃÃÂ¼Ã¦ÂµÂÃ¨Â¯Â =====

    @Test
    fun `login should succeed with valid credentials`() {
        val mockCall = mockk<Call<BaseResponse<LoginResponse>>>(relaxed = true)
        val mockResponse = Response.success(
            BaseResponse(200, "success", LoginResponse("test-token-123", 1L, "测试用户"))
        )

        every { mockUserApi.login(any()) } returns mockCall

        var successCalled = false
        var token: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<LoginResponse>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        userRepository.login("13800138000", "123456") { success, result ->
            if (success) {
                successCalled = true
                token = result
            }
        }

        assertTrue(successCalled)
        assertEquals("test-token-123", token)
    }

    @Test
    fun `login should fail with wrong credentials`() {
        val mockCall = mockk<Call<BaseResponse<LoginResponse>>>(relaxed = true)
        val mockResponse = Response.success(
            BaseResponse(400, "手机号或密码错误", LoginResponse("", 0L, ""))
        )

        every { mockUserApi.login(any()) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<LoginResponse>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        userRepository.login("wrong", "wrong") { success, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("手机号或密码错误", errorMessage)
    }

    @Test
    fun `login should fail with network error`() {
        val mockCall = mockk<Call<BaseResponse<LoginResponse>>>(relaxed = true)

        every { mockUserApi.login(any()) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<LoginResponse>>>()
            callback.onFailure(mockCall, Throwable("网络连接失败"))
        }

        userRepository.login("13800138000", "123456") { success, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("网络异常，请稍后再试", errorMessage)
    }

    // ===== 网络API注册测试 =====

    @Test
    fun `register should succeed with valid data`() {
        val mockCall = mockk<Call<BaseResponse<RegisterResponse>>>(relaxed = true)
        val mockResponse = Response.success(
            BaseResponse(200, "success", RegisterResponse("测试用户", 1L))
        )

        every { mockUserApi.register(any()) } returns mockCall

        var successCalled = false

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<RegisterResponse>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        userRepository.register("测试用户", "13800138000", "123456") { success, error ->
            if (success) {
                successCalled = true
            }
        }

        assertTrue(successCalled)
    }

    @Test
    fun `register should fail with duplicate phone`() {
        val mockCall = mockk<Call<BaseResponse<RegisterResponse>>>(relaxed = true)
        val mockResponse = Response.success(
            BaseResponse(400, "手机号已注册", RegisterResponse("", 0L))
        )

        every { mockUserApi.register(any()) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<RegisterResponse>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        userRepository.register("测试用户", "13800138000", "123456") { success, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("手机号已注册", errorMessage)
    }

    // ===== 获取个人信息测试 =====

    @Test
    fun `getMyInfo should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var errorCalled = false
        var errorMessage: String? = null

        userRepository.getMyInfo(mockContext) { success, userInfo, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("用户未登录", errorMessage)
    }

    @Test
    fun `getMyInfo should succeed with valid token`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockUserInfo = UserInfo(
            id = 1L,
            username = "测试用户",
            phone = "13800138000",
            points = 100,
            admin = false,
            creditScore = 80,
            level = 2,
            avatarUrl = "",
            createdAt = "2024-01-01"
        )

        val mockCall = mockk<Call<BaseResponse<UserInfo>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", mockUserInfo))

        every { mockUserApi.getMyInfo(mockHeader) } returns mockCall

        var successCalled = false
        var userInfo: UserInfo? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<UserInfo>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        userRepository.getMyInfo(mockContext) { success, info, error ->
            if (success) {
                successCalled = true
                userInfo = info
            }
        }

        assertTrue(successCalled)
        assertNotNull(userInfo)
        assertEquals("测试用户", userInfo?.username)
        assertEquals(100, userInfo?.points)
    }

    @Test
    fun `getMyInfo should fail with network error`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<UserInfo>>>(relaxed = true)

        every { mockUserApi.getMyInfo(mockHeader) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<UserInfo>>>()
            callback.onFailure(mockCall, Throwable("网络错误"))
        }

        userRepository.getMyInfo(mockContext) { success, userInfo, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("网络异常，请稍后再试", errorMessage)
    }

    // ===== 上传头像测试 =====

    @Test
    fun `uploadAvatar should fail when user not logged in`() {
        val mockUri = mockk<Uri>(relaxed = true)
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var errorCalled = false
        var errorMessage: String? = null

        userRepository.uploadAvatar(mockContext, mockUri) { success, response, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("用户未登录", errorMessage)
    }

    @Test
    fun `uploadAvatar should fail when cannot read file`() {
        val mockHeader = "Bearer test-token"
        val mockUri = mockk<Uri>(relaxed = true)
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader
        every { mockContext.contentResolver.openInputStream(mockUri) } returns null

        var errorCalled = false
        var errorMessage: String? = null

        userRepository.uploadAvatar(mockContext, mockUri) { success, response, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("无法读取图片文件", errorMessage)
    }
}

