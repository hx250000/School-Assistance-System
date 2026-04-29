package com.example.campustask.repository

import android.content.Context
import com.example.campustask.data.FakeTaskDatabase
import com.example.campustask.model.response.BaseResponse
import com.example.campustask.model.*
import com.example.campustask.model.request.TaskCreateRequest
import com.example.campustask.model.response.HomeStatResp
import com.example.campustask.network.RetrofitClient
import com.example.campustask.utils.AuthTokenStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskRepository {

    fun mockGetAllTasks(): List<Task> {
        return FakeTaskDatabase.getAllTasks()
    }

    fun mockGetTasksByStatus(status: String): List<Task> {
        return FakeTaskDatabase.getTasksByStatus(status)
    }

    fun mockGetTasksByType(type: String): List<Task> {
        return FakeTaskDatabase.getByType(type)
    }

    fun mockAddTask(task: Task) {
        FakeTaskDatabase.addTask(task)
    }

    private val taskApi = RetrofitClient.taskApi

    // 创建任务
    fun createTask(context: Context, request: TaskCreateRequest, callback: (Boolean, Long?, String?) -> Unit) {
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        taskApi.createTask(header, request).enqueue(object : Callback<BaseResponse<Long>> {
            override fun onResponse(call: Call<BaseResponse<Long>>, response: Response<BaseResponse<Long>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, response.body()?.data, null)
                } else {
                    callback(false, null, response.body()?.message ?: "创建任务失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<Long>>, t: Throwable) {
                callback(false, null, t.message)
            }
        })
    }

    // 获取任务列表（分页）
    fun getAllTasks(context: Context, page: Int = 0, size: Int = 20, callback: (Boolean, List<Task>?, String?) -> Unit) {
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        taskApi.listTasks(header, page, size).enqueue(object : Callback<BaseResponse<List<Task>>> {
            override fun onResponse(call: Call<BaseResponse<List<Task>>>, response: Response<BaseResponse<List<Task>>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, response.body()?.data, null)
                } else {
                    callback(false, null, response.body()?.message ?: "获取任务列表失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<Task>>>, t: Throwable) {
                callback(false, null, t.message)
            }
        })
    }

    // 根据状态获取任务（客户端过滤，如果后端不支持，可在回调中过滤）
    fun getTasksByStatus(context: Context, status: String, page: Int = 0, size: Int = 20, callback: (Boolean, List<Task>?, String?) -> Unit) {
        getAllTasks(context, page, size) { success, tasks, error ->
            if (success && tasks != null) {
                val filtered = tasks.filter { it.status == status }
                callback(true, filtered, null)
            } else {
                callback(false, null, error)
            }
        }
    }

    // 根据类型获取任务（客户端过滤）
    fun getTasksByType(context: Context, type: String, page: Int = 0, size: Int = 20, callback: (Boolean, List<Task>?, String?) -> Unit) {
        getAllTasks(context, page, size) { success, tasks, error ->
            if (success && tasks != null) {
                val filtered = tasks.filter { it.type == type }
                callback(true, filtered, null)
            } else {
                callback(false, null, error)
            }
        }
    }

    // 抢任务
    fun grabTask(context: Context, taskId: Long, callback: (Boolean, Task?, String?) -> Unit) {
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        taskApi.grabTask(header, taskId).enqueue(object : Callback<BaseResponse<Task>> {
            override fun onResponse(call: Call<BaseResponse<Task>>, response: Response<BaseResponse<Task>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, response.body()?.data, null)
                } else {
                    callback(false, null, response.body()?.message ?: "抢任务失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<Task>>, t: Throwable) {
                callback(false, null, t.message)
            }
        })
    }

    // 完成任务
    fun finishTask(context: Context, taskId: Long, callback: (Boolean, String?) -> Unit) {
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, "用户未登录")
            return
        }

        taskApi.finishTask(header, taskId).enqueue(object : Callback<BaseResponse<Void>> {
            override fun onResponse(call: Call<BaseResponse<Void>>, response: Response<BaseResponse<Void>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, null)
                } else {
                    callback(false, response.body()?.message ?: "完成任务失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<Void>>, t: Throwable) {
                callback(false, t.message)
            }
        })
    }

    // 取消任务
    fun cancelTask(context: Context, taskId: Long, callback: (Boolean, String?) -> Unit) {
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, "用户未登录")
            return
        }

        taskApi.cancelTask(header, taskId).enqueue(object : Callback<BaseResponse<Void>> {
            override fun onResponse(call: Call<BaseResponse<Void>>, response: Response<BaseResponse<Void>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, null)
                } else {
                    callback(false, response.body()?.message ?: "取消任务失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<Void>>, t: Throwable) {
                callback(false, t.message)
            }
        })
    }

    // 获取我的任务历史
    fun getMyTaskHistory(context: Context, callback: (Boolean, List<Task>?, String?) -> Unit) {
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        taskApi.getMyTaskHistory(header).enqueue(object : Callback<BaseResponse<List<Task>>> {
            override fun onResponse(call: Call<BaseResponse<List<Task>>>, response: Response<BaseResponse<List<Task>>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, response.body()?.data, null)
                } else {
                    callback(false, null, response.body()?.message ?: "获取任务历史失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<Task>>>, t: Throwable) {
                callback(false, null, t.message)
            }
        })
    }

    // 搜索任务
    fun searchTasks(context: Context, keywords: String, callback: (Boolean, List<Task>?, String?) -> Unit) {
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        taskApi.searchTasks(header, keywords).enqueue(object : Callback<BaseResponse<List<Task>>> {
            override fun onResponse(call: Call<BaseResponse<List<Task>>>, response: Response<BaseResponse<List<Task>>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, response.body()?.data, null)
                } else {
                    callback(false, null, response.body()?.message ?: "搜索任务失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<Task>>>, t: Throwable) {
                callback(false, null, t.message)
            }
        })
    }

    // 根据ID获取任务
    fun getTaskById(context: Context, taskId: Long, callback: (Boolean, Task?, String?) -> Unit) {
        val header = AuthTokenStore.authorizationHeader(context)
        if (header == null) {
            callback(false, null, "用户未登录")
            return
        }

        taskApi.getTaskById(header, taskId).enqueue(object : Callback<BaseResponse<Task>> {
            override fun onResponse(call: Call<BaseResponse<Task>>, response: Response<BaseResponse<Task>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, response.body()?.data, null)
                } else {
                    callback(false, null, response.body()?.message ?: "获取任务失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<Task>>, t: Throwable) {
                callback(false, null, t.message)
            }
        })
    }

    fun stats(context: Context, callback: (Boolean, HomeStatResp?, String?) -> Unit){
        taskApi.stats().enqueue(object : Callback<BaseResponse<HomeStatResp>> {
            override fun onResponse(call: Call<BaseResponse<HomeStatResp>>, response: Response<BaseResponse<HomeStatResp>>) {
                if (response.isSuccessful && response.body()?.code == 200) {
                    callback(true, response.body()?.data, null)
                } else {
                    callback(false, null, response.body()?.message ?: "返回统计信息失败")
                }
            }

            override fun onFailure(call: Call<BaseResponse<HomeStatResp>>, t: Throwable) {
                callback(false, null, t.message)
            }
        })
    }
}
