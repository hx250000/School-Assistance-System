package com.example.campustask.model.response

import com.example.campustask.model.PointRecord

data class PointHistoryResponse(
    val pointsHistoryList: List<PointRecord>,
    val increasePoints: Int,
    val decreasePoints: Int
)
