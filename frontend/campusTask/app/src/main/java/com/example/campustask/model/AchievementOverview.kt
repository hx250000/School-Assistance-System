package com.example.campustask.model

data class AchievementOverview (
    val unlockedCount: Int,
    val totalCount: Int,
    val completionRate: Double,
    val achievements: List<Achievement>
)