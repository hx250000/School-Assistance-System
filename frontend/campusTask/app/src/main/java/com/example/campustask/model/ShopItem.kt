package com.example.campustask.model

data class ShopItem(
    val id: Long,
    val name: String,
    val price: Int,
    val stock: Int,
    val description: String,
    val imageRes: String
)