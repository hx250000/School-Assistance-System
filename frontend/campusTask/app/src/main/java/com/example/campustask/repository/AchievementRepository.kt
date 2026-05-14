package com.example.campustask.repository

import android.content.Context
import android.util.Log
import com.example.campustask.model.AchievementOverview
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.response.PointHistoryResponse
import com.example.campustask.network.RetrofitClient
import com.example.campustask.utils.AuthTokenStore
import com.example.campustask.utils.ResponseHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AchievementRepository {
    private val TAG="AchievementRepository"
    fun getMyAchievements(context: Context, callback: (Boolean, AchievementOverview?, String?) -> Unit){
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        RetrofitClient.achievementApi.getMyAchievements(header).enqueue(object : Callback<BaseResponse<AchievementOverview>> {
            override fun onResponse(call: Call<BaseResponse<AchievementOverview>>, response: Response<BaseResponse<AchievementOverview>>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (ResponseHandler.isUnauthorized(body.code)) {
                        ResponseHandler.handleUnauthorized(context)
                        return
                    }
                    if (body.code == 200) {
                        callback(true, body.data, null)
                    } else {
                        callback(false, null, body.message ?: "获取成就记录失败")
                    }
                } else {
                    callback(false, null, body?.message ?: "获取成就记录失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<AchievementOverview>>, t: Throwable) {
                Log.d(TAG,t.message?:"网络连接失败，请稍后再试")
                callback(false, null, "网络连接失败，请稍后再试")
            }
        })
    }
}