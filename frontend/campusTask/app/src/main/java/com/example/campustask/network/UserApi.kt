package com.example.campustask.network

import com.example.campustask.model.BaseResponse
import com.example.campustask.model.LoginRequest
import com.example.campustask.model.RegisterRequest
import com.example.campustask.model.User
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call


interface UserApi {

    @POST("user/register")
    fun register(@Body request: RegisterRequest): Call<BaseResponse<User>>

    @POST("user/login")
    fun login(@Body request: LoginRequest): Call<BaseResponse<String>> // 返回 token
}