package com.example.campustask.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.adapter.MyTaskAdapter
import com.example.campustask.model.Task
import com.example.campustask.repository.TaskRepository

class MyTaskFragment : Fragment(R.layout.fragment_task) {

    private lateinit var adapter: MyTaskAdapter
    private var myPublishedList = mutableListOf<Task>()
    private var myJoinedList = mutableListOf<Task>()

    private val TAG = "MyTasksFragment"

    // 默认显示“我参与”
    private var defaultStatus = "JOINED"

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

        defaultStatus = arguments?.getString(KEY_STATUS) ?: "JOINED"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_task)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = MyTaskAdapter(myPublishedList) { task ->

            val isJoinedTask = defaultStatus == "JOINED"
            val fragment = MyTaskDetailFragment.newInstance(task, isJoinedTask)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recycler.adapter = adapter

        // 搜索框
        val etSearch = view.findViewById<EditText>(R.id.et_search)

        etSearch.setOnEditorActionListener { v, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val keyword = etSearch.text.toString().trim()

                if (keyword.isNotEmpty()) {
                    performSearch(view, keyword)
                } else {
                    filter(defaultStatus)
                }

                // 收起键盘
                val imm =
                    requireContext().getSystemService(InputMethodManager::class.java)

                imm.hideSoftInputFromWindow(v.windowToken, 0)

                true
            } else {
                false
            }
        }

        // 初始化 tab
        initTab(view)

        // 加载数据
        Log.d(TAG, "调用后端API")

        loadTasks()

        // 默认选中 tab
        val tabJoined = view.findViewById<TextView>(R.id.tab_joined)
        val tabPublish = view.findViewById<TextView>(R.id.tab_publish)
        val tabIng = view.findViewById<TextView>(R.id.tab_ing)
        val tabDone = view.findViewById<TextView>(R.id.tab_done)

        when (defaultStatus) {

            "JOINED" -> {
                selectTab(view, tabJoined)
            }

            "OPEN" -> {
                selectTab(view, tabPublish)
            }

            "IN_PROGRESS" -> {
                selectTab(view, tabIng)
            }

            "FINISHED" -> {
                selectTab(view, tabDone)
            }
        }
    }

    /**
     * 初始化 Tab 点击
     */
    private fun initTab(view: View) {

        val tabJoined = view.findViewById<TextView>(R.id.tab_joined)
        val tabPublish = view.findViewById<TextView>(R.id.tab_publish)
        val tabIng = view.findViewById<TextView>(R.id.tab_ing)
        val tabDone = view.findViewById<TextView>(R.id.tab_done)

        // 我参与
        tabJoined?.setOnClickListener {

            defaultStatus = "JOINED"

            selectTab(view, tabJoined)

            filter("JOINED")
        }

        // 已发布
        tabPublish?.setOnClickListener {

            defaultStatus = "OPEN"

            selectTab(view, tabPublish)

            filter("OPEN")
        }

        // 进行中
        tabIng?.setOnClickListener {

            defaultStatus = "IN_PROGRESS"

            selectTab(view, tabIng)

            filter("IN_PROGRESS")
        }

        // 已完成
        tabDone?.setOnClickListener {

            defaultStatus = "FINISHED"

            selectTab(view, tabDone)

            filter("FINISHED")
        }
    }

    /**
     * 获取任务信息
     */
    private fun loadTasks(){
        TaskRepository().getMyPublishedTaskHistory(requireContext()) { success, tasks, error ->

            if (success && tasks != null) {

                myPublishedList.clear()
                myPublishedList.addAll(tasks)
                Log.d(TAG, "PublishedTasks: $tasks")
                updateStatistics(myPublishedList)
                // 默认筛选
                filter(defaultStatus)

            } else {
                Toast.makeText(
                    requireContext(),
                    error ?: "加载失败",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        TaskRepository().getMyJoinedTaskHistory(requireContext()) { success, tasks, error ->

            if (success && tasks != null) {
                myJoinedList.clear()
                myJoinedList.addAll(tasks)
                Log.d(TAG, "JoinedTasks: $tasks")
            } else {
                Toast.makeText(
                    requireContext(),
                    error ?: "加载失败",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    /**
     * 根据状态筛选
     */
    private fun filter(status: String) {

        val data = when (status) {

            // 我参与
            "JOINED" -> {
                myJoinedList
            }

            // 已发布
            "OPEN" -> {
                myPublishedList.filter {
                    it.status == "OPEN"
                }
            }

            // 进行中
            "IN_PROGRESS" -> {
                myPublishedList.filter {
                    it.status == "IN_PROGRESS"
                }
            }

            // 已完成
            "FINISHED" -> {
                myPublishedList.filter {
                    it.status == "FINISHED"
                }
            }

            else -> {
                myPublishedList
            }
        }

        if (::adapter.isInitialized) {
            adapter.update(data)
        }
    }

    /**
     * 选中 Tab
     */
    private fun selectTab(view: View, selected: TextView?) {

        // 清空搜索框
        val etSearch = view.findViewById<EditText>(R.id.et_search)

        etSearch.text.clear()
        etSearch.clearFocus()

        val tabs = listOf(
            view.findViewById<TextView>(R.id.tab_joined),
            view.findViewById<TextView>(R.id.tab_publish),
            view.findViewById<TextView>(R.id.tab_ing),
            view.findViewById<TextView>(R.id.tab_done)
        )

        // 全部恢复默认背景
        tabs.forEach {
            it?.setBackgroundResource(R.drawable.bg_tab)
        }

        // 当前选中
        selected?.setBackgroundResource(R.drawable.bg_tab_selected)

        Log.d(TAG, "TabSelected: $selected")
    }

    /**
     * 更新顶部统计
     */
    private fun updateStatistics(tasks: List<Task>) {

        val publishCount = tasks.count {
            it.status == "OPEN"
        }

        val ingCount = tasks.count {
            it.status == "IN_PROGRESS"
        }

        val doneCount = tasks.count {
            it.status == "FINISHED"
        }

        view?.let { v ->
            v.findViewById<TextView>(R.id.published_count).text =
                publishCount.toString()

            v.findViewById<TextView>(R.id.in_progress_count).text =
                ingCount.toString()

            v.findViewById<TextView>(R.id.finished_count).text =
                doneCount.toString()
        }
    }

    /**
     * 搜索
     */
    private fun performSearch(view: View, keyword: String) {
        val sourceList = when (defaultStatus) {
            "JOINED" -> myJoinedList
            else -> myPublishedList
        }

        val tabs = listOf(
            view.findViewById<TextView>(R.id.tab_joined),
            view.findViewById<TextView>(R.id.tab_publish),
            view.findViewById<TextView>(R.id.tab_ing),
            view.findViewById<TextView>(R.id.tab_done)
        )

        // 搜索时取消 tab 高亮
        tabs.forEach {
            it?.setBackgroundResource(R.drawable.bg_tab)
        }

        if (keyword.isEmpty()) {

            filter(defaultStatus)

            return
        }

        // 本地模糊搜索
        val filteredList = sourceList.filter { task ->

            task.title.contains(keyword, ignoreCase = true)
                    || task.description.contains(keyword, ignoreCase = true)
        }

        adapter.update(filteredList)

        if (filteredList.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "未找到匹配任务",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}