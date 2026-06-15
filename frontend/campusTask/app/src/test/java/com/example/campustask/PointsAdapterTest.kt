package com.example.campustask

import com.example.campustask.adapter.PointsAdapter
import com.example.campustask.model.PointRecord
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.spyk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PointsAdapterTest {

    private lateinit var pointsAdapter: PointsAdapter
    private lateinit var mockPointRecords: List<PointRecord>

    @Before
    fun setup() {
        mockPointRecords = listOf(
            PointRecord(
                title = "完成任务",
                description = "完成第一个任务获得积分",
                changeAmount = 50,
                time = "2024-01-01 10:00"
            ),
            PointRecord(
                title = "兑换商品",
                description = "兑换星巴克咖啡券",
                changeAmount = -200,
                time = "2024-01-02 15:00"
            )
        )

        pointsAdapter = spyk(PointsAdapter(mockPointRecords))
        every { pointsAdapter.notifyDataSetChanged() } just runs
    }

    // ===== 数据测试 =====

    @Test
    fun `adapter should have correct item count`() {
        assertEquals(2, pointsAdapter.itemCount)
    }

    @Test
    fun `adapter should handle empty list`() {
        val emptyAdapter = PointsAdapter(emptyList())
        assertEquals(0, emptyAdapter.itemCount)
    }

    @Test
    fun `adapter should handle single item`() {
        val singleAdapter = PointsAdapter(mockPointRecords.take(1))
        assertEquals(1, singleAdapter.itemCount)
    }

    // ===== 数据更新测试 =====

    @Test
    fun `updateData should change adapter data`() {
        val newRecords = listOf(
            PointRecord(
                title = "新记录",
                description = "新积分记录",
                changeAmount = 100,
                time = "2024-01-03 12:00"
            )
        )

        pointsAdapter.updateData(newRecords)
        assertEquals(1, pointsAdapter.itemCount)
    }

    @Test
    fun `updateData should handle empty list`() {
        pointsAdapter.updateData(emptyList())
        assertEquals(0, pointsAdapter.itemCount)
    }

    // ===== PointRecord数据验证 =====

    @Test
    fun `PointRecord should have correct data`() {
        val record = mockPointRecords.first()

        assertEquals("完成任务", record.title)
        assertEquals("完成第一个任务获得积分", record.description)
        assertEquals(50, record.changeAmount)
        assertEquals("2024-01-01 10:00", record.time)
    }

    @Test
    fun `PointRecord positive change should be formatted correctly`() {
        val record = mockPointRecords.first()
        val scoreText = if (record.changeAmount > 0) "+${record.changeAmount}" else "${record.changeAmount}"
        assertEquals("+50", scoreText)
    }

    @Test
    fun `PointRecord negative change should be formatted correctly`() {
        val record = mockPointRecords[1]
        val scoreText = if (record.changeAmount > 0) "+${record.changeAmount}" else "${record.changeAmount}"
        assertEquals("-200", scoreText)
    }

    @Test
    fun `PointRecord should handle zero change`() {
        val record = PointRecord(
            title = "零积分",
            description = "无积分变化",
            changeAmount = 0,
            time = "2024-01-01 10:00"
        )

        assertEquals(0, record.changeAmount)
        val scoreText = if (record.changeAmount > 0) "+${record.changeAmount}" else "${record.changeAmount}"
        assertEquals("0", scoreText)
    }
}
