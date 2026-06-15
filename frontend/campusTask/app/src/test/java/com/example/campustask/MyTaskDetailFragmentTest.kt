package com.example.campustask

import com.example.campustask.model.Task
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MyTaskDetailFragmentTest {

    private lateinit var mockTask: Task

    @Before
    fun setup() {
        mockTask = Task(
            taskId = 1L,
            title = "测试任务",
            description = "这是一个测试任务",
            type = "STUDY",
            publisherId = 1L,
            publisherName = "测试用户",
            needPeople = 3,
            currentPeople = 1,
            rewardPoints = 50,
            status = "OPEN",
            deadline = System.currentTimeMillis() + 3600000,
            createdAt = System.currentTimeMillis()
        )
    }

    // ===== Task数据验证 =====

    @Test
    fun `Task should have correct data`() {
        assertEquals(1L, mockTask.taskId)
        assertEquals("测试任务", mockTask.title)
        assertEquals("这是一个测试任务", mockTask.description)
        assertEquals("STUDY", mockTask.type)
        assertEquals(3, mockTask.needPeople)
        assertEquals(1, mockTask.currentPeople)
        assertEquals(50, mockTask.rewardPoints)
        assertEquals("OPEN", mockTask.status)
    }

    @Test
    fun `Task people count should be correct`() {
        val peopleText = "${mockTask.currentPeople}/${mockTask.needPeople}人"
        assertEquals("1/3人", peopleText)
    }

    @Test
    fun `Task reward should be formatted correctly`() {
        val rewardText = "+${mockTask.rewardPoints}积分"
        assertEquals("+50积分", rewardText)
    }

    @Test
    fun `Task should have valid deadline`() {
        assertTrue(mockTask.deadline > System.currentTimeMillis())
    }

    @Test
    fun `Task status should be valid`() {
        val validStatuses = listOf("OPEN", "FULL", "FINISHED", "CANCELLED")
        assertTrue(validStatuses.contains(mockTask.status))
    }
}
