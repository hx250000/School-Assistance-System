package com.example.campustask.repository

import android.content.Context
import android.util.Log
import com.example.campustask.model.*
import com.example.campustask.model.request.LoginRequest
import com.example.campustask.model.request.RegisterRequest
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.response.LoginResponse
import com.example.campustask.model.response.RegisterResponse
import com.example.campustask.network.RetrofitClient
import com.example.campustask.utils.AuthTokenStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {

    private val TAG="UserRepository"

    // 登录接口
    fun login(phone: String, password: String, callback: (Boolean, String?) -> Unit) {
        val request = LoginRequest(phone, password)
        RetrofitClient.userApi.login(request).enqueue(object : Callback<BaseResponse<LoginResponse>> {
            override fun onResponse(call: Call<BaseResponse<LoginResponse>>, response: Response<BaseResponse<LoginResponse>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, response.body()?.data?.token) // 返回 token
                } else {
                    callback(false, response.body()?.message)
                }
            }

            override fun onFailure(call: Call<BaseResponse<LoginResponse>>, t: Throwable) {
                Log.d(TAG,t.message?:"网络连接失败")
                callback(false, "网络异常，请稍后再试")
            }
        })
    }

    // 注册接口
    fun register(username: String, phone: String, password: String, callback: (Boolean, String?) -> Unit) {
        val request = RegisterRequest(username, phone, password)
        RetrofitClient.userApi.register(request).enqueue(object : Callback<BaseResponse<RegisterResponse>> {
            override fun onResponse(call: Call<BaseResponse<RegisterResponse>>, response: Response<BaseResponse<RegisterResponse>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, null)
                } else {
                    callback(false, response.body()?.message)
                }
            }

            override fun onFailure(call: Call<BaseResponse<RegisterResponse>>, t: Throwable) {
                callback(false, t.message)
            }
        })
    }

    //获取个人信息
    fun getMyInfo(context: Context, callback: (Boolean, UserInfo?, String?) -> Unit){
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        RetrofitClient.userApi.getMyInfo(header).enqueue(object : Callback<BaseResponse<UserInfo>> {
            override fun onResponse(call: Call<BaseResponse<UserInfo>>, response: Response<BaseResponse<UserInfo>>) {
                val body = response.body()
                if (response.isSuccessful && body?.code == 200) {
                    callback(true, body.data, null) // 成功返回数据
                } else {
                    callback(false, null, body?.message ?: "用户信息获取失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<UserInfo>>, t: Throwable) {
                Log.d(TAG,t.message?:"网络连接失败")
                callback(false, null, "网络异常，请稍后再试")
            }
        })
    }

    // ===========================
    // Mock 登录（UI 开发用）
    // ===========================
    fun mockLogin(phone: String, password: String, callback: (Boolean, String?) -> Unit) {
        if (phone == "18069801871" && password == "123456") {
            callback(true, "mock-token-123")
        } else {
            callback(false, "手机号或密码错误")
        }
    }

    // ===========================
    // Mock 注册（UI 开发用）
    // ===========================
    fun mockRegister(username: String, phone: String, password: String, callback: (Boolean, String?) -> Unit) {
        if (username.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
            callback(true, null)
        } else {
            callback(false, "注册信息不完整")
        }
    }
}