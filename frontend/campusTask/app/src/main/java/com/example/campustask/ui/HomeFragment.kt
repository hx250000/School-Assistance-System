package com.example.campustask.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.adapter.TaskAdapter
import com.example.campustask.model.Task
import com.example.campustask.repository.TaskRepository
import com.example.campustask.util.CategoryMapper

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var adapter: TaskAdapter

    val TAG="HomeFragment"

    val taskRepo = TaskRepository()

    private var currentPage = 0    // 当前页码
    private val pageSize = 20      // 每页数量
    private var isLoading = false  // 是否正在请求中
    private var isStatsLoading = false  // 顶部统计是否正在请求中
    private var isLastPage = false // 后端是否已经没有更多数据了
    private var currentCategory = "全部" // 记录当前分类
    private var isSearchMode = false // 搜索状态

    // 存储所有已加载的数据
    private val allLoadedTasks = mutableListOf<Task>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val et_search=view.findViewById<EditText>(R.id.et_search)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val tabAll = view.findViewById<TextView>(R.id.tab_all)
        val tabGame = view.findViewById<TextView>(R.id.tab_game)
        val tabLife = view.findViewById<TextView>(R.id.tab_life)
        val tabStudy = view.findViewById<TextView>(R.id.tab_study)

        val tabs = listOf(tabAll, tabGame, tabLife, tabStudy)

        // 获取Mock数据
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

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // 增加 !isSearchMode 判断
                if (!isLoading && !isLastPage && !isSearchMode) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3
                        && firstVisibleItemPosition >= 0) {
                        loadData(currentCategory)
                    }
                }
            }
        })

        // 默认选中 / 恢复当前分类
        currentCategory = currentCategory.ifBlank { "全部" }
        val selectedTab = when (currentCategory) {
            "游戏" -> tabGame
            "生活" -> tabLife
            "学习" -> tabStudy
            else -> tabAll
        }
        selectTab(selectedTab, tabs)

        if (allLoadedTasks.isNotEmpty()) {
            adapter.update(allLoadedTasks)
            // 列表复用缓存，但顶部统计仍需要刷新
            refreshHeadStats()
        } else {
            loadData(currentCategory, isRefresh = true)
        }

        // 点击事件
        tabAll.setOnClickListener {
            et_search.text.clear()
            selectTab(tabAll, tabs)
            currentCategory="全部"
            loadData(currentCategory,isRefresh = true)
        }
        tabGame.setOnClickListener {
            et_search.text.clear()
            selectTab(tabGame, tabs)
            currentCategory="游戏"
            loadData(currentCategory,isRefresh = true)
        }
        tabLife.setOnClickListener {
            et_search.text.clear()
            selectTab(tabLife, tabs)
            currentCategory="生活"
            loadData(currentCategory,isRefresh = true)
        }
        tabStudy.setOnClickListener {
            et_search.text.clear()
            selectTab(tabStudy, tabs)
            currentCategory="学习"
            loadData(currentCategory,isRefresh = true)
        }

        et_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val keyword = et_search.text.toString().trim()

                if (keyword.isNotEmpty()) {
                    performSearch(view, keyword)
                } else {
                    // 如果关键词为空，重新拉取首页数据（恢复全量模式）
                    isSearchMode = false
                    loadData(currentCategory, isRefresh = true)
                }

                val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }
        et_search.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // 当用户点击其他可点击控件（如 Tab、RecyclerView）导致搜索框失去焦点时
                if (et_search.text.isEmpty() && isSearchMode) {
                    exitSearchMode()
                }
            }
        }
        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 如果文字被清空，且当前处于搜索模式，则自动切回全量模式
                if (s.isNullOrEmpty() && isSearchMode) {
                    exitSearchMode()
                }
            }
        })

        // 接收抢单成功结果：刷新列表与顶部统计
        parentFragmentManager.setFragmentResultListener(
            TaskDetailFragment.GRAB_RESULT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val success = bundle.getBoolean("success", false)
            if (success) {
                // 强制刷新：防止返回时仍处于加载中导致被 loadData() 拦截
                isLoading = false
                currentPage = 0
                isLastPage = false
                allLoadedTasks.clear()
                adapter.update(emptyList())
                loadData(currentCategory, isRefresh = true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val et_search=view?.findViewById<EditText>(R.id.et_search)
        et_search?.text?.clear()
        et_search?.clearFocus()
        // 返回到 Home 时：即使任务列表已恢复（复用缓存），顶部统计也要重新拉取
        refreshHeadStats()

        if (!isLoading && allLoadedTasks.isEmpty()) {
            loadData(currentCategory, isRefresh = true)
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

    private fun loadData(category: String = "全部", isRefresh: Boolean=false) {
        if(isLoading) return

        // 如果是切换分类或者下拉刷新，重置状态
        if (isRefresh) {
            currentPage = 0
            isLastPage = false
            isSearchMode = false
            allLoadedTasks.clear()
        }

        if (isLastPage) return

        Log.i(TAG, "loadingData, category=$category, currentPage=$currentPage, size=$pageSize")

        isLoading=true

        val safeContext = context ?: run {
            isLoading = false
            return
        }

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
        taskRepo.getAllTasks(safeContext, currentPage,pageSize) { success, tasks, error ->
            if (!isAdded || activity == null) {
                isLoading = false
                return@getAllTasks
            }
            activity?.runOnUiThread {
                isLoading = false
                if (success && tasks != null) {
                    if(tasks.isEmpty()) {
                        isLastPage=true
                    }
                    else {
                        Log.d(TAG,"tasks="+tasks)
                        val filteredData = if (category == "全部") {
                            tasks
                        } else {

                            tasks.filter { it.type == CategoryMapper.toType(category) }
                        }
                        Log.d(TAG,"category="+category+"filtered="+filteredData)
                        allLoadedTasks.addAll(filteredData)
                        adapter.update(allLoadedTasks)
                        currentPage++
                    }
                } else {
                    isLoading=false
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

    private fun refreshHeadStats() {
        if (isStatsLoading) return

        val safeContext = context ?: return
        isStatsLoading = true

        taskRepo.stats(safeContext) { success, data, err ->
            if (!isAdded || activity == null) {
                isStatsLoading = false
                return@stats
            }

            activity?.runOnUiThread {
                isStatsLoading = false
                if (success && data != null) {
                    updateHeadStats(data.inProgress, data.finished, data.users)
                } else {
                    Toast.makeText(safeContext, err ?: "加载失败", Toast.LENGTH_SHORT).show()
                    updateHeadStats(100, 900, 500)
                }
            }
        }
    }

    private fun performSearch(view: View, keyword: String) {
        val safeContext = context ?: return

        // 1. 进入搜索模式，重置状态
        isLoading = true
        isSearchMode = true
        isLastPage = true // 搜索不分页，所以直接封死滑动加载

        // 2. 调用后端搜索接口
        taskRepo.searchTasks(safeContext, keyword) { success, tasks, error ->
            if (!isAdded || activity == null) {
                isLoading = false
                return@searchTasks
            }

            activity?.runOnUiThread {
                isLoading = false
                if (success && tasks != null) {
                    // 3. 搜索成功，直接覆盖 Adapter 数据
                    // 注意：这里不操作 allLoadedTasks，以免破坏全量数据的缓存
                    Log.d(TAG,"[search]tasks: "+tasks)
                    adapter.update(tasks)

                    if (tasks.isEmpty()) {
                        Toast.makeText(safeContext, "未找到相关任务", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 搜索失败处理
                    Toast.makeText(safeContext, error ?: "搜索失败", Toast.LENGTH_SHORT).show()
                    // 如果搜索失败，建议自动刷回全量数据，或者保持空状态
                    isSearchMode = false
                    loadData(currentCategory, isRefresh = true)
                }
            }
        }
    }
    private fun exitSearchMode() {
        // 1. 状态重置
        isSearchMode = false
        isLastPage = false
        currentPage = 0
        allLoadedTasks.clear()

        // 2. 只有在当前 Fragment 处于活动状态时才执行加载
        if (isAdded) {
            loadData(currentCategory, isRefresh = true)
        }
    }
}