package com.example.campustask.repository

import com.example.campustask.data.FakeTaskDatabase
import com.example.campustask.model.Task

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
}