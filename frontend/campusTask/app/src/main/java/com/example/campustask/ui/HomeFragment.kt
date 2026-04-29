package com.example.campustask.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.adapter.TaskAdapter
import com.example.campustask.repository.TaskRepository

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var adapter: TaskAdapter

    val TAG="HomeFragment"

    val taskRepo = TaskRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val tabAll = view.findViewById<TextView>(R.id.tab_all)
        val tabGame = view.findViewById<TextView>(R.id.tab_game)
        val tabLife = view.findViewById<TextView>(R.id.tab_life)
        val tabStudy = view.findViewById<TextView>(R.id.tab_study)

        val tabs = listOf(tabAll, tabGame, tabLife, tabStudy)

        // 获取数据
        /*val allTasks = taskRepo.mockGetAllTasks()

        adapter = TaskAdapter(allTasks) { task ->
            val fragment = TaskDetailFragment.newInstance(task)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }*/
        adapter = TaskAdapter(emptyList()) { task ->
            val fragment = TaskDetailFragment.newInstance(task)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // 默认选中
        selectTab(tabAll, tabs)
        loadData("全部")

        // 点击事件
        tabAll.setOnClickListener {
            selectTab(tabAll, tabs)
            loadData("全部")
        }
        tabGame.setOnClickListener {
            selectTab(tabGame, tabs)
            loadData("游戏")
        }
        tabLife.setOnClickListener {
            selectTab(tabLife, tabs)
            loadData("生活")
        }
        tabStudy.setOnClickListener {
            selectTab(tabStudy, tabs)
            loadData("学习")
        }
    }

//    private fun filter(category: String) {
//        val data = if (category == "全部") {
//            taskRepo.mockGetAllTasks()
//        } else {
//            val type = when (category) {
//                "游戏" -> "GAME"
//                "生活" -> "LIFE"
//                "学习" -> "STUDY"
//                else -> ""
//            }
//            taskRepo.mockGetTasksByType(type)
//        }
//        adapter.update(data)
//    }

    private fun selectTab(selected: TextView, tabs: List<TextView>) {
        for (tv in tabs) {
            tv.setBackgroundResource(R.drawable.bg_tag)
        }
        selected.setBackgroundResource(R.drawable.bg_tag_selected)
    }

    private fun loadData(category: String = "全部") {
        Log.i(TAG,"loadingdata...")

        val safeContext = context ?: return

        //更新统计数据
        taskRepo.stats(safeContext){ success, data, err->
            if (!isAdded || activity == null) return@stats
            activity?.runOnUiThread {
                if (success && data != null) {
                    updateHeadStats(data.inProgress, data.finished, data.users)
                } else {
                    Toast.makeText(safeContext, err ?: "加载失败", Toast.LENGTH_SHORT).show()
                    updateHeadStats(100, 900, 500)
                }
            }
        }


        // 如果是“全部”，直接调用接口；如果是分类，可以调用接口后在客户端过滤
        taskRepo.getAllTasks(safeContext) { success, tasks, error ->
            if (!isAdded || activity == null) return@getAllTasks
            activity?.runOnUiThread {
                if (success && tasks != null) {
                    val filteredData = if (category == "全部") {
                        tasks
                    } else {
                        val type = when (category) {
                            "游戏" -> "GAME"
                            "生活" -> "LIFE"
                            "学习" -> "STUDY"
                            else -> ""
                        }
                        tasks.filter { it.type == type }
                    }
                    adapter.update(filteredData)
                } else {
                    if (error == "用户未登录") {
                        // 返回登录界面
                        startActivity(Intent(activity, LoginActivity::class.java))
                    }
                    // 提示错误
                    Toast.makeText(safeContext, error ?: "加载失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun updateHeadStats(inprogress: Int, finished: Int, users:Int){
        val inprogressTextView=view?.findViewById<TextView>(R.id.inprogress)
        val finishedTextView=view?.findViewById<TextView>(R.id.finished)
        val totaluserTextView=view?.findViewById<TextView>(R.id.users)
        inprogressTextView?.text=inprogress.toString()
        finishedTextView?.text=finished.toString()
        totaluserTextView?.text=users.toString()
    }
}