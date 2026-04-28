package com.example.campustask.repository

import android.content.Context
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.PointRecord
import com.example.campustask.network.RetrofitClient
import com.example.campustask.utils.AuthTokenStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PointRepository {
    //获取个人积分记录
    fun getMyPointsHistory(context: Context, callback: (Boolean, List<PointRecord>?, String?) -> Unit){
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        RetrofitClient.pointApi.getMyPointsHistory(header).enqueue(object : Callback<BaseResponse<List<PointRecord>>> {
            override fun onResponse(call: Call<BaseResponse<List<PointRecord>>>, response: Response<BaseResponse<List<PointRecord>>>) {
                val body = response.body()
                if (response.isSuccessful && body?.code == 200) {
                    callback(true, body.data, null) // 成功返回数据
                } else {
                    callback(false, null, body?.message ?: "用户信息获取失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<PointRecord>>>, t: Throwable) {
                callback(false, null, t.message)
            }
        })
    }
}