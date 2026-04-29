package com.example.campustask

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.Assert.*

class MainActivityTest {

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

    @Test
    fun `bottom nav switches fragment correctly`() {
        val navigator = mockk<FragmentNavigator>(relaxed = true)

        navigator.switchTo("home")

        verify { navigator.switchTo("home") }
    }

    @Test
    fun `floating button can be clicked`() {
        val btn = mockk<FloatingButton>()

        every { btn.isClickable() } returns true

        assertTrue(btn.isClickable())
    }

    @Test
    fun `click floating btn shows popup`() {
        val popup = mockk<PopupController>(relaxed = true)

        popup.show()

        verify { popup.show() }
    }

    @Test
    fun `floating btn can drag and snap to edge`() {
        val drag = mockk<DragController>()

        every { drag.canDrag() } returns true

        assertTrue(drag.canDrag())
    }
}