package com.example.campustask.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.campustask.ui.LoginActivity

object ResponseHandler {

    private const val UNAUTHORIZED_CODE = 401
    private const val UNAUTHORIZED_MESSAGE = "登录状态失效，请重新登录"

    /**
     * 检查响应是否为未授权（JWT过期等）
     */
    fun isUnauthorized(code: Int): Boolean {
        return code == UNAUTHORIZED_CODE
    }

    /**
     * 处理未授权情况：清除token并跳转到登录页
     */
    fun handleUnauthorized(context: Context) {
        AuthTokenStore.clearToken(context)
        Toast.makeText(context, UNAUTHORIZED_MESSAGE, Toast.LENGTH_SHORT).show()
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    /**
     * 统一处理API响应
     * @return true 表示响应成功且非401，false 表示需要处理错误或401
     */
    fun <T> handleResponse(
        context: Context?,
        code: Int,
        successCallback: (T?, String?) -> Unit,
        errorCallback: (String?) -> Unit
    ): Boolean {
        if (code == 200) {
            return true
        }
        
        if (isUnauthorized(code) && context != null) {
            handleUnauthorized(context)
            return false
        }
        
        return false
    }
}