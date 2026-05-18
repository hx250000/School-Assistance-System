package com.example.campustask.model

data class Task(
    val taskId: Long,
    val title: String,
    val description: String,
    val type: String,
    val publisherId: Long,
    val publisherName: String,
    val needPeople: Int,
    val currentPeople: Int,
    val rewardPoints: Int,
    val rewardMoney: Double? = null,
    val status: String,
    val deadline: Long,
    val createdAt: Long
)