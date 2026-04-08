package com.example.campustask

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class MyTaskDetailFragment : Fragment(R.layout.fragment_my_task_detail) {

    companion object {
        fun newInstance(task: Task): MyTaskDetailFragment {
            val fragment = MyTaskDetailFragment()
            val bundle = Bundle().apply {
                putString("title", task.title)
                putString("people", task.people)
                putString("score", task.score)
                putString("time", task.time)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ===== 获取参数 =====
        val title = arguments?.getString("title") ?: ""
        val people = arguments?.getString("people") ?: ""
        val score = arguments?.getString("score") ?: ""
        val time = arguments?.getString("time") ?: ""

        // ===== 顶部显示 =====
        view.findViewById<TextView>(R.id.tv_title).text = title
        view.findViewById<TextView>(R.id.tv_task_id).text = "任务ID: 101"

        // ===== 进度条 =====
        view.findViewById<TextView>(R.id.tv_progress).text = people
        view.findViewById<ProgressBar>(R.id.progress_bar).progress = 66

        // ===== 信息卡片 =====
        view.findViewById<TextView>(R.id.tv_people).text = people
        view.findViewById<TextView>(R.id.tv_score).text = score
        view.findViewById<TextView>(R.id.tv_time).text = time
        view.findViewById<TextView>(R.id.tv_deadline).text = "2026-03-20 19:00"

        // ===== 顶部操作按钮 =====
        view.findViewById<ImageView>(R.id.btn_edit).setOnClickListener {
            Toast.makeText(requireContext(), "编辑任务", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<ImageView>(R.id.btn_delete).setOnClickListener {
            Toast.makeText(requireContext(), "删除任务", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // ===== 底部按钮 =====
        view.findViewById<Button>(R.id.btn_finish).setOnClickListener {
            Toast.makeText(requireContext(), "任务已完成", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            Toast.makeText(requireContext(), "任务已取消", Toast.LENGTH_SHORT).show()
        }
    }
}