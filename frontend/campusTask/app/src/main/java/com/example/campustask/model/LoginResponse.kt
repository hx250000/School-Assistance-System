package com.example.campustask.model

data class LoginResponse (
    val token: String,
    val userId: Long,
    val username: String
)