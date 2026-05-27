package com.example.campustask.repository

import android.content.Context
import android.util.Log
import com.example.campustask.model.request.AiGenerateRequest
import com.example.campustask.model.response.AiGenerateResponse
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.network.RetrofitClient.aiApi
import com.example.campustask.network.RetrofitClient.taskApi
import com.example.campustask.utils.AuthTokenStore
import com.example.campustask.utils.ResponseHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AiRepository {
    val TAG = "AiRepository"

    fun generateDesc(context: Context, request: AiGenerateRequest, callback: (Boolean, AiGenerateResponse?, String?) -> Unit) {
        aiApi.callAi(request).enqueue(object : Callback<BaseResponse<AiGenerateResponse>> {
            override fun onResponse(call: Call<BaseResponse<AiGenerateResponse>>, response: Response<BaseResponse<AiGenerateResponse>>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (body.code == 200) {
                        callback(true, body.data,null)
                    } else {
                        Log.d(TAG,"AiError: "+body.message)
                        callback(false, null,body.message ?: "完成任务失败")
                    }
                } else {
                    Log.d(TAG, "AiError: " + (body?.message ?: "AI生成失败，请手动输入"))
                    callback(false, null,body?.message ?: "AI生成失败，请手动输入")
                }
            }

            override fun onFailure(call: Call<BaseResponse<AiGenerateResponse>>, t: Throwable) {
                callback(false, null,t.message)
            }
        })
    }
}