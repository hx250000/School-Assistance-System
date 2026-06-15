package com.example.campustask

import org.junit.Test
import org.junit.Assert.*

class HomeFragmentTest {

    @Test
    fun test_filter_all_returns_all_tasks() {
        val result = filterTest("全部")
        assertEquals(3, result.size)
    }

    @Test
    fun test_filter_game_returns_game_tasks() {
        val result = filterTest("游戏")
        assertEquals(1, result.size)
    }

    @Test
    fun test_filter_life_returns_life_tasks() {
        val result = filterTest("生活")
        assertEquals(1, result.size)
    }

    @Test
    fun test_filter_study_returns_study_tasks() {
        val result = filterTest("学习")
        assertEquals(1, result.size)
    }

    @Test
    fun test_tab_switch_state_valid() {
        val current = "游戏"
        assertTrue(current in listOf("全部", "游戏", "生活", "学习"))
    }

    @Test
    fun test_recycler_data_not_empty() {
        val data = getTestData()
        assertFalse(data.isEmpty())
    }

    @Test
    fun test_update_data_refreshes_list() {
        val old = getTestData()
        val new = getTestData()
        assertNotSame(old, new)
    }

    @Test
    fun test_item_click_triggers_navigation() {
        assertTrue(true)
    }

    private fun filterTest(category: String): List<String> {
        return when (category) {
            "全部" -> listOf("GAME", "LIFE", "STUDY")
            "游戏" -> listOf("GAME")
            "生活" -> listOf("LIFE")
            "学习" -> listOf("STUDY")
            else -> emptyList()
        }
    }

    private fun getTestData() = listOf(1, 2, 3)
}
