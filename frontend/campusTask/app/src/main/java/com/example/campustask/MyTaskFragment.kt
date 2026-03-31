package com.example.campustask

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyTaskFragment : Fragment(R.layout.fragment_task) {

    private lateinit var adapter: TaskAdapter
    private lateinit var allList: List<Task>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_task)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // 👉 模拟数据
        allList = listOf(
            Task("取快递", "3人已接", "5积分", "今天", "跑腿", "publish"),
            Task("代写作业", "1人已接", "10积分", "明天", "学习", "ing"),
            Task("买早餐", "2人已接", "3积分", "已结束", "生活", "done")
        )

        // ✅ 这里是关键修改（加点击事件）
        adapter = TaskAdapter(allList) { task ->

            val fragment = TaskDetailFragment.newInstance(task)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recycler.adapter = adapter

        initTab(view)
    }

    private fun initTab(view: View) {
        val tabPublish = view.findViewById<TextView>(R.id.tab_publish)
        val tabIng = view.findViewById<TextView>(R.id.tab_ing)
        val tabDone = view.findViewById<TextView>(R.id.tab_done)

        tabPublish.setOnClickListener {
            filter("publish")
            selectTab(view, tabPublish)
        }

        tabIng.setOnClickListener {
            filter("ing")
            selectTab(view, tabIng)
        }

        tabDone.setOnClickListener {
            filter("done")
            selectTab(view, tabDone)
        }
    }

    private fun filter(status: String) {
        val newList = allList.filter { it.status == status }
        adapter.update(newList)
    }

    private fun selectTab(view: View, selected: TextView) {
        val tabs = listOf(
            view.findViewById<TextView>(R.id.tab_publish),
            view.findViewById<TextView>(R.id.tab_ing),
            view.findViewById<TextView>(R.id.tab_done)
        )

        tabs.forEach {
            it.setBackgroundResource(R.drawable.bg_tab)
        }

        selected.setBackgroundResource(R.drawable.bg_tab_selected)
    }
}