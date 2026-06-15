package com.example.campustask

import android.content.Context
import com.example.campustask.model.Task
import com.example.campustask.model.UserInfo
import com.example.campustask.model.request.GrabTaskRequest
import com.example.campustask.model.request.TaskCreateRequest
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.response.HomeStatResp
import com.example.campustask.network.TaskApi
import com.example.campustask.repository.TaskRepository
import com.example.campustask.utils.AuthTokenStore
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskRepositoryTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var mockTaskApi: TaskApi
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockTaskApi = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)
        TestAndroidSupport.mockAndroidLog()

        mockkObject(com.example.campustask.network.RetrofitClient)
        every { com.example.campustask.network.RetrofitClient.taskApi } returns mockTaskApi

        taskRepository = TaskRepository()

        mockkObject(AuthTokenStore)
    }

    @After
    fun tearDown() {
        unmockkAll()
        TestAndroidSupport.unmockAndroidLog()
    }

    // ===== Mockæ°æ®æµè¯ =====

    @Test
    fun `mockGetAllTasks should return task list`() {
        val tasks = taskRepository.mockGetAllTasks()
        assertTrue(tasks.isNotEmpty())
    }

    @Test
    fun `mockGetTasksByStatus should filter by status`() {
        val tasks = taskRepository.mockGetTasksByStatus("OPEN")
        assertTrue(tasks.all { it.status == "OPEN" })
    }

    @Test
    fun `mockGetTasksByType should filter by type`() {
        val tasks = taskRepository.mockGetTasksByType("GAME")
        assertTrue(tasks.all { it.type == "GAME" })
    }

    @Test
    fun `mockAddTask should add task to database`() {
        val newTask = Task(
            taskId = 999L,
            title = "测试任务",
            description = "这是一个测试任务",
            type = "STUDY",
            publisherId = 1L,
            publisherName = "用户",
            needPeople = 2,
            currentPeople = 0,
            rewardPoints = 50,
            status = "OPEN",
            deadline = System.currentTimeMillis() + 3600000,
            createdAt = System.currentTimeMillis()
        )

        taskRepository.mockAddTask(newTask)
        val tasks = taskRepository.mockGetAllTasks()
        assertTrue(tasks.any { it.taskId == 999L })
    }

    // ===== ç½ç»APIæµè¯ - æªç»å½åºï¿?=====

    @Test
    fun `createTask should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var successCalled = false
        var errorCalled = false
        var errorMessage: String? = null

        taskRepository.createTask(
            mockContext,
            TaskCreateRequest("æµè¯", "æè¿°", "STUDY", 2, 50, "2024-12-31"),
            { success, taskId, error ->
                if (!success) {
                    errorCalled = true
                    errorMessage = error
                }
                successCalled = true
            }
        )

        assertTrue(successCalled)
        assertTrue(errorCalled)
        assertEquals("用户未登录", errorMessage)
    }

    @Test
    fun `getAllTasks should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var successCalled = false
        var errorCalled = false
        var errorMessage: String? = null

        taskRepository.getAllTasks(mockContext, 0, 20) { success, tasks, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
            successCalled = true
        }

        assertTrue(successCalled)
        assertTrue(errorCalled)
        assertEquals("用户未登录", errorMessage)
    }

    @Test
    fun `grabTask should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var successCalled = false
        var errorCalled = false

        taskRepository.grabTask(mockContext, GrabTaskRequest(1L)) { success, task, error ->
            if (!success) {
                errorCalled = true
            }
            successCalled = true
        }

        assertTrue(successCalled)
        assertTrue(errorCalled)
    }

    @Test
    fun `finishTask should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var successCalled = false
        var errorCalled = false

        taskRepository.finishTask(mockContext, 1L) { success, error ->
            if (!success) {
                errorCalled = true
            }
            successCalled = true
        }

        assertTrue(successCalled)
        assertTrue(errorCalled)
    }

    @Test
    fun `cancelTask should fail when user not logged in`() {
        every { AuthTokenStore.authorizationHeader(mockContext) } returns null

        var successCalled = false
        var errorCalled = false

        taskRepository.cancelTask(mockContext, 1L) { success, error ->
            if (!success) {
                errorCalled = true
            }
            successCalled = true
        }

        assertTrue(successCalled)
        assertTrue(errorCalled)
    }

    // ===== ç½ç»APIæµè¯ - æååºæ¯ =====

    @Test
    fun `createTask should succeed with valid token`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<Long>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", 123L))

        every { mockTaskApi.createTask(mockHeader, any()) } returns mockCall

        var successCalled = false
        var taskId: Long? = null

        // 注释
        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<Long>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        taskRepository.createTask(
            mockContext,
            TaskCreateRequest("æµè¯", "æè¿°", "STUDY", 2, 50, "2024-12-31"),
            { success, id, error ->
                if (success) {
                    successCalled = true
                    taskId = id
                }
            }
        )

        assertTrue(successCalled)
        assertEquals(123L, taskId)
    }

    @Test
    fun `getAllTasks should succeed with valid token`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockTasks = listOf(
            Task(
                taskId = 1L,
                title = "ä»»å¡1",
                description = "æè¿°1",
                type = "GAME",
                publisherId = 1L,
                publisherName = "ç¨æ·1",
                needPeople = 2,
                currentPeople = 1,
                rewardPoints = 50,
                status = "OPEN",
                deadline = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            )
        )

        val mockCall = mockk<Call<BaseResponse<List<Task>>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", mockTasks))

        every { mockTaskApi.listTasks(mockHeader, 0, 20) } returns mockCall

        var successCalled = false
        var tasks: List<Task>? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<List<Task>>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        taskRepository.getAllTasks(mockContext, 0, 20) { success, taskList, error ->
            if (success) {
                successCalled = true
                tasks = taskList
            }
        }

        assertTrue(successCalled)
        assertNotNull(tasks)
        assertEquals(1, tasks?.size)
    }

    @Test
    fun `getTaskParticipants should succeed with valid token`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockParticipants = listOf(
            UserInfo(
                id = 1L,
                username = "参与者",
                phone = "13800138000",
                points = 100,
                admin = false,
                creditScore = 80,
                level = 2,
                avatarUrl = "",
                createdAt = "2024-01-01"
            )
        )

        val mockCall = mockk<Call<BaseResponse<List<UserInfo>>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", mockParticipants))

        every { mockTaskApi.participants(mockHeader, 1L) } returns mockCall

        var successCalled = false
        var participants: List<UserInfo>? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<List<UserInfo>>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        taskRepository.getTaskParticipants(mockContext, 1L) { success, userList, error ->
            if (success) {
                successCalled = true
                participants = userList
            }
        }

        assertTrue(successCalled)
        assertNotNull(participants)
        assertEquals(1, participants?.size)
    }

    @Test
    fun `stats should succeed`() {
        val mockStatResp = HomeStatResp(100, 50, 200)
        val mockCall = mockk<Call<BaseResponse<HomeStatResp>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", mockStatResp))

        every { mockTaskApi.stats() } returns mockCall

        var successCalled = false
        var stats: HomeStatResp? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<HomeStatResp>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        taskRepository.stats(mockContext) { success, statResp, error ->
            if (success) {
                successCalled = true
                stats = statResp
            }
        }

        assertTrue(successCalled)
        assertNotNull(stats)
        assertEquals(100, stats?.inProgress)
        assertEquals(50, stats?.finished)
        assertEquals(200, stats?.users)
    }

    // ===== ç½ç»APIæµè¯ - å¤±è´¥åºæ¯ =====

    @Test
    fun `createTask should fail with error response`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<Long>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(400, "参数错误", 0L))

        every { mockTaskApi.createTask(mockHeader, any()) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<Long>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        taskRepository.createTask(
            mockContext,
            TaskCreateRequest("æµè¯", "æè¿°", "STUDY", 2, 50, "2024-12-31"),
            { success, taskId, error ->
                if (!success) {
                    errorCalled = true
                    errorMessage = error
                }
            }
        )

        assertTrue(errorCalled)
        assertEquals("参数错误", errorMessage)
    }

    @Test
    fun `getAllTasks should fail with network error`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockCall = mockk<Call<BaseResponse<List<Task>>>>(relaxed = true)

        every { mockTaskApi.listTasks(mockHeader, 0, 20) } returns mockCall

        var errorCalled = false
        var errorMessage: String? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<List<Task>>>>()
            callback.onFailure(mockCall, Throwable("网络错误"))
        }

        taskRepository.getAllTasks(mockContext, 0, 20) { success, tasks, error ->
            if (!success) {
                errorCalled = true
                errorMessage = error
            }
        }

        assertTrue(errorCalled)
        assertEquals("网络错误", errorMessage)
    }

    // ===== 过滤测试 =====

    @Test
    fun `getTasksByStatus should filter tasks correctly`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockTasks = listOf(
            Task(
                taskId = 1L,
                title = "ä»»å¡1",
                description = "æè¿°1",
                type = "GAME",
                publisherId = 1L,
                publisherName = "ç¨æ·1",
                needPeople = 2,
                currentPeople = 1,
                rewardPoints = 50,
                status = "OPEN",
                deadline = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            ),
            Task(
                taskId = 2L,
                title = "ä»»å¡2",
                description = "æè¿°2",
                type = "STUDY",
                publisherId = 2L,
                publisherName = "ç¨æ·2",
                needPeople = 1,
                currentPeople = 1,
                rewardPoints = 30,
                status = "FINISHED",
                deadline = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            )
        )

        val mockCall = mockk<Call<BaseResponse<List<Task>>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", mockTasks))

        every { mockTaskApi.listTasks(mockHeader, 0, 20) } returns mockCall

        var filteredTasks: List<Task>? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<List<Task>>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        taskRepository.getTasksByStatus(mockContext, "OPEN", 0, 20) { success, tasks, error ->
            if (success) {
                filteredTasks = tasks
            }
        }

        assertNotNull(filteredTasks)
        assertEquals(1, filteredTasks?.size)
        assertEquals("OPEN", filteredTasks?.first()?.status)
    }

    @Test
    fun `getTasksByType should filter tasks correctly`() {
        val mockHeader = "Bearer test-token"
        every { AuthTokenStore.authorizationHeader(mockContext) } returns mockHeader

        val mockTasks = listOf(
            Task(
                taskId = 1L,
                title = "ä»»å¡1",
                description = "æè¿°1",
                type = "GAME",
                publisherId = 1L,
                publisherName = "ç¨æ·1",
                needPeople = 2,
                currentPeople = 1,
                rewardPoints = 50,
                status = "OPEN",
                deadline = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            ),
            Task(
                taskId = 2L,
                title = "ä»»å¡2",
                description = "æè¿°2",
                type = "STUDY",
                publisherId = 2L,
                publisherName = "ç¨æ·2",
                needPeople = 1,
                currentPeople = 1,
                rewardPoints = 30,
                status = "OPEN",
                deadline = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            )
        )

        val mockCall = mockk<Call<BaseResponse<List<Task>>>>(relaxed = true)
        val mockResponse = Response.success(BaseResponse(200, "success", mockTasks))

        every { mockTaskApi.listTasks(mockHeader, 0, 20) } returns mockCall

        var filteredTasks: List<Task>? = null

        every { mockCall.enqueue(any()) } answers {
            val callback = firstArg<Callback<BaseResponse<List<Task>>>>()
            callback.onResponse(mockCall, mockResponse)
        }

        taskRepository.getTasksByType(mockContext, "GAME", 0, 20) { success, tasks, error ->
            if (success) {
                filteredTasks = tasks
            }
        }

        assertNotNull(filteredTasks)
        assertEquals(1, filteredTasks?.size)
        assertEquals("GAME", filteredTasks?.first()?.type)
    }
}

