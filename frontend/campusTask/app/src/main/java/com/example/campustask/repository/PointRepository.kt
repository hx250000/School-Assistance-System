package com.example.campustask.repository

import android.content.Context
import android.util.Log
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.PointRecord
import com.example.campustask.model.response.PointHistoryResponse
import com.example.campustask.network.RetrofitClient
import com.example.campustask.utils.AuthTokenStore
import com.example.campustask.utils.ResponseHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PointRepository {

    val TAG = "PointRepository"

    //获取个人积分记录
    fun getMyPointsHistory(context: Context, callback: (Boolean, PointHistoryResponse?, String?) -> Unit){
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        RetrofitClient.pointApi.getMyPointsHistory(header).enqueue(object : Callback<BaseResponse<PointHistoryResponse>> {
            override fun onResponse(call: Call<BaseResponse<PointHistoryResponse>>, response: Response<BaseResponse<PointHistoryResponse>>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (ResponseHandler.isUnauthorized(body.code)) {
                        ResponseHandler.handleUnauthorized(context)
                        return
                    }
                    if (body.code == 200) {
                        callback(true, body.data, null)
                    } else {
                        callback(false, null, body.message ?: "获取积分记录失败")
                    }
                } else {
                    callback(false, null, body?.message ?: "获取积分记录失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<PointHistoryResponse>>, t: Throwable) {
                Log.d(TAG,t.message?:"网络连接失败，请稍后再试")
                callback(false, null, "网络连接失败，请稍后再试")
            }
        })
    }
    //获取个人当前积分
    fun getMyCurrentPoints(context: Context, callback: (Boolean, Int?, String?) -> Unit){
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        RetrofitClient.pointApi.getMyPoints(header).enqueue(object : Callback<BaseResponse<Int>> {
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
                        callback(false, null, body.message ?: "获取积分失败")
                    }
                } else {
                    callback(false, null, body?.message ?: "获取积分失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<Int>>, t: Throwable) {
                Log.d(TAG,t.message?:"网络连接失败，请稍后再试")
                callback(false, null, "网络连接失败，请稍后再试")
            }
        })
    }
}