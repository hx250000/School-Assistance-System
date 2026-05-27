package com.example.campustask.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/api/" // 连接本机后端

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val pointApi: PointApi by lazy { retrofit.create(PointApi::class.java) }
    val taskApi: TaskApi by lazy { retrofit.create(TaskApi::class.java) }
    val shopApi: ShopApi by lazy { retrofit.create(ShopApi::class.java) }
    val achievementApi: AchievementApi by lazy { retrofit.create(AchievementApi::class.java) }
    val aiApi: AiApi by lazy { retrofit.create(AiApi::class.java) }
    val fileApi: FileApi by lazy { retrofit.create(FileApi::class.java) }

}