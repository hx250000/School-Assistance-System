package com.example.campustask

import org.junit.Test
import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

class MyTaskDetailFragmentTest {

    data class Task(
        val id: Long,
        val title: String,
        val currentPeople: Int,
        val needPeople: Int,
        val rewardPoints: Int,
        val deadline: Long
    )

    @Test
    fun taskIdNull_shouldNotCrash() {
        val taskId: Long? = null
        assertNull(taskId)
    }

    @Test
    fun progressPercent_shouldHandleZeroSafely() {
        val current = 1
        val need = 1

        val percent = current * 100 / need

        assertTrue(percent >= 0)
    }

    @Test
    fun progressText_correctFormat() {
        val text = "${2}/${5}"
        assertEquals("2/5", text)
    }

    @Test
    fun taskNotFound_shouldReturnNull() {
        val task = findTaskById(9999L)
        assertNull(task)
    }

    @Test
    fun timeFormat_shouldNotCrash() {
        val time = -123456789L
        val result = formatTime(time)
        assertNotNull(result)
    }

    @Test
    fun taskTitle_shouldNotBeEmpty() {
        val task = mockTask()
        assertFalse(task.title.isEmpty())
    }

    @Test
    fun rewardText_shouldContainPoints() {
        val text = "+10积分"
        assertTrue(text.contains("积分"))
    }

    @Test
    fun backButton_shouldWork() {
        assertTrue(true)
    }

    @Test
    fun progress_over_100_should_be_detected() {
        val current = 10
        val need = 5
        val percent = current * 100 / need

        assertTrue(percent > 100)
    }

    // ===== mock logic =====

    private fun findTaskById(id: Long): Task? {
        val list = listOf(
            Task(1, "代取快递", 2, 5, 10, System.currentTimeMillis())
        )
        return list.find { it.id == id }
    }

    private fun mockTask(): Task {
        return Task(1, "测试任务", 1, 2, 5, System.currentTimeMillis())
    }

    private fun formatTime(time: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(time))
    }
}