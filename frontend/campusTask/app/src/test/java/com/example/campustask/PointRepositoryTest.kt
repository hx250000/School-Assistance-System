package com.example.campustask

import android.content.Context
import com.example.campustask.model.PointRecord
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.response.PointHistoryResponse
import com.example.campustask.network.PointApi
import com.example.campustask.repository.PointRepository
import com.example.campustask.utils.AuthTokenStore
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PointRepositoryTest {

    private lateinit var pointRepository: PointRepository
    private lateinit var mockPointApi: PointApi
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockPointApi = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)
        TestAndroidSupport.mockAndroidLog()

        mockkObject(com.example.campustask.network.RetrofitClient)
        every { com.example.campustask.network.RetrofitClient.pointApi } returns mockPointApi

        pointRepository = PointRepository()

        mockkObject(AuthTokenStore)
    }

    @After
    fun tearDown() {
        unmockkAll()
        TestAndroidSupport.unmockAndroidLog()
    }

    // ===== 获取积分历史测试 =====

    @Test
    fun `getMyPointsHistory should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var errorCalled = false
        var errorMessage: String? = null

        pointRepository.getMyPointsHistory(mockContext) { success, data, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("用户未登录", errorMessage)
    }

    @Test
    fun `getMyPointsHistory should succeed with valid token`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockRecords = listOf(
            PointRecord(
                title = "完成任务",
                description = "完成第一个任务获得积分",
                changeAmount = 50,
                time = "2024-01-01 10:00"
            )
        )
        val mockResponse = PointHistoryResponse(mockRecords, 100, 5)
        val mockCall = mockk<Call<BaseResponse<PointHistoryResponse>>>(relaxed = true)
        val mockApiResponse = Response.success(BaseResponse(200, "success", mockResponse))

        every { mockPointApi.getMyPointsHistory(mockHeader) } returns mockCall

        var successCalled = false
        var pointData: PointHistoryResponse? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<PointHistoryResponse>>>()
            callback.onResponse(mockCall, mockApiResponse)
        }

        pointRepository.getMyPointsHistory(mockContext) { success, data, error ->
            if (success) {
                successCalled = true
                pointData = data
            }
        }

        assertTrue(successCalled)
        assertNotNull(pointData)
        assertEquals(100, pointData?.increasePoints)
        assertEquals(5, pointData?.decreasePoints)
    }

    @Test
    fun `getMyPointsHistory should fail with network error`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<PointHistoryResponse>>>(relaxed = true)

        every { mockPointApi.getMyPointsHistory(mockHeader) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<PointHistoryResponse>>>()
            callback.onFailure(mockCall, Throwable("网络错误"))
        }

        pointRepository.getMyPointsHistory(mockContext) { success, data, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertNotNull(errorMessage)
    }

    // ===== 获取当前积分测试 =====

    @Test
    fun `getMyCurrentPoints should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var errorCalled = false
        var errorMessage: String? = null

        pointRepository.getMyCurrentPoints(mockContext) { success, points, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("用户未登录", errorMessage)
    }

    @Test
    fun `getMyCurrentPoints should succeed with valid token`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<Int>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", 100))

        every { mockPointApi.getMyPoints(mockHeader) } returns mockCall

        var successCalled = false
        var currentPoints: Int? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<Int>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        pointRepository.getMyCurrentPoints(mockContext) { success, points, error ->
            if (success) {
                successCalled = true
                currentPoints = points
            }
        }

        assertTrue(successCalled)
        assertEquals(100, currentPoints)
    }

    @Test
    fun `getMyCurrentPoints should fail with network error`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<Int>>>(relaxed = true)

        every { mockPointApi.getMyPoints(mockHeader) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<Int>>>()
            callback.onFailure(mockCall, Throwable("网络错误"))
        }

        pointRepository.getMyCurrentPoints(mockContext) { success, points, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertNotNull(errorMessage)
    }
}