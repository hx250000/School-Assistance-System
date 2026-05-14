package com.example.campustask.model

import java.time.LocalDateTime

data class Achievement(
    val id: Long,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val totalProgress: Int,
    val iconUrl: String,
    val isUnlocked: Boolean,
    val unlockedAt: Long?
)