package com.example.campustask.network

import com.example.campustask.model.AchievementOverview
import com.example.campustask.model.response.BaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface AchievementApi {
    @GET("achievements/my")
    fun getMyAchievements(@Header("Authorization") token: String): Call<BaseResponse<AchievementOverview>>

}