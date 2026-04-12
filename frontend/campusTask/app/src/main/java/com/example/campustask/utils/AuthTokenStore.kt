package com.example.campustask.utils

import android.content.Context

object AuthTokenStore {

    private const val PREFS_NAME = "campus_task_auth"
    private const val KEY_TOKEN = "jwt_token"

    fun saveToken(context: Context, token: String) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun getToken(context: Context): String? =
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)

    fun clearToken(context: Context) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_TOKEN)
            .apply()
    }

    /** 供需要登录的接口使用：后端要求 `Authorization: Bearer <token>` */
    fun authorizationHeader(context: Context): String? {
        val raw = getToken(context) ?: return null
        return "Bearer $raw"
    }
}
