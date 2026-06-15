package com.example.campustask

import com.example.campustask.adapter.AchievementAdapter
import com.example.campustask.model.Achievement
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AchievementAdapterTest {

    private lateinit var achievementAdapter: AchievementAdapter
    private lateinit var mockAchievements: List<Achievement>

    @Before
    fun setup() {
        mockAchievements = listOf(
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
            ),
            Achievement(
                id = 3L,
                title = "积分达人",
                description = "累计获得100积分",
                currentProgress = 50,
                totalProgress = 100,
                iconUrl = "coin",
                isUnlocked = false,
                unlockedAt = null
            )
        )

        achievementAdapter = AchievementAdapter(mockAchievements)
    }

    @Test
    fun `adapter should have correct item count`() {
        assertEquals(3, achievementAdapter.itemCount)
    }

    @Test
    fun `adapter should handle empty list`() {
        val emptyAdapter = AchievementAdapter(emptyList())
        assertEquals(0, emptyAdapter.itemCount)
    }

    @Test
    fun `adapter should handle single item`() {
        val singleAdapter = AchievementAdapter(mockAchievements.take(1))
        assertEquals(1, singleAdapter.itemCount)
    }

    @Test
    fun `Achievement should have correct data`() {
        val achievement = mockAchievements.first()

        assertEquals(1L, achievement.id)
        assertEquals("初学者", achievement.title)
        assertEquals("完成第一个任务", achievement.description)
        assertEquals(1, achievement.currentProgress)
        assertEquals(1, achievement.totalProgress)
        assertEquals("star", achievement.iconUrl)
        assertTrue(achievement.isUnlocked)
        assertNotNull(achievement.unlockedAt)
    }

    @Test
    fun `Achievement progress should be formatted correctly`() {
        val achievement = mockAchievements[1]
        val progressText = "${achievement.currentProgress}/${achievement.totalProgress}"
        assertEquals("5/10", progressText)
    }

    @Test
    fun `Achievement should handle completed progress`() {
        val achievement = mockAchievements.first()
        assertEquals(achievement.currentProgress, achievement.totalProgress)
        assertTrue(achievement.isUnlocked)
    }

    @Test
    fun `Achievement should handle incomplete progress`() {
        val achievement = mockAchievements[1]
        assertTrue(achievement.currentProgress < achievement.totalProgress)
        assertFalse(achievement.isUnlocked)
    }

    @Test
    fun `Achievement should handle zero progress`() {
        val zeroAchievement = Achievement(
            id = 99L,
            title = "新成就",
            description = "尚未开始",
            currentProgress = 0,
            totalProgress = 10,
            iconUrl = "new",
            isUnlocked = false,
            unlockedAt = null
        )

        assertEquals(0, zeroAchievement.currentProgress)
        assertFalse(zeroAchievement.isUnlocked)
    }

    @Test
    fun `Achievement should handle full progress`() {
        val fullAchievement = Achievement(
            id = 99L,
            title = "已完成",
            description = "全部完成",
            currentProgress = 100,
            totalProgress = 100,
            iconUrl = "complete",
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis()
        )

        assertEquals(100, fullAchievement.currentProgress)
        assertEquals(100, fullAchievement.totalProgress)
        assertTrue(fullAchievement.isUnlocked)
    }

    @Test
    fun `Achievement progress percentage should be calculated correctly`() {
        val achievement = mockAchievements[1]
        val percentage = achievement.currentProgress * 100 / achievement.totalProgress
        assertEquals(50, percentage)
    }

    @Test
    fun `Achievement progress percentage should be 100 when completed`() {
        val achievement = mockAchievements.first()
        val percentage = achievement.currentProgress * 100 / achievement.totalProgress
        assertEquals(100, percentage)
    }

    @Test
    fun `Achievement progress percentage should be 0 when not started`() {
        val achievement = Achievement(
            id = 99L,
            title = "未开始",
            description = "尚未开始",
            currentProgress = 0,
            totalProgress = 10,
            iconUrl = "new",
            isUnlocked = false,
            unlockedAt = null
        )

        val percentage = achievement.currentProgress * 100 / achievement.totalProgress
        assertEquals(0, percentage)
    }

    @Test
    fun `achievements list should not be empty`() {
        assertTrue(mockAchievements.isNotEmpty())
    }

    @Test
    fun `achievements should have unique ids`() {
        val ids = mockAchievements.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `achievements should have non-empty titles`() {
        assertTrue(mockAchievements.all { it.title.isNotEmpty() })
    }

    @Test
    fun `achievements should have non-empty descriptions`() {
        assertTrue(mockAchievements.all { it.description.isNotEmpty() })
    }

    @Test
    fun `achievements should have valid progress`() {
        assertTrue(mockAchievements.all { it.currentProgress >= 0 })
        assertTrue(mockAchievements.all { it.totalProgress > 0 })
        assertTrue(mockAchievements.all { it.currentProgress <= it.totalProgress })
    }

    @Test
    fun `achievements should include both unlocked and locked`() {
        val hasUnlocked = mockAchievements.any { it.isUnlocked }
        val hasLocked = mockAchievements.any { !it.isUnlocked }
        assertTrue(hasUnlocked)
        assertTrue(hasLocked)
    }

    @Test
    fun `adapter should handle large progress values`() {
        val largeAchievement = Achievement(
            id = 99L,
            title = "大目标",
            description = "完成1000个任务",
            currentProgress = 500,
            totalProgress = 1000,
            iconUrl = "big",
            isUnlocked = false,
            unlockedAt = null
        )

        val adapter = AchievementAdapter(listOf(largeAchievement))
        assertEquals(1, adapter.itemCount)
        assertEquals(500, largeAchievement.currentProgress)
        assertEquals(1000, largeAchievement.totalProgress)
    }

    @Test
    fun `adapter should handle multiple achievements`() {
        val manyAchievements = List(10) { index ->
            Achievement(
                id = index.toLong(),
                title = "成就$index",
                description = "描述$index",
                currentProgress = index,
                totalProgress = 10,
                iconUrl = "icon$index",
                isUnlocked = index == 10,
                unlockedAt = if (index == 10) System.currentTimeMillis() else null
            )
        }

        val adapter = AchievementAdapter(manyAchievements)
        assertEquals(10, adapter.itemCount)
    }

    @Test
    fun `Achievement should handle unlockedAt timestamp`() {
        val unlockedAchievement = mockAchievements.first()
        assertNotNull(unlockedAchievement.unlockedAt)
        assertTrue(unlockedAchievement.unlockedAt ?: 0 > 0)
    }

    @Test
    fun `Achievement should handle null unlockedAt`() {
        val lockedAchievement = mockAchievements[1]
        assertNull(lockedAchievement.unlockedAt)
        assertFalse(lockedAchievement.isUnlocked)
    }
}
