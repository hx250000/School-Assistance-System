package com.example.campustask.network

import com.example.campustask.model.Task
import com.example.campustask.model.request.TaskCreateRequest
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.response.HomeStatResp
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskApi {

    @POST("task/create")
    fun createTask(@Header("Authorization") token: String, @Body request: TaskCreateRequest): Call<BaseResponse<Long>>

    @GET("task/list")
    fun listTasks(@Header("Authorization") token: String, @Query("page") page: Int, @Query("size") size: Int): Call<BaseResponse<List<Task>>>

    @POST("task/grab/{taskId}")
    fun grabTask(@Header("Authorization") token: String, @Path("taskId") taskId: Long): Call<BaseResponse<Task>>

    @POST("task/finish/{taskId}")
    fun finishTask(@Header("Authorization") token: String, @Path("taskId") taskId: Long): Call<BaseResponse<Void>>

    @POST("task/cancel/{taskId}")
    fun cancelTask(@Header("Authorization") token: String, @Path("taskId") taskId: Long): Call<BaseResponse<Void>>

    @GET("task/my-history")
    fun getMyTaskHistory(@Header("Authorization") token: String): Call<BaseResponse<List<Task>>>

    @GET("task/search")
    fun searchTasks(@Header("Authorization") token: String, @Query("title") keywords: String): Call<BaseResponse<List<Task>>>

    @GET("task/{taskId}")
    fun getTaskById(@Header("Authorization") token: String, @Path("taskId") taskId: Long): Call<BaseResponse<Task>>

    @GET("task/stats")
    fun stats(): Call<BaseResponse<HomeStatResp>>
}
