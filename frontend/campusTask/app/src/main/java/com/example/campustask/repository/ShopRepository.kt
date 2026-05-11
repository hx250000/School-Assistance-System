package com.example.campustask.repository

import android.content.Context
import android.util.Log
import com.example.campustask.model.ShopItem
import com.example.campustask.model.request.ShopExchangeRequest
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.network.RetrofitClient
import com.example.campustask.utils.AuthTokenStore
import com.example.campustask.utils.ResponseHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopRepository {

    private val shopApi = RetrofitClient.shopApi

    private val TAG="ShopRepository"

    fun getShopItems(callback: (Boolean, List<ShopItem>?, String?) -> Unit) {
        shopApi.listItems().enqueue(object : Callback<BaseResponse<List<ShopItem>>> {
            override fun onResponse(call: Call<BaseResponse<List<ShopItem>>>, response: Response<BaseResponse<List<ShopItem>>>) {
                val body = response.body()
                if (response.isSuccessful && body?.code == 200) {
                    callback(true, body.data, null)
                } else {
                    callback(false, null, body?.message ?: "获取商品列表失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<ShopItem>>>, t: Throwable) {
                Log.d(TAG,t.message?:"网络连接失败")
                callback(false, null, "网络连接失败，请稍后再试")
            }
        })
    }

    fun exchangeItem(context: Context, itemId: Long, callback: (Boolean, Long?, String?) -> Unit) {
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        shopApi.exchange(header, ShopExchangeRequest(itemId)).enqueue(object : Callback<BaseResponse<Long>> {
            override fun onResponse(call: Call<BaseResponse<Long>>, response: Response<BaseResponse<Long>>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (ResponseHandler.isUnauthorized(body.code)) {
                        ResponseHandler.handleUnauthorized(context)
                        return
                    }
                    if (body.code == 200) {
                        callback(true, body.data, null)
                    } else {
                        callback(false, null, body.message ?: "兑换商品失败")
                    }
                } else {
                    callback(false, null, body?.message ?: "兑换商品失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<Long>>, t: Throwable) {
                Log.d(TAG,t.message?:"网络连接失败")
                callback(false, null, t.message)
            }
        })
    }
    //获取个人兑换次数
    fun getMyExchangeCount(context: Context, callback: (Boolean, Int?, String?) -> Unit){
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        RetrofitClient.shopApi.getMyExchangeCount(header).enqueue(object : Callback<BaseResponse<Int>> {
            override fun onResponse(call: Call<BaseResponse<Int>>, response: Response<BaseResponse<Int>>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (ResponseHandler.isUnauthorized(body.code)) {
                        ResponseHandler.handleUnauthorized(context)
                        return
                    }
                    if (body.code == 200) {
                        callback(true, body.data, null)
                    } else {
                        callback(false, null, body.message ?: "获取兑换次数失败")
                    }
                } else {
                    callback(false, null, body?.message ?: "获取兑换次数失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<Int>>, t: Throwable) {
                Log.d(TAG,t.message?:"网络连接失败，请稍后再试")
                callback(false, null, "网络连接失败，请稍后再试")
            }
        })
    }
}