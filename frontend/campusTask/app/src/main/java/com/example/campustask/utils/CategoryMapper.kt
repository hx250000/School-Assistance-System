package com.example.campustask.util

object CategoryMapper {

    fun toType(category: String): String {
        return when (category) {
            "游戏" -> "GAME"
            "生活" -> "LIFE"
            "学习" -> "STUDY"
            else -> ""
        }
    }

    fun toCategory(type: String): String {
        return when (type) {
            "GAME" -> "游戏"
            "LIFE" -> "生活"
            "STUDY" -> "学习"
            else -> "全部"
        }
    }
}