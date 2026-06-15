package com.example.campustask

import com.example.campustask.adapter.ShopAdapter
import com.example.campustask.model.ShopItem
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ShopAdapterTest {

    private lateinit var shopAdapter: ShopAdapter
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

        shopAdapter = ShopAdapter(mockShopItems, true, null)
    }

    // ===== 数据测试 =====

    @Test
    fun `adapter should have correct item count`() {
        assertEquals(2, shopAdapter.itemCount)
    }

    @Test
    fun `adapter should handle empty list`() {
        val emptyAdapter = ShopAdapter(emptyList(), true, null)
        assertEquals(0, emptyAdapter.itemCount)
    }

    @Test
    fun `adapter should handle single item`() {
        val singleAdapter = ShopAdapter(mockShopItems.take(1), true, null)
        assertEquals(1, singleAdapter.itemCount)
    }

    // ===== ShopItem数据验证 =====

    @Test
    fun `ShopItem should have correct data`() {
        val item = mockShopItems.first()

        assertEquals(1L, item.id)
        assertEquals("星巴克咖啡券", item.name)
        assertEquals(200, item.price)
        assertEquals(100, item.stock)
        assertEquals("coffee", item.imageRes)
    }

    @Test
    fun `ShopItem price should be formatted correctly`() {
        val item = mockShopItems.first()
        val priceText = "${item.price}积分"
        assertEquals("200积分", priceText)
    }

    @Test
    fun `ShopItem should handle zero price`() {
        val item = ShopItem(
            id = 1L,
            name = "免费商品",
            price = 0,
            stock = 10,
            description = "免费商品",
            imageRes = "free"
        )

        assertEquals(0, item.price)
        val priceText = "${item.price}积分"
        assertEquals("0积分", priceText)
    }

    @Test
    fun `ShopItem should handle zero stock`() {
        val item = ShopItem(
            id = 1L,
            name = "无库存商品",
            price = 100,
            stock = 0,
            description = "无库存商品",
            imageRes = "empty"
        )

        assertEquals(0, item.stock)
    }
}
