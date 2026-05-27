package com.example.campustask.network

import com.example.campustask.model.request.AiGenerateRequest
import com.example.campustask.model.request.LoginRequest
import com.example.campustask.model.response.AiGenerateResponse
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AiApi {
    @POST("ai/description")
    fun callAi(@Body request: AiGenerateRequest): Call<BaseResponse<AiGenerateResponse>> // 返回 description

}