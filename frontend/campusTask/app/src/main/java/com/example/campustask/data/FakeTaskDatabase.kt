package com.example.campustask.data

import com.example.campustask.model.Task

object FakeTaskDatabase {

    private val taskList = mutableListOf<Task>()

    private fun ensureInit() {
        if (taskList.isNotEmpty()) return

        taskList.add(
            Task(
                taskId = 1,
                title = "王者开黑",
                description = "缺辅助，来个会配合的",
                type = "GAME",
                publisherId = 1001,
                needPeople = 3,
                currentPeople = 1,
                rewardPoints = 20,
                rewardMoney = null,
                status = "OPEN",
                deadline = System.currentTimeMillis() + 3600_000,
                publisherName = "李四",
                createdAt = System.currentTimeMillis()
            )
        )

        taskList.add(
            Task(
                taskId = 2,
                title = "帮拿外卖",
                description = "二饭麻辣香锅",
                type = "LIFE",
                publisherId = 1002,
                needPeople = 1,
                currentPeople = 0,
                rewardPoints = 15,
                rewardMoney = null,
                status = "OPEN",
                deadline = System.currentTimeMillis() + 7200_000,
                publisherName = "李四",
                createdAt = System.currentTimeMillis()
            )
        )

        taskList.add(
            Task(
                taskId = 3,
                title = "高数互助",
                description = "一起写作业",
                type = "STUDY",
                publisherId = 1003,
                needPeople = 2,
                currentPeople = 1,
                rewardPoints = 30,
                rewardMoney = null,
                status = "IN_PROGRESS",
                deadline = System.currentTimeMillis() + 86400_000,
                publisherName = "罗翔",
                createdAt = System.currentTimeMillis()
            )
        )
    }

    fun getAllTasks(): List<Task> {
        ensureInit()
        return taskList.toList()
    }

    fun getTasksByStatus(status: String): List<Task> {
        ensureInit()
        return taskList.filter { it.status == status }
    }

    fun getByType(type: String): List<Task> {
        ensureInit()
        return taskList.filter { it.type == type }
    }

    fun addTask(task: Task) {
        ensureInit()
        taskList.add(task)
    }
}