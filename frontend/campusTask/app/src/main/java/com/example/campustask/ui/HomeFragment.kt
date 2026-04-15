package com.example.campustask.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.adapter.TaskAdapter
import com.example.campustask.model.Task
import com.example.campustask.repository.TaskRepository

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var adapter: TaskAdapter
    private lateinit var allTasks: List<Task>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val tabAll = view.findViewById<TextView>(R.id.tab_all)
        val tabGame = view.findViewById<TextView>(R.id.tab_game)
        val tabLife = view.findViewById<TextView>(R.id.tab_life)
        val tabStudy = view.findViewById<TextView>(R.id.tab_study)

        val tabs = listOf(tabAll, tabGame, tabLife, tabStudy)

        // 🔥 从“假数据库”获取数据
        allTasks = TaskRepository.getAllTasks()

        adapter = TaskAdapter(allTasks) { task ->
            val fragment = TaskDetailFragment.newInstance(task)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // ===== 默认选中 =====
        selectTab(tabAll, tabs)
        filter("全部")

        // ===== 点击事件 =====
        tabAll.setOnClickListener {
            selectTab(tabAll, tabs)
            filter("全部")
        }

        tabGame.setOnClickListener {
            selectTab(tabGame, tabs)
            filter("游戏")
        }

        tabLife.setOnClickListener {
            selectTab(tabLife, tabs)
            filter("生活")
        }

        tabStudy.setOnClickListener {
            selectTab(tabStudy, tabs)
            filter("学习")
        }
    }

    private fun filter(category: String) {

        val data = if (category == "全部") {
            TaskRepository.getAllTasks()
        } else {
            val type = when (category) {
                "游戏" -> "GAME"
                "生活" -> "LIFE"
                "学习" -> "STUDY"
                else -> ""
            }

            TaskRepository.getTasksByType(type)
        }

        adapter.update(data)
    }
    private fun selectTab(selected: TextView, tabs: List<TextView>) {
        for (tv in tabs) {
            tv.setBackgroundResource(R.drawable.bg_tag)
        }
        selected.setBackgroundResource(R.drawable.bg_tag_selected)
    }
}