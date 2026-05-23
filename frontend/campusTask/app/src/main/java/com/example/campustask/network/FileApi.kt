package com.example.campustask.network

import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.response.FileUploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {
    // 上传商品图片
    @Multipart
    @POST("items/image")
    fun uploadShopItemImage(
        @Part file: MultipartBody.Part
    ): Call<BaseResponse<FileUploadResponse>>

    // 上传用户头像（需要携带 Token 认证）
    @Multipart
    @POST("user/avatar")
    fun uploadUserAvatar(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<BaseResponse<FileUploadResponse>>
}