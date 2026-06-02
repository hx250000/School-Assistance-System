package com.example.campustask.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.campustask.model.UserInfo
import com.example.campustask.model.request.LoginRequest
import com.example.campustask.model.request.RegisterRequest
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.response.FileUploadResponse
import com.example.campustask.model.response.LoginResponse
import com.example.campustask.model.response.RegisterResponse
import com.example.campustask.network.RetrofitClient
import com.example.campustask.utils.AuthTokenStore
import com.example.campustask.utils.ResponseHandler
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
                    Log.d(TAG,response.body()?.message?:"network error")
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
                if (response.isSuccessful && body != null) {
                    if (ResponseHandler.isUnauthorized(body.code)) {
                        ResponseHandler.handleUnauthorized(context)
                        return
                    }
                    if (body.code == 200) {
                        callback(true, body.data, null)
                    } else {
                        callback(false, null, body.message ?: "用户信息获取失败")
                    }
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

    fun uploadAvatar(context: Context, uri: Uri, callback: (Boolean, FileUploadResponse?, String?) -> Unit) {
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        val resolver = context.contentResolver
        val inputStream = resolver.openInputStream(uri)
        if (inputStream == null) {
            callback(false, null, "无法读取图片文件")
            return
        }

        val bytes = inputStream.use { it.readBytes() }
        val contentType = resolver.getType(uri) ?: "image/jpeg"
        val fileName = getFileName(context, uri)
        val requestBody = bytes.toRequestBody(contentType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", fileName, requestBody)

        RetrofitClient.fileApi.uploadUserAvatar(header, part).enqueue(object : Callback<BaseResponse<FileUploadResponse>> {
            override fun onResponse(call: Call<BaseResponse<FileUploadResponse>>, response: Response<BaseResponse<FileUploadResponse>>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (ResponseHandler.isUnauthorized(body.code)) {
                        ResponseHandler.handleUnauthorized(context)
                        return
                    }
                    if (body.code == 200) {
                        callback(true, body.data, null)
                    } else {
                        callback(false, null, body.message ?: "上传头像失败")
                    }
                } else {
                    callback(false, null, body?.message ?: "上传头像失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<FileUploadResponse>>, t: Throwable) {
                Log.d(TAG, t.message ?: "网络连接失败")
                callback(false, null, t.message ?: "网络异常，请稍后再试")
            }
        })
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name = "avatar.jpg"
        val cursor: Cursor? = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
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