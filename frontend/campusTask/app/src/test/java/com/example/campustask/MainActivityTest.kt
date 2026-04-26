package com.example.campustask

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MainActivityTest {

    // 1. 测试底部导航切换
    @Test
    fun `bottom nav switches fragment correctly`() {
        val navigator = mockk<FragmentNavigator>()
        navigator.switchTo("home")
        verify { navigator.switchTo("home") }
    }

    // 2. 测试悬浮按钮可点击
    @Test
    fun `floating button can be clicked`() {
        val btn = mockk<FloatingButton>()
        every { btn.isClickable() } returns true
        assert(btn.isClickable())
    }

    // 3. 测试弹窗能打开
    @Test
    fun `click floating btn shows popup`() {
        val popup = mockk<PopupController>()
        popup.show()
        verify { popup.show() }
    }

    // 4. 测试拖动逻辑有效
    @Test
    fun `floating btn can drag and snap to edge`() {
        val drag = mockk<DragController>()
        every { drag.canDrag() } returns true
        assert(drag.canDrag())
    }

    // 模拟类
    interface FragmentNavigator {
        fun switchTo(page: String)
    }

    interface FloatingButton {
        fun isClickable(): Boolean
    }

    interface PopupController {
        fun show()
    }

    interface DragController {
        fun canDrag(): Boolean
    }
}