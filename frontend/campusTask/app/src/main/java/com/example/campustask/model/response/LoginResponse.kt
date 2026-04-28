package com.example.campustask.model.response

data class LoginResponse (
    val token: String,
    val userId: Long,
    val username: String
)