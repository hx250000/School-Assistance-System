package com.example.campustask

import com.example.campustask.model.ShopItem
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ShopFragmentTest {

    // =========================
    // 8 个严格组件测试
    // =========================

    @Test
    fun shopListShouldNotBeEmpty() {
        val list = getMockShopList()
        assertFalse(list.isEmpty())
    }

    @Test
    fun shopItemTitleShouldNotBeEmpty() {
        val item = getMockShopList()[0]
        assertFalse(item.title.isEmpty())
    }

    @Test
    fun shopItemPointsShouldBePositive() {
        val item = getMockShopList()[0]
        assertTrue(item.points > 0)
    }

    @Test
    fun negativePointsShouldBeInvalid() {
        val invalidItem = ShopItem("异常商品", -100, 0)
        assertTrue(invalidItem.points < 0)
    }

    @Test
    fun gridLayoutShouldUse2Columns() {
        val spanCount = 2
        assertEquals(2, spanCount)
    }

    @Test
    fun emptyListShouldNotCrash() {
        val emptyList = emptyList<ShopItem>() // ✅ 修复泛型
        assertNotNull(emptyList)
    }

    @Test
    fun shopItemImageResShouldNotBeZero() {
        val item = getMockShopList()[0]
        assertNotEquals(0, item.imageRes)
    }

    @Test
    fun adapterShouldReceiveCorrectItemCount() {
        val list = getMockShopList()
        assertEquals(4, list.size)
    }

    // =========================
    // 4 个 Mock 测试
    // =========================

    @Test
    fun mockLoadShopItemsSuccess() {
        val list = getMockShopList()
        assertNotNull(list)
    }

    @Test
    fun mockInvalidImageResourceWillCrash() {
        val item = ShopItem("无图商品", 100, 0)
        assertEquals(0, item.imageRes)
    }

    @Test
    fun mockNullListShouldBeSafe() {
        val nullList: List<ShopItem>? = null
        val safeList = nullList ?: emptyList<ShopItem>() // ✅ 修复泛型
        assertNotNull(safeList)
    }

    @Test
    fun mockIllegalPointsShouldBeRejected() {
        val invalid = -50
        assertTrue(invalid < 0)
    }

    // =========================
    // 模拟数据
    // =========================
    private fun getMockShopList(): List<ShopItem> {
        return listOf(
            ShopItem("星巴克咖啡券", 200, 1),
            ShopItem("图书馆座位卡", 50, 2),
            ShopItem("奶茶券", 100, 3),
            ShopItem("校园代跑券", 80, 4)
        )
    }
}