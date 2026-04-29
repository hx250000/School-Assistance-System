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
        private const val ARG_TASK_ID = "task_id"
        private const val ARG_TASK_TITLE = "task_title"
        private const val ARG_TASK_DESC = "task_desc"
        private const val ARG_TASK_TYPE = "task_type"
        private const val ARG_TASK_PUBLISHER_ID = "task_publisher_id"
        private const val ARG_TASK_NEED_PEOPLE = "task_need_people"
        private const val ARG_TASK_CURRENT_PEOPLE = "task_current_people"
        private const val ARG_TASK_REWARD_POINTS = "task_reward_points"
        private const val ARG_TASK_REWARD_MONEY = "task_reward_money"
        private const val ARG_TASK_STATUS = "task_status"
        private const val ARG_TASK_DEADLINE = "task_deadline"
        private const val ARG_TASK_CREATED_AT = "task_created_at"

        fun newInstance(task: Task): TaskDetailFragment {
            val fragment = TaskDetailFragment()
            val bundle = Bundle().apply {
                putLong(ARG_TASK_ID, task.id)
                putString(ARG_TASK_TITLE, task.title)
                putString(ARG_TASK_DESC, task.description)
                putString(ARG_TASK_TYPE, task.type)
                putLong(ARG_TASK_PUBLISHER_ID, task.publisherId)
                putInt(ARG_TASK_NEED_PEOPLE, task.needPeople)
                putInt(ARG_TASK_CURRENT_PEOPLE, task.currentPeople)
                putInt(ARG_TASK_REWARD_POINTS, task.rewardPoints)
                task.rewardMoney?.let { putDouble(ARG_TASK_REWARD_MONEY, it) }
                putString(ARG_TASK_STATUS, task.status)
                putLong(ARG_TASK_DEADLINE, task.deadline)
                putLong(ARG_TASK_CREATED_AT, task.createdAt)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 优先使用 Bundle 中完整任务数据；兼容历史逻辑按 id 从 FakeTaskDatabase 回查
        val task = parseTaskFromArgs() ?: run {
            val taskId = arguments?.getLong(ARG_TASK_ID) ?: run {
                parentFragmentManager.popBackStack()
                return
            }
            FakeTaskDatabase.getAllTasks().find { it.id == taskId }
        } ?: run {
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

    //从Bundle中获取数据组装Task
    private fun parseTaskFromArgs(): Task? {
        val args = arguments ?: return null
        if (!args.containsKey(ARG_TASK_TITLE)) return null

        return Task(
            id = args.getLong(ARG_TASK_ID),
            title = args.getString(ARG_TASK_TITLE).orEmpty(),
            description = args.getString(ARG_TASK_DESC).orEmpty(),
            type = args.getString(ARG_TASK_TYPE).orEmpty(),
            publisherId = args.getLong(ARG_TASK_PUBLISHER_ID),
            needPeople = args.getInt(ARG_TASK_NEED_PEOPLE),
            currentPeople = args.getInt(ARG_TASK_CURRENT_PEOPLE),
            rewardPoints = args.getInt(ARG_TASK_REWARD_POINTS),
            rewardMoney = if (args.containsKey(ARG_TASK_REWARD_MONEY)) {
                args.getDouble(ARG_TASK_REWARD_MONEY)
            } else {
                null
            },
            status = args.getString(ARG_TASK_STATUS).orEmpty(),
            deadline = args.getLong(ARG_TASK_DEADLINE),
            createdAt = args.getLong(ARG_TASK_CREATED_AT)
        )
    }

    private fun formatTime(time: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(time))
    }
}