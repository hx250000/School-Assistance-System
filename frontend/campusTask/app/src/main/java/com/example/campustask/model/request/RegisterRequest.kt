package com.example.campustask.model.request

data class RegisterRequest(
    val username: String,
    val phone: String,
    val password: String
)