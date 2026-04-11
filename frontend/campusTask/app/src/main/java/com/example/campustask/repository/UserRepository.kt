package com.example.campustask.repository

import com.example.campustask.model.*
import com.example.campustask.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {

    // ===========================
    // 登录接口
    // ===========================
    fun login(phone: String, password: String, callback: (Boolean, String?) -> Unit) {
        val request = LoginRequest(phone, password)
        RetrofitClient.api.login(request).enqueue(object : Callback<BaseResponse<LoginResponse>> {
            override fun onResponse(call: Call<BaseResponse<LoginResponse>>, response: Response<BaseResponse<LoginResponse>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, response.body()?.data?.token) // 返回 token
                } else {
                    callback(false, response.body()?.message)
                }
            }

            override fun onFailure(call: Call<BaseResponse<LoginResponse>>, t: Throwable) {
                callback(false, t.message)
            }
        })
    }

    // ===========================
    // 注册接口
    // ===========================
    fun register(username: String, phone: String, password: String, callback: (Boolean, String?) -> Unit) {
        val request = RegisterRequest(username, phone, password)
        RetrofitClient.api.register(request).enqueue(object : Callback<BaseResponse<RegisterResponse>> {
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