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

    // 存储所有已加载的数据
    private val allLoadedTasks = mutableListOf<Task>()

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

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // 判断是否滑到了倒数第 3 个（提前一点加载，体验更好）
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3
                        && firstVisibleItemPosition >= 0) {
                        loadData(currentCategory) // 加载下一页
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
            selectTab(tabAll, tabs)
            currentCategory="全部"
            loadData(currentCategory,isRefresh = true)
        }
        tabGame.setOnClickListener {
            selectTab(tabGame, tabs)
            currentCategory="游戏"
            loadData(currentCategory,isRefresh = true)
        }
        tabLife.setOnClickListener {
            selectTab(tabLife, tabs)
            currentCategory="生活"
            loadData(currentCategory,isRefresh = true)
        }
        tabStudy.setOnClickListener {
            selectTab(tabStudy, tabs)
            currentCategory="学习"
            loadData(currentCategory,isRefresh = true)
        }

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
}