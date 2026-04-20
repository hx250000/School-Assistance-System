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

class TaskDetailFragment : Fragment(R.layout.fragment_task_detail) {

    companion object {
        fun newInstance(task: Task): TaskDetailFragment {
            val fragment = TaskDetailFragment()
            val bundle = Bundle().apply {
                putLong("task_id", task.id)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 安全获取 taskId
        val taskId = arguments?.getLong("task_id") ?: run {
            parentFragmentManager.popBackStack()
            return
        }

        // 安全获取任务
        val task = FakeTaskDatabase.getAllTasks()
            .find { it.id == taskId } ?: run {
            parentFragmentManager.popBackStack()
            return
        }

        // 安全绑定控件（全部加空判断，彻底解决空指针）
        view.findViewById<TextView>(R.id.tv_title)?.text = task.title
        view.findViewById<TextView>(R.id.tv_desc)?.text = task.description
        view.findViewById<TextView>(R.id.tv_task_id)?.text = "任务ID: ${task.id}"

        // 进度
        val progressText = "${task.currentPeople}/${task.needPeople}"
        view.findViewById<TextView>(R.id.tv_progress)?.text = progressText

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar?.progress = if (task.needPeople > 0) {
            task.currentPeople * 100 / task.needPeople
        } else 0

        // 卡片信息
        bindCard(view, R.id.card_people, "参与人数", progressText)
        bindCard(view, R.id.card_score, "积分奖励", "+${task.rewardPoints}")
        bindCard(view, R.id.card_time, "截止时间", formatTime(task.deadline))
        bindCard(view, R.id.card_deadline, "创建时间", formatTime(task.createdAt))

        // 返回按钮
        view.findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 操作按钮
        view.findViewById<Button>(R.id.btn_finish)?.setOnClickListener {
            Toast.makeText(requireContext(), "任务完成", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<Button>(R.id.btn_cancel)?.setOnClickListener {
            Toast.makeText(requireContext(), "任务取消", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindCard(view: View, cardId: Int, label: String, value: String) {
        val card = view.findViewById<View>(cardId) ?: return
        card.findViewById<TextView>(R.id.tv_label)?.text = label
        card.findViewById<TextView>(R.id.tv_value)?.text = value
    }

    private fun formatTime(time: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(time))
    }
}