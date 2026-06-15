package com.example.campustask

import com.example.campustask.util.CategoryMapper
import org.junit.Assert.*
import org.junit.Test

class CategoryMapperTest {

    @Test
    fun `toType should convert 游戏 to GAME`() {
        assertEquals("GAME", CategoryMapper.toType("游戏"))
    }

    @Test
    fun `toType should convert 生活 to LIFE`() {
        assertEquals("LIFE", CategoryMapper.toType("生活"))
    }

    @Test
    fun `toType should convert 学习 to STUDY`() {
        assertEquals("STUDY", CategoryMapper.toType("学习"))
    }

    @Test
    fun `toType should return empty string for unknown category`() {
        assertEquals("", CategoryMapper.toType("未知"))
    }

    @Test
    fun `toType should handle empty string`() {
        assertEquals("", CategoryMapper.toType(""))
    }

    @Test
    fun `toCategory should convert GAME to 游戏`() {
        assertEquals("游戏", CategoryMapper.toCategory("GAME"))
    }

    @Test
    fun `toCategory should convert LIFE to 生活`() {
        assertEquals("生活", CategoryMapper.toCategory("LIFE"))
    }

    @Test
    fun `toCategory should convert STUDY to 学习`() {
        assertEquals("学习", CategoryMapper.toCategory("STUDY"))
    }

    @Test
    fun `toCategory should return 全部 for unknown type`() {
        assertEquals("全部", CategoryMapper.toCategory("UNKNOWN"))
    }

    @Test
    fun `toCategory should handle empty string`() {
        assertEquals("全部", CategoryMapper.toCategory(""))
    }

    @Test
    fun `toType should return consistent results`() {
        val category = "学习"
        assertEquals(CategoryMapper.toType(category), CategoryMapper.toType(category))
        assertEquals("STUDY", CategoryMapper.toType(category))
    }

    @Test
    fun `toCategory should return consistent results`() {
        val type = "GAME"
        assertEquals(CategoryMapper.toCategory(type), CategoryMapper.toCategory(type))
        assertEquals("游戏", CategoryMapper.toCategory(type))
    }
}
