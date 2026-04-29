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

    private var defaultStatus = "ALL"

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
        defaultStatus = arguments?.getString(KEY_STATUS) ?: "ALL"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_task)
        recycler.layoutManager = LinearLayoutManager(requireContext())

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
            FakeTaskDatabase.getAllTasks()
        } else {
            FakeTaskDatabase.getTasksByStatus(status)
        }
        adapter.update(data)
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

        selected?.setBackgroundResource(R.drawable.bg_tab_selected)
    }
}