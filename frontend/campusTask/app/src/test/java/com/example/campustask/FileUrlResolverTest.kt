package com.example.campustask

import com.example.campustask.BuildConfig
import com.example.campustask.utils.FileUrlResolver
import org.junit.Assert.*
import org.junit.Test

class FileUrlResolverTest {

    private val fileBaseUrl = BuildConfig.BASE_URL.removeSuffix("api/")

    @Test
    fun `resolve should return full URL for relative path`() {
        val relativePath = "uploads/shopitem/coffee.jpg"
        val result = FileUrlResolver.resolve(relativePath)

        assertTrue(result is String)
        val url = result as String
        assertTrue(url.startsWith("http"))
        assertTrue(url.contains(relativePath))
        assertEquals("${fileBaseUrl}$relativePath", url)
    }

    @Test
    fun `resolve should handle path with backslash`() {
        val relativePath = "uploads\\avatar\\user.jpg"
        val result = FileUrlResolver.resolve(relativePath)

        assertTrue(result is String)
        val url = result as String
        assertTrue(url.startsWith("http"))
    }

    @Test
    fun `resolve should return default drawable for null path`() {
        val result = FileUrlResolver.resolve(null)

        assertTrue(result is Int)
        assertTrue((result as Int) > 0)
    }

    @Test
    fun `resolve should return default drawable for empty path`() {
        val result = FileUrlResolver.resolve("")

        assertTrue(result is Int)
        assertTrue((result as Int) > 0)
    }

    @Test
    fun `resolve should return default drawable for blank path`() {
        val result = FileUrlResolver.resolve("   ")

        assertTrue(result is Int)
        assertTrue((result as Int) > 0)
    }

    @Test
    fun `resolve should return default drawable for invalid path`() {
        val result = FileUrlResolver.resolve("invalidPath")

        assertTrue(result is Int)
        assertTrue((result as Int) > 0)
    }

    @Test
    fun `resolve should return avatar default for avatar type`() {
        val result = FileUrlResolver.resolve(null, "avatar")

        assertTrue(result is Int)
        assertEquals(com.example.campustask.R.drawable.ic_avatar, result as Int)
    }

    @Test
    fun `resolve should return shop default for shop type`() {
        val result = FileUrlResolver.resolve(null, "shop")

        assertTrue(result is Int)
        assertEquals(com.example.campustask.R.drawable.ic_launcher_foreground, result as Int)
    }

    @Test
    fun `resolve should return shop default for unknown type`() {
        val result = FileUrlResolver.resolve(null, "unknown")

        assertTrue(result is Int)
        assertEquals(com.example.campustask.R.drawable.ic_launcher_foreground, result as Int)
    }

    @Test
    fun `resolved URL should have correct format`() {
        val relativePath = "uploads/test.jpg"
        val result = FileUrlResolver.resolve(relativePath)

        assertTrue(result is String)
        val url = result as String
        assertTrue(url.startsWith("http"))
        assertTrue(url.contains(relativePath))
    }

    @Test
    fun `resolved URL should not contain api suffix`() {
        val relativePath = "uploads/test.jpg"
        val result = FileUrlResolver.resolve(relativePath)

        assertTrue(result is String)
        val url = result as String
        assertFalse(url.contains("api/uploads"))
        assertTrue(url.contains("uploads"))
    }

    @Test
    fun `resolve should handle deep path`() {
        val deepPath = "uploads/shopitem/category/subcategory/item.jpg"
        val result = FileUrlResolver.resolve(deepPath)

        assertTrue(result is String)
        assertTrue((result as String).contains(deepPath))
    }

    @Test
    fun `resolve should handle path with spaces`() {
        val pathWithSpaces = "uploads/item with spaces.jpg"
        val result = FileUrlResolver.resolve(pathWithSpaces)

        assertTrue(result is String)
        assertTrue((result as String).contains(pathWithSpaces))
    }

    @Test
    fun `resolve should handle path with special characters`() {
        val specialPath = "uploads/item-test_v2.jpg"
        val result = FileUrlResolver.resolve(specialPath)

        assertTrue(result is String)
        assertTrue((result as String).contains(specialPath))
    }

    @Test
    fun `BASE_URL should be correctly processed`() {
        val relativePath = "uploads/test.jpg"
        val result = FileUrlResolver.resolve(relativePath)

        assertTrue(result is String)
        val url = result as String
        assertTrue(url.startsWith(fileBaseUrl))
        assertFalse(url.contains("api/uploads"))
    }

    @Test
    fun `resolve should work correctly for multiple calls`() {
        val paths = listOf(
            "uploads/test1.jpg",
            "uploads/test2.jpg",
            "uploads/test3.jpg"
        )

        for (path in paths) {
            val result = FileUrlResolver.resolve(path)
            assertTrue(result is String)
            assertTrue((result as String).contains(path))
        }
    }

    @Test
    fun `resolve should handle mixed valid and invalid paths`() {
        val paths = listOf(
            "uploads/valid.jpg",
            null,
            "",
            "uploads/another.jpg",
            "invalid"
        )

        for (path in paths) {
            assertNotNull(FileUrlResolver.resolve(path))
        }
    }
}
