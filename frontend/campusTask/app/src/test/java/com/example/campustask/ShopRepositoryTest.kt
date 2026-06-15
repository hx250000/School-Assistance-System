package com.example.campustask

import android.content.Context
import com.example.campustask.model.ShopItem
import com.example.campustask.model.request.ShopExchangeRequest
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.network.ShopApi
import com.example.campustask.repository.ShopRepository
import com.example.campustask.utils.AuthTokenStore
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopRepositoryTest {

    private lateinit var shopRepository: ShopRepository
    private lateinit var mockShopApi: ShopApi
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockShopApi = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)
        TestAndroidSupport.mockAndroidLog()

        mockkObject(com.example.campustask.network.RetrofitClient)
        every { com.example.campustask.network.RetrofitClient.shopApi } returns mockShopApi

        shopRepository = ShopRepository()

        mockkObject(AuthTokenStore)
    }

    @After
    fun tearDown() {
        unmockkAll()
        TestAndroidSupport.unmockAndroidLog()
    }

    // ===== 获取商品列表测试 =====

    @Test
    fun `getShopItems should succeed`() {
        val mockShopItems = listOf(
            ShopItem(
                id = 1L,
                name = "星巴克咖啡券",
                price = 200,
                stock = 100,
                description = "星巴克咖啡券",
                imageRes = "coffee"
            ),
            ShopItem(
                id = 2L,
                name = "奶茶券",
                price = 100,
                stock = 50,
                description = "奶茶券",
                imageRes = "drink"
            )
        )

        val mockCall = mockk<Call<BaseResponse<List<ShopItem>>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", mockShopItems))

        every { mockShopApi.listItems() } returns mockCall

        var successCalled = false
        var shopItems: List<ShopItem>? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<List<ShopItem>>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        shopRepository.getShopItems { success, items, error ->
            if (success) {
                successCalled = true
                shopItems = items
            }
        }

        assertTrue(successCalled)
        assertNotNull(shopItems)
        assertEquals(2, shopItems?.size)
        assertEquals("星巴克咖啡券", shopItems?.first()?.name)
    }

    @Test
    fun `getShopItems should fail with network error`() {
        val mockCall = mockk<Call<BaseResponse<List<ShopItem>>>>(relaxed = true)

        every { mockShopApi.listItems() } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<List<ShopItem>>>>()
            callback.onFailure(mockCall, Throwable("网络错误"))
        }

        shopRepository.getShopItems { success, items, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertNotNull(errorMessage)
    }

    @Test
    fun `getShopItems should fail with error response`() {
        val mockCall = mockk<Call<BaseResponse<List<ShopItem>>>>(relaxed = true)
        val mockResponse: Response<BaseResponse<List<ShopItem>>> = Response.success(BaseResponse(400, "服务器错误", emptyList<ShopItem>()))

        every { mockShopApi.listItems() } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<List<ShopItem>>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        shopRepository.getShopItems { success, items, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertNotNull(errorMessage)
    }

    // ===== 兑换商品测试 =====

    @Test
    fun `exchangeItem should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var errorCalled = false
        var errorMessage: String? = null

        shopRepository.exchangeItem(mockContext, 1L) { success, orderId, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("用户未登录", errorMessage)
    }

    @Test
    fun `exchangeItem should succeed with valid token`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<Long>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", 123L))

        every { mockShopApi.exchange(mockHeader, any()) } returns mockCall

        var successCalled = false
        var orderId: Long? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<Long>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        shopRepository.exchangeItem(mockContext, 1L) { success, id, error ->
            if (success) {
                successCalled = true
                orderId = id
            }
        }

        assertTrue(successCalled)
        assertEquals(123L, orderId)
    }

    @Test
    fun `exchangeItem should fail with insufficient points`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<Long>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(400, "积分不足", 0L))

        every { mockShopApi.exchange(mockHeader, any()) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<Long>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        shopRepository.exchangeItem(mockContext, 1L) { success, orderId, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertNotNull(errorMessage)
    }

    @Test
    fun `exchangeItem should fail with network error`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<Long>>>(relaxed = true)

        every { mockShopApi.exchange(mockHeader, any()) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<Long>>>()
            callback.onFailure(mockCall, Throwable("网络错误"))
        }

        shopRepository.exchangeItem(mockContext, 1L) { success, orderId, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertNotNull(errorMessage)
    }

    // ===== 获取兑换次数测试 =====

    @Test
    fun `getMyExchangeCount should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var errorCalled = false
        var errorMessage: String? = null

        shopRepository.getMyExchangeCount(mockContext) { success, count, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("用户未登录", errorMessage)
    }

    @Test
    fun `getMyExchangeCount should succeed with valid token`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<Int>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", 5))

        every { mockShopApi.getMyExchangeCount(mockHeader) } returns mockCall

        var successCalled = false
        var exchangeCount: Int? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<Int>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        shopRepository.getMyExchangeCount(mockContext) { success, count, error ->
            if (success) {
                successCalled = true
                exchangeCount = count
            }
        }

        assertTrue(successCalled)
        assertEquals(5, exchangeCount)
    }

    @Test
    fun `getMyExchangeCount should fail with network error`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<Int>>>(relaxed = true)

        every { mockShopApi.getMyExchangeCount(mockHeader) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<Int>>>()
            callback.onFailure(mockCall, Throwable("网络错误"))
        }

        shopRepository.getMyExchangeCount(mockContext) { success, count, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertNotNull(errorMessage)
    }
}