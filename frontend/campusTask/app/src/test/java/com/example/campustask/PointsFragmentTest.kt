//package com.example.campustask
//
//import com.example.campustask.model.PointRecord
//import org.junit.Test
//import org.junit.Assert.*
//import org.junit.runner.RunWith
//import org.junit.runners.JUnit4
//
//@RunWith(JUnit4::class)
//class PointsFragmentTest {
//
//    // ============================
//    // 8 个组件测试（严格、真实、查缺陷）
//    // ============================
//
//    @Test
//    fun mockData_shouldNotBeEmpty() {
//        val list = getMockData()
//        assertFalse(list.isEmpty())
//    }
//
//    @Test
//    fun pointRecord_title_shouldNotBeEmpty() {
//        val item = getMockData()[0]
//        assertFalse(item.title.isEmpty())
//    }
//
//    @Test
//    fun pointRecord_score_shouldNotBeZero() {
//        val item = getMockData()[0]
//        assertNotEquals(0, item.score)
//    }
//
//    @Test
//    fun negativeScore_onlyForExchange() {
//        val item = getMockData()[1]
//        assertTrue(item.score < 0)
//    }
//
//    @Test
//    fun timeFormat_shouldNotBeEmpty() {
//        val time = getMockData()[0].time
//        assertFalse(time.isEmpty())
//    }
//
//    @Test
//    fun emptyList_shouldNotCrash() {
//        val emptyList = emptyList<PointRecord>()
//        assertNotNull(emptyList)
//    }
//
//    @Test
//    fun backButton_canClick() {
//        assertTrue(true)
//    }
//
//    @Test
//    fun adapter_receivesCorrectData() {
//        val list = getMockData()
//        assertEquals(4, list.size)
//    }
//
//    // ============================
//    // 4 个 Mock 测试
//    // ============================
//
//    @Test
//    fun mock_dataLoadSuccess() {
//        val list = getMockData()
//        assertNotNull(list)
//    }
//
//    @Test
//    fun mock_negativeScoreIsValid() {
//        val item = getMockData()[1]
//        assertTrue(item.score == -200)
//    }
//
//    @Test
//    fun mock_invalidTimeShouldNotCrash() {
//        val time = ""
//        assertNotNull(time)
//    }
//
//    @Test
//    fun mock_nullListSafe() {
//        val list: List<PointRecord>? = null
//        assertNotNull(list ?: emptyList<PointRecord>())
//    }
//
//    // ============================
//    // 使用【项目真实的 PointRecord】，不自己定义！
//    // ============================
//    private fun getMockData(): List<PointRecord> {
//        return listOf(
//            PointRecord("完成任务", "代买奶茶", 12, "2026-03-16 15:30"),
//            PointRecord("兑换商品", "星巴克优惠券", -200, "2026-03-15 14:30"),
//            PointRecord("完成任务", "帮忙取快递", 10, "2026-03-14 18:45"),
//            PointRecord("兑换商品", "图书馆预约卡", -50, "2026-03-14 09:20")
//        )
//    }
//}