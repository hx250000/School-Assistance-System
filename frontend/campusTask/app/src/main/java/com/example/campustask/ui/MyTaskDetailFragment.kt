package com.example.campustask.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.campustask.R
import com.example.campustask.data.FakeTaskDatabase
import com.example.campustask.model.Task
import java.text.SimpleDateFormat
import java.util.*

class MyTaskDetailFragment : Fragment(R.layout.fragment_my_task_detail) {

    companion object {
        fun newInstance(task: Task): MyTaskDetailFragment {
            val fragment = MyTaskDetailFragment()

            val bundle = Bundle().apply {
                putLong("id", task.taskId)
            }

            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val taskId = arguments?.getLong("id") ?: return

        // 👉 这里先用假数据（后面接数据库）
        val task = FakeTaskDatabase
            .getAllTasks()
            .find { it.taskId == taskId } ?: return

        // ===== 标题 =====
        view.findViewById<TextView>(R.id.tv_title).text = task.title

        // ===== 任务ID =====
        view.findViewById<TextView>(R.id.tv_task_id).text = "任务ID: ${task.taskId}"

        // ===== 进度 =====
        val progressText = "${task.currentPeople}/${task.needPeople}"
        view.findViewById<TextView>(R.id.tv_progress).text = progressText

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.progress = (task.currentPeople * 100 / task.needPeople)

        // ===== 信息卡片 =====
        view.findViewById<TextView>(R.id.tv_people).text = progressText
        view.findViewById<TextView>(R.id.tv_score).text = "+${task.rewardPoints}积分"

        view.findViewById<TextView>(R.id.tv_time).text = formatTime(task.deadline)

        view.findViewById<TextView>(R.id.tv_deadline).text = formatTime(task.deadline)

        // ===== 按钮 =====
        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<Button>(R.id.btn_finish).setOnClickListener {
            Toast.makeText(requireContext(), "任务完成", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            Toast.makeText(requireContext(), "任务取消", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatTime(time: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(time))
    }
}