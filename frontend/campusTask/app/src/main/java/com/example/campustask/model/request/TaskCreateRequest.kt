package com.example.campustask.model.request

data class TaskCreateRequest (
    val title: String,                // 必填
    val description: String ,     // 默认为空字符串
    val type: String ,      // 默认类型
    val needPeople: Int ,          // 默认 1 人
    val rewardPoints: Int ,        // 默认 0 积分
    val deadline: String
)