package com.example.campustask.network

import com.example.campustask.model.BaseResponse
import com.example.campustask.model.LoginRequest
import com.example.campustask.model.LoginResponse
import com.example.campustask.model.RegisterRequest
import com.example.campustask.model.RegisterResponse
import com.example.campustask.model.User
import com.example.campustask.model.UserInfo
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header


interface UserApi {

    @POST("user/register")
    fun register(@Body request: RegisterRequest): Call<BaseResponse<RegisterResponse>>

    @POST("user/login")
    fun login(@Body request: LoginRequest): Call<BaseResponse<LoginResponse>> // 返回 token

    @GET("user/info")
    fun getMyInfo(@Header("Authorization") token: String): Call<BaseResponse<UserInfo>>
}