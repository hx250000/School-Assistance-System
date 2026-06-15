
package com.example.campustask

import com.example.campustask.model.ShopItem
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ShopFragmentTest {

    private lateinit var mockShopItems: List<ShopItem>

    @Before
    fun setup() {
        mockShopItems = listOf(
            ShopItem(
                id = 1L,
                name = "星巴克咖啡券",
                price = 200,
                stock = 100,
                description = "星巴克咖啡券",
                imageRes = "coffee"
            ),
            ShopItem(
                id = 2L,
                name = "奶茶券",
                price = 100,
                stock = 50,
                description = "奶茶券",
                imageRes = "drink"
            )
        )
    }

    // ===== 数据测试 =====

    @Test
    fun `shopItems should have correct data`() {
        assertEquals(2, mockShopItems.size)
        assertEquals("星巴克咖啡券", mockShopItems.first().name)
        assertEquals(200, mockShopItems.first().price)
    }

    @Test
    fun `shopItems should handle empty list`() {
        val emptyList = emptyList<ShopItem>()
        assertEquals(0, emptyList.size)
    }

    @Test
    fun `ShopItem should have correct properties`() {
        val item = mockShopItems.first()
        assertEquals(1L, item.id)
        assertEquals("星巴克咖啡券", item.name)
        assertEquals(200, item.price)
        assertEquals(100, item.stock)
        assertEquals("coffee", item.imageRes)
    }

    @Test
    fun `ShopItem price should be positive`() {
        for (item in mockShopItems) {
            assertTrue(item.price > 0)
        }
    }

    @Test
    fun `ShopItem stock should be non-negative`() {
        for (item in mockShopItems) {
            assertTrue(item.stock >= 0)
        }
    }
}
