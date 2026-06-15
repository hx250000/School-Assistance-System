package com.example.campustask

import android.util.Log
import android.widget.Toast
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic

object TestAndroidSupport {

    fun mockAndroidLog() {
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.d(any<String>(), any<String>(), any()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
    }

    fun unmockAndroidLog() {
        unmockkStatic(Log::class)
    }

    fun mockToast() {
        mockkStatic(Toast::class)
        every { Toast.makeText(any(), any<CharSequence>(), any()) } returns mockk(relaxed = true)
    }

    fun unmockToast() {
        unmockkStatic(Toast::class)
    }
}
