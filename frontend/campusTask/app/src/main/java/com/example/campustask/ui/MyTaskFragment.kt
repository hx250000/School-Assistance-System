package com.example.campustask.ui

import android.R.attr.data
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.adapter.MyTaskAdapter
import com.example.campustask.data.FakeTaskDatabase
import com.example.campustask.model.Task
import com.example.campustask.repository.TaskRepository

class MyTaskFragment : Fragment(R.layout.fragment_task) {

    private lateinit var adapter: MyTaskAdapter
    private var allList= mutableListOf<Task>()
    private val TAG="MyTasksFragment"

    private var defaultStatus = "OPEN"

    companion object {
        private const val KEY_STATUS = "status"

        fun newInstance(status: String): MyTaskFragment {
            val fragment = MyTaskFragment()
            val bundle = Bundle()
            bundle.putString(KEY_STATUS, status)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultStatus = "OPEN"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_task)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = MyTaskAdapter(allList) { task ->
            val fragment = MyTaskDetailFragment.newInstance(task)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        recycler.adapter = adapter // 绑定到 RecyclerView

//        allList = FakeTaskDatabase.getAllTasks()

//        adapter = MyTaskAdapter(allList) { task ->
//            val fragment = MyTaskDetailFragment.newInstance(task)
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .addToBackStack(null)
//                .commit()
//        }

        // 调用后端API
        Log.d(TAG,"调用后端API")
        TaskRepository().getMyTaskHistory(requireContext()) { success, tasks, error ->
            if (success && tasks != null) {
                allList.clear()
                allList.addAll(tasks)

                Log.d(TAG,"Tasks: "+tasks)

                updateStatistics(allList)
                filter(defaultStatus)

                recycler.adapter = adapter
            } else {
                Toast.makeText(requireContext(), error ?: "加载失败", Toast.LENGTH_SHORT).show()
            }
        }

        Log.d(TAG, "Current defaultStatus: $defaultStatus")

        //recycler.adapter = adapter

        initTab(view)

        // ===== 设置默认选中 tab =====
        val tabAll = view.findViewById<TextView>(R.id.tab_all)
        val tabPublish = view.findViewById<TextView>(R.id.tab_publish)
        val tabIng = view.findViewById<TextView>(R.id.tab_ing)
        val tabDone = view.findViewById<TextView>(R.id.tab_done)

        when (defaultStatus) {
            "OPEN" -> selectTab(view, tabPublish)
            "IN_PROGRESS" -> selectTab(view, tabIng)
            "FINISHED" -> selectTab(view, tabDone)
            else -> selectTab(view, tabAll)
        }

        // ===== 默认筛选 =====
        filter(defaultStatus)
    }

    private fun initTab(view: View) {
        val tabPublish = view.findViewById<TextView>(R.id.tab_publish)
        val tabIng = view.findViewById<TextView>(R.id.tab_ing)
        val tabDone = view.findViewById<TextView>(R.id.tab_done)
        val tabAll = view.findViewById<TextView>(R.id.tab_all)

        tabAll?.setOnClickListener {
            selectTab(view, tabAll)
            filter("ALL")
        }

        tabPublish?.setOnClickListener {
            selectTab(view, tabPublish)
            filter("OPEN")
        }

        tabIng?.setOnClickListener {
            selectTab(view, tabIng)
            filter("IN_PROGRESS")
        }

        tabDone?.setOnClickListener {
            selectTab(view, tabDone)
            filter("FINISHED")
        }
    }

    private fun filter(status: String) {
        val data = if (status == "ALL") {
            //FakeTaskDatabase.getAllTasks()
            allList
        } else {
            //FakeTaskDatabase.getTasksByStatus(status)
            allList.filter { it.status==status }
        }
        if (::adapter.isInitialized) {
            adapter.update(data)
        }
    }

    private fun selectTab(view: View, selected: TextView?) {
        val tabs = listOf(
            view.findViewById<TextView>(R.id.tab_all),
            view.findViewById<TextView>(R.id.tab_publish),
            view.findViewById<TextView>(R.id.tab_ing),
            view.findViewById<TextView>(R.id.tab_done)
        )

        tabs.forEach {
            it?.setBackgroundResource(R.drawable.bg_tab)
        }

        Log.d(TAG,"TabSelected: "+selected?.toString())
        selected?.setBackgroundResource(R.drawable.bg_tab_selected)
    }

    private fun updateStatistics(tasks: List<Task>) {
        // 1. 分类计数
        val publishCount = tasks.count { it.status == "OPEN" }
        val ingCount = tasks.count { it.status == "IN_PROGRESS" }
        val doneCount = tasks.count { it.status == "FINISHED" }

        // 2. 更新 UI（确保在主线程执行，Repository 回调通常在主线程）
        view?.let { v ->
            v.findViewById<TextView>(R.id.published_count).text = publishCount.toString()
            v.findViewById<TextView>(R.id.in_progress_count).text = ingCount.toString()
            v.findViewById<TextView>(R.id.finished_count).text = doneCount.toString()
        }
    }
}