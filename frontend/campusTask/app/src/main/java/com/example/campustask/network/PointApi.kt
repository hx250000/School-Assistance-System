package com.example.campustask.network

import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.PointRecord
import com.example.campustask.model.request.RegisterRequest
import com.example.campustask.model.response.PointHistoryResponse
import com.example.campustask.model.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header

interface PointApi {
    @GET("points/mypoints")
    fun getMyPoints(@Header("Authorization") token: String): Call<BaseResponse<Int>>

    @GET("points/history")
    fun getMyPointsHistory(@Header("Authorization") token: String): Call<BaseResponse<PointHistoryResponse>>
}