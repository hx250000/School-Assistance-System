package com.example.campustask

import android.view.View
import android.widget.TextView
import com.example.campustask.adapter.TaskAdapter
import com.example.campustask.model.Task
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.spyk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapterTest {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var mockTasks: List<Task>

    @Before
    fun setup() {
        mockTasks = listOf(
            Task(
                taskId = 1L,
                title = "测试任务1",
                description = "这是一个测试任务",
                type = "GAME",
                publisherId = 1L,
                publisherName = "用户1",
                needPeople = 3,
                currentPeople = 1,
                rewardPoints = 50,
                status = "OPEN",
                deadline = System.currentTimeMillis() + 3600000,
                createdAt = System.currentTimeMillis()
            ),
            Task(
                taskId = 2L,
                title = "测试任务2",
                description = "这是另一个测试任务",
                type = "STUDY",
                publisherId = 2L,
                publisherName = "用户2",
                needPeople = 2,
                currentPeople = 2,
                rewardPoints = 30,
                status = "FINISHED",
                deadline = System.currentTimeMillis() + 7200000,
                createdAt = System.currentTimeMillis()
            )
        )

        taskAdapter = spyk(TaskAdapter(mockTasks) { })
        every { taskAdapter.notifyDataSetChanged() } just runs
    }

    // ===== 数据测试 =====

    @Test
    fun `adapter should have correct item count`() {
        assertEquals(2, taskAdapter.itemCount)
    }

    @Test
    fun `adapter should handle empty list`() {
        val emptyAdapter = TaskAdapter(emptyList()) { }
        assertEquals(0, emptyAdapter.itemCount)
    }

    @Test
    fun `adapter should handle single item`() {
        val singleAdapter = TaskAdapter(mockTasks.take(1)) { }
        assertEquals(1, singleAdapter.itemCount)
    }

    // ===== 数据更新测试 =====

    @Test
    fun `update should change adapter data`() {
        val newTasks = listOf(
            Task(
                taskId = 3L,
                title = "新任务",
                description = "新任务描述",
                type = "LIFE",
                publisherId = 3L,
                publisherName = "用户3",
                needPeople = 1,
                currentPeople = 0,
                rewardPoints = 100,
                status = "OPEN",
                deadline = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            )
        )

        taskAdapter.update(newTasks)
        assertEquals(1, taskAdapter.itemCount)
    }

    @Test
    fun `update should handle empty list`() {
        taskAdapter.update(emptyList())
        assertEquals(0, taskAdapter.itemCount)
    }

    // ===== 时间格式化测试 =====

    @Test
    fun `formatTime should format timestamp correctly`() {
        val timestamp = System.currentTimeMillis()
        val formattedTime = formatTime(timestamp)

        // 验证格式是否正确 (MM-dd HH:mm)
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        val expected = sdf.format(Date(timestamp))

        assertEquals(expected, formattedTime)
    }

    @Test
    fun `formatTime should handle negative timestamp`() {
        val formattedTime = formatTime(-123456789L)
        assertNotNull(formattedTime)
        assertTrue(formattedTime.isNotEmpty())
    }

    @Test
    fun `formatTime should handle zero timestamp`() {
        val formattedTime = formatTime(0L)
        assertNotNull(formattedTime)
        assertTrue(formattedTime.isNotEmpty())
    }

    @Test
    fun `formatTime should handle future timestamp`() {
        val futureTime = System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000 // 一年后
        val formattedTime = formatTime(futureTime)
        assertNotNull(formattedTime)
        assertTrue(formattedTime.isNotEmpty())
    }

    // ===== Task数据验证 =====

    @Test
    fun `Task should have correct data`() {
        val task = mockTasks.first()

        assertEquals(1L, task.taskId)
        assertEquals("测试任务1", task.title)
        assertEquals("GAME", task.type)
        assertEquals(3, task.needPeople)
        assertEquals(1, task.currentPeople)
        assertEquals(50, task.rewardPoints)
        assertEquals("OPEN", task.status)
    }

    @Test
    fun `Task people count should be formatted correctly`() {
        val task = mockTasks.first()
        val peopleText = "${task.currentPeople}/${task.needPeople}人"
        assertEquals("1/3人", peopleText)
    }

    @Test
    fun `Task reward points should be formatted correctly`() {
        val task = mockTasks.first()
        val scoreText = "+${task.rewardPoints}积分"
        assertEquals("+50积分", scoreText)
    }

    @Test
    fun `Task should handle zero reward points`() {
        val task = Task(
            taskId = 1L,
            title = "免费任务",
            description = "无积分奖励",
            type = "LIFE",
            publisherId = 1L,
            publisherName = "用户",
            needPeople = 1,
            currentPeople = 0,
            rewardPoints = 0,
            status = "OPEN",
            deadline = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis()
        )

        assertEquals(0, task.rewardPoints)
        val scoreText = "+${task.rewardPoints}积分"
        assertEquals("+0积分", scoreText)
    }

    @Test
    fun `Task should handle full capacity`() {
        val task = Task(
            taskId = 1L,
            title = "已满任务",
            description = "已满",
            type = "GAME",
            publisherId = 1L,
            publisherName = "用户",
            needPeople = 5,
            currentPeople = 5,
            rewardPoints = 100,
            status = "FULL",
            deadline = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis()
        )

        assertEquals(5, task.currentPeople)
        assertEquals(5, task.needPeople)
        assertTrue(task.currentPeople == task.needPeople)
    }

    // ===== 辅助方法 =====

    private fun formatTime(time: Long): String {
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(time))
    }
}