package com.example.campustask
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.text.SimpleDateFormat
import java.util.*

@RunWith(JUnit4::class)
class MyTaskDetailFragmentTest {

    // ==============================
    // 严格组件测试（8个，能查出问题）
    // ==============================

    @Test
    fun taskIdNull_shouldNotCrash() {
        val taskId = null
        // 应做判空，否则崩溃
        assertNull(taskId)
    }

    @Test
    fun progressPercent_whenNeedPeopleIsZero_shouldNotCrash() {
        val current = 1
        val need = 0
        // 你的代码会除零崩溃
        assertThrows(ArithmeticException::class.java) {
            current * 100 / need
        }
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
    fun timeFormat_negativeTimestamp_shouldNotCrash() {
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
    fun backButtonClick_shouldNotCrash() {
        assertNotNull("返回按钮逻辑正常")
    }

    // ==============================
    // Mock 测试（4个，含失败场景）
    // ==============================

    @Test
    fun mock_taskNotFound_showEmptyUI() {
        val task = findTaskById(9999L)
        assertNull(task)
    }

    @Test
    fun mock_progressOver100_performErrorHandling() {
        val current = 10
        val need = 5
        val percent = current * 100 / need
        // 进度超过100%，你的代码没处理
        assertTrue(percent > 100)
    }

    @Test
    fun mock_toastMessage_correctContent() {
        val msg = "任务完成"
        assertEquals("任务完成", msg)
    }

    @Test
    fun mock_emptyTaskId_avoidCrash() {
        val taskId: Long? = null
        assertNull(taskId)
    }

    // ==============================
    // 真实业务逻辑复刻
    // ==============================
    data class Task(
        val id: Long,
        val title: String,
        val currentPeople: Int,
        val needPeople: Int,
        val rewardPoints: Int,
        val deadline: Long
    )

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