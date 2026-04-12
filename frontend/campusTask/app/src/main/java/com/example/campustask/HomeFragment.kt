package com.example.campustask

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.model.Task

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var adapter: TaskAdapter
    private val allTasks = listOf(
        Task("王者荣耀开黑", "1/3人", "20积分", "今晚8点", "游戏", "publish"),
        Task("英雄联盟组队", "2/4人", "25积分", "今晚9点", "游戏", "publish"),
        Task("帮忙带饭", "0/1人", "15积分", "中午", "生活", "publish")
    )

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//
//        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
//        val tabContainer = view.findViewById<LinearLayout>(R.id.tab_container)
//
//        adapter = TaskAdapter(allTasks)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//
//        val tabs = listOf("全部", "游戏", "生活", "学习")
//
//        tabs.forEachIndexed { index, text ->
//            val tv = TextView(requireContext())
//            tv.text = text
//            tv.setPadding(30, 15, 30, 15)
//            tv.setBackgroundResource(R.drawable.bg_tag)
//
//            if (index == 0) tv.setBackgroundResource(R.drawable.bg_tag_selected)
//
//            tv.setOnClickListener {
//                updateTab(tabContainer, tv)
//                filter(text)
//            }
//
//            tabContainer.addView(tv)
//        }
//    }

    private fun selectTab(selected: TextView, tabs: List<TextView>) {
        for (tv in tabs) {
            tv.setBackgroundResource(R.drawable.bg_tag)
        }
        selected.setBackgroundResource(R.drawable.bg_tag_selected)
    }
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

    val tabAll = view.findViewById<TextView>(R.id.tab_all)
    val tabGame = view.findViewById<TextView>(R.id.tab_game)
    val tabLife = view.findViewById<TextView>(R.id.tab_life)
    val tabStudy = view.findViewById<TextView>(R.id.tab_study)

    val tabs = listOf(tabAll, tabGame, tabLife, tabStudy)

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
        if (category == "全部") {
            adapter.update(allTasks)
        } else {
            adapter.update(allTasks.filter { it.category == category })
        }
    }

    private fun updateTab(container: LinearLayout, selected: TextView) {
        for (i in 0 until container.childCount) {
            val tv = container.getChildAt(i) as TextView
            tv.setBackgroundResource(R.drawable.bg_tag)
        }
        selected.setBackgroundResource(R.drawable.bg_tag_selected)
    }
}