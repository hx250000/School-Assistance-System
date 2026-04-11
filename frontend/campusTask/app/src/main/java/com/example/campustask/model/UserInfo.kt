package com.example.campustask.model

data class UserInfo(
    val id: Long,
    val username: String,
    val phone: String,
    val points: Int,
    val creditScore: Int,
    val level: Int,
    val createdAt: String // 先用 String 接收 JSON 序列化后的时间
)
