package com.example.campustask

import android.content.Context
import com.example.campustask.model.Achievement
import com.example.campustask.model.AchievementOverview
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.network.AchievementApi
import com.example.campustask.repository.AchievementRepository
import com.example.campustask.utils.AuthTokenStore
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AchievementRepositoryTest {

    private lateinit var achievementRepository: AchievementRepository
    private lateinit var mockAchievementApi: AchievementApi
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockAchievementApi = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)
        TestAndroidSupport.mockAndroidLog()

        mockkObject(com.example.campustask.network.RetrofitClient)
        every { com.example.campustask.network.RetrofitClient.achievementApi } returns mockAchievementApi

        achievementRepository = AchievementRepository()

        mockkObject(AuthTokenStore)
    }

    @After
    fun tearDown() {
        unmockkAll()
        TestAndroidSupport.unmockAndroidLog()
    }

    @Test
    fun `getMyAchievements should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var errorCalled = false
        var errorMessage: String? = null

        achievementRepository.getMyAchievements(mockContext) { success, overview, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("用户未登录", errorMessage)
    }

    @Test
    fun `getMyAchievements should succeed with valid token`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockAchievementOverview = AchievementOverview(
            totalCount = 5,
            unlockedCount = 3,
            completionRate = 0.6,
            achievements = listOf(
                Achievement(
                    id = 1L,
                    title = "初学者",
                    description = "完成第一个任务",
                    currentProgress = 1,
                    totalProgress = 1,
                    iconUrl = "star",
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                ),
                Achievement(
                    id = 2L,
                    title = "活跃用户",
                    description = "完成10个任务",
                    currentProgress = 5,
                    totalProgress = 10,
                    iconUrl = "badge",
                    isUnlocked = false,
                    unlockedAt = null
                )
            )
        )

        val mockCall = mockk<Call<BaseResponse<AchievementOverview>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", mockAchievementOverview))

        every { mockAchievementApi.getMyAchievements(mockHeader) } returns mockCall

        var successCalled = false
        var overview: AchievementOverview? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<AchievementOverview>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        achievementRepository.getMyAchievements(mockContext) { success, achievementOverview, error ->
            if (success) {
                successCalled = true
                overview = achievementOverview
            }
        }

        assertTrue(successCalled)
        assertNotNull(overview)
        assertEquals(5, overview?.totalCount)
        assertEquals(3, overview?.unlockedCount)
        assertEquals(2, overview?.achievements?.size)
        assertEquals("初学者", overview?.achievements?.first()?.title)
        assertTrue(overview?.achievements?.first()?.isUnlocked == true)
    }

    @Test
    fun `getMyAchievements should fail with network error`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<AchievementOverview>>>(relaxed = true)

        every { mockAchievementApi.getMyAchievements(mockHeader) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<AchievementOverview>>>()
            callback.onFailure(mockCall, Throwable("网络错误"))
        }

        achievementRepository.getMyAchievements(mockContext) { success, overview, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("网络连接失败，请稍后再试", errorMessage)
    }

    @Test
    fun `getMyAchievements should fail with error response`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<AchievementOverview>>>(relaxed = true)
        val mockResponse = Response.success(
            BaseResponse(400, "获取成就记录失败", AchievementOverview(0, 0, 0.0, emptyList()))
        )

        every { mockAchievementApi.getMyAchievements(mockHeader) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<AchievementOverview>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        achievementRepository.getMyAchievements(mockContext) { success, overview, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("获取成就记录失败", errorMessage)
    }

    @Test
    fun `Achievement should have correct data`() {
        val achievement = Achievement(
            id = 1L,
            title = "初学者",
            description = "完成第一个任务",
            currentProgress = 1,
            totalProgress = 1,
            iconUrl = "star",
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis()
        )

        assertEquals(1L, achievement.id)
        assertEquals("初学者", achievement.title)
        assertEquals("完成第一个任务", achievement.description)
        assertEquals("star", achievement.iconUrl)
        assertTrue(achievement.isUnlocked)
        assertEquals(1, achievement.currentProgress)
        assertEquals(1, achievement.totalProgress)
    }

    @Test
    fun `AchievementOverview should have correct data`() {
        val overview = AchievementOverview(
            totalCount = 10,
            unlockedCount = 5,
            completionRate = 0.5,
            achievements = listOf(
                Achievement(
                    id = 1L,
                    title = "成就1",
                    description = "描述1",
                    currentProgress = 100,
                    totalProgress = 100,
                    iconUrl = "icon1",
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
            )
        )

        assertEquals(10, overview.totalCount)
        assertEquals(5, overview.unlockedCount)
        assertEquals(1, overview.achievements.size)
        assertEquals("成就1", overview.achievements.first().title)
    }

    @Test
    fun `Achievement progress should be between 0 and total`() {
        val achievement = Achievement(
            id = 1L,
            title = "测试成就",
            description = "测试",
            currentProgress = 5,
            totalProgress = 10,
            iconUrl = "test",
            isUnlocked = false,
            unlockedAt = null
        )

        assertTrue(achievement.currentProgress >= 0)
        assertTrue(achievement.currentProgress <= achievement.totalProgress)
    }

    @Test
    fun `Achievement should be unlocked when progress equals total`() {
        val achievement = Achievement(
            id = 1L,
            title = "已解锁成就",
            description = "测试",
            currentProgress = 100,
            totalProgress = 100,
            iconUrl = "test",
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis()
        )

        assertTrue(achievement.isUnlocked)
        assertEquals(achievement.currentProgress, achievement.totalProgress)
    }
}
