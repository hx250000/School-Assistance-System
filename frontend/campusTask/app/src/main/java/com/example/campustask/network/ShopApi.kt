package com.example.campustask.network

import com.example.campustask.model.ShopItem
import com.example.campustask.model.response.BaseResponse
import retrofit2.Call
import retrofit2.http.*

interface ShopApi {

    @GET("shop/items")
    fun listItems(): Call<BaseResponse<List<ShopItem>>>

    @POST("shop/exchange")
    fun exchange(@Header("Authorization") token: String, @Query("itemId") itemId: Long): Call<BaseResponse<Long>>
}
