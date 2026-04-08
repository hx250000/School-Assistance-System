package com.example.campustask.model

data class User(
    val id: Long,
    val username: String,
    val phone: String,
    val password: String,
    val deleted: Boolean = false // 软删除标记
)