package com.example.campustask.repository

import com.example.campustask.data.FakeTaskDatabase
import com.example.campustask.model.Task

object TaskRepository {

    fun getAllTasks(): List<Task> {
        return FakeTaskDatabase.getAllTasks()
    }

    fun getTasksByStatus(status: String): List<Task> {
        return FakeTaskDatabase.getTasksByStatus(status)
    }

    fun getTasksByType(type: String): List<Task> {
        return FakeTaskDatabase.getByType(type)
    }

    fun addTask(task: Task) {
        FakeTaskDatabase.addTask(task)
    }
}