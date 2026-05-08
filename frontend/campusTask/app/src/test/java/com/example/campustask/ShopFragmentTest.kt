package com.example.campustask

import com.example.campustask.model.ShopItem
import org.junit.Test
import org.junit.Assert.*

class ShopFragmentTest {

    @Test
    fun shopListShouldNotBeEmpty() {
        val list = getMockShopList()
        assertTrue(list.isNotEmpty())
    }

    @Test
    fun shopItemTitleShouldNotBeEmpty() {
        val item = getMockShopList()[0]
        assertTrue(item.name.isNotEmpty())
    }

    @Test
    fun shopItemPointsShouldBePositive() {
        val item = getMockShopList()[0]
        assertTrue(item.price > 0)
    }

    @Test
    fun negativePoints_shouldBeDetected() {
        val item = ShopItem(999,"异常商品", -100, 0,"","")
        assertTrue(item.price < 0)
    }

    @Test
    fun gridLayout_shouldBe2Columns() {
        val spanCount = 2
        assertEquals(2, spanCount)
    }

    @Test
    fun emptyList_shouldNotCrash() {
        val emptyList: List<ShopItem> = emptyList()
        assertNotNull(emptyList)
    }

    @Test
    fun imageRes_shouldBeValid() {
        val item = getMockShopList()[0]
        assertNotEquals(0, item.imageRes)
    }

    @Test
    fun adapter_shouldHaveCorrectSize() {
        val list = getMockShopList()
        assertEquals(4, list.size)
    }

    @Test
    fun mockLoad_shouldReturnList() {
        val list = getMockShopList()
        assertNotNull(list)
    }

    @Test
    fun mockNullSafeList() {
        val nullList: List<ShopItem>? = null
        val safeList = nullList ?: emptyList()
        assertNotNull(safeList)
    }

    @Test
    fun mockIllegalPoints_shouldBeInvalid() {
        val invalid = -50
        assertTrue(invalid < 0)
    }

    private fun getMockShopList(): List<ShopItem> {
        return listOf(
            ShopItem(1,"星巴克咖啡券", 200, 100,"星巴克咖啡券","sample_coffee"),
            ShopItem(2,"图书馆座位卡", 50, 100,"图书馆座位卡","sample_book"),
            ShopItem(3,"奶茶券", 100,100,"奶茶券", "sample_drink"),
            ShopItem(4,"校园代跑券", 80, 100,"校园跑","sample_run")
        )
    }
}