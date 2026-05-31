package com.example.campustask.utils

import com.example.campustask.R
import com.example.campustask.network.RetrofitClient

object FileUrlResolver {

    // 统一管理后端的 Base URL。与 RetrofitClient 使用相同基础地址。
    private val BASE_URL = RetrofitClient.BASE_URL.removeSuffix("api/")

    /**
     * 将后端返回的相对路径转换为可直接访问的完整 URL
     * @param relativePath 后端返回的相对路径（如 "uploads/shopitem/xxx.jpg"）
     * @param defaultType 兜底图类型："avatar" 代表头像，"shop" 代表商品
     * @return 完整的 URL 或者是本地本地 Drawable 资源 ID 包装成的 Any
     */
    fun resolve(relativePath: String?, defaultType: String = "shop"): Any {
        // 如果路径为 null 或者为空字符串，直接返回对应类型的本地默认图
        if (relativePath.isNullOrBlank()) {
            return getDefaultDrawable(defaultType)
        }

        // 如果包含斜杠，说明是正常的后端存储路径，拼接完整 URL
        if (relativePath.contains("/") || relativePath.contains("\\")) {
            return "$BASE_URL$relativePath"
        }

        // 兜底安全：如果是个非法字符串，直接给默认图
        return getDefaultDrawable(defaultType)
    }

    /**
     * 根据业务类型获取对应的默认本地 Drawable 资源
     */
    private fun getDefaultDrawable(type: String): Int {
        return when (type) {
            "avatar" -> R.drawable.ic_avatar      // 你的用户默认头像
            else -> R.drawable.ic_launcher_foreground          // 你的商品默认占位图（若有单独的商品默认图可换成对应的 R.drawable）
        }
    }
}