package com.example.campustask.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.adapter.MyTaskAdapter
import com.example.campustask.data.FakeTaskDatabase
import com.example.campustask.model.Task

class MyTaskFragment : Fragment(R.layout.fragment_task) {

    private lateinit var adapter: MyTaskAdapter
    private lateinit var allList: List<Task>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_task)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // 从“假数据库”获取数据
        allList = FakeTaskDatabase.getAllTasks()

        adapter = MyTaskAdapter(allList) { task ->
            val fragment = MyTaskDetailFragment.newInstance(task)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recycler.adapter = adapter

        initTab(view)

        // 默认选中“全部”
        filter("ALL")
    }

    // ===== tab 初始化（已全部加安全判断，绝不崩溃）=====
    private fun initTab(view: View) {
        val tabPublish = view.findViewById<TextView>(R.id.tab_publish)
        val tabIng = view.findViewById<TextView>(R.id.tab_ing)
        val tabDone = view.findViewById<TextView>(R.id.tab_done)
        val tabAll = view.findViewById<TextView>(R.id.tab_all)

        // 👇 全部加上 ? 安全调用，找不到控件也不会崩
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

    // ===== 筛选逻辑 =====
    private fun filter(status: String) {
        val data = if (status == "ALL") {
            FakeTaskDatabase.getAllTasks()
        } else {
            FakeTaskDatabase.getTasksByStatus(status)
        }
        adapter.update(data)
    }

    // ===== tab UI（全部安全调用）=====
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

        selected?.setBackgroundResource(R.drawable.bg_tab_selected)
    }
}