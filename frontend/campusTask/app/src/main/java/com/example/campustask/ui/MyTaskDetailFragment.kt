package com.example.campustask.ui

import android.os.Bundle
import android.util.Log
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
import com.example.campustask.repository.TaskRepository
import java.text.SimpleDateFormat
import java.util.*

class MyTaskDetailFragment : Fragment(R.layout.fragment_my_task_detail) {

    val TAG="MyTaskDetailFragment"

    companion object {
        private const val ARG_TASK_ID = "task_id"
        private const val ARG_TASK_TITLE = "task_title"
        private const val ARG_TASK_DESC = "task_desc"
        private const val ARG_TASK_TYPE = "task_type"
        private const val ARG_TASK_PUBLISHER_ID = "task_publisher_id"
        private const val ARG_TASK_PUBLISHER_NAME = "task_publisher_name"
        private const val ARG_TASK_NEED_PEOPLE = "task_need_people"
        private const val ARG_TASK_CURRENT_PEOPLE = "task_current_people"
        private const val ARG_TASK_REWARD_POINTS = "task_reward_points"
        private const val ARG_TASK_REWARD_MONEY = "task_reward_money"
        private const val ARG_TASK_STATUS = "task_status"
        private const val ARG_TASK_DEADLINE = "task_deadline"
        private const val ARG_TASK_CREATED_AT = "task_created_at"

        fun newInstance(task: Task): MyTaskDetailFragment {
            val fragment = MyTaskDetailFragment()

            val bundle = Bundle().apply {
                putLong(ARG_TASK_ID, task.taskId)
                putString(ARG_TASK_TITLE, task.title)
                putString(ARG_TASK_DESC, task.description)
                putString(ARG_TASK_TYPE, task.type)
                putLong(ARG_TASK_PUBLISHER_ID, task.publisherId)
                putString(ARG_TASK_PUBLISHER_NAME, task.publisherName)
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

        val task = parseTaskFromArgs() ?: run {
            parentFragmentManager.popBackStack()
            return
        }

        // ===== 标题 =====
        view.findViewById<TextView>(R.id.tv_title)?.text = task.title

        // ===== 任务ID =====
        view.findViewById<TextView>(R.id.tv_task_id)?.text = "任务ID: ${task.taskId}"

        // ===== 进度 =====
        val progressText = "${task.currentPeople}/${task.needPeople}"
        view.findViewById<TextView>(R.id.tv_progress)?.text = progressText

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        progressBar?.progress = if (task.needPeople > 0) {
            task.currentPeople * 100 / task.needPeople
        } else 0

        // ===== 信息卡片 =====
        view.findViewById<TextView>(R.id.tv_people)?.text = progressText
        view.findViewById<TextView>(R.id.tv_score)?.text = "+${task.rewardPoints}积分"

        view.findViewById<TextView>(R.id.tv_time)?.text = formatTime(task.deadline)

        view.findViewById<TextView>(R.id.tv_deadline)?.text = formatTime(task.createdAt)

        // ===== 按钮 =====
        view.findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 完成任务按钮
        view.findViewById<Button>(R.id.btn_finish)?.setOnClickListener { btn ->
            btn.isEnabled = false
            TaskRepository().finishTask(requireContext(), task.taskId) { success, msg ->
                if (!isAdded) return@finishTask
                requireActivity().runOnUiThread {
                    btn.isEnabled = true
                    if (success) {
                        Log.d(TAG,msg?:"success")
                        Toast.makeText(requireContext(), "任务已完成", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Log.d(TAG,msg?:"error")
                        Toast.makeText(requireContext(), msg ?: "完成任务失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 取消任务按钮
        view.findViewById<Button>(R.id.btn_cancel)?.setOnClickListener { btn ->
            btn.isEnabled = false
            TaskRepository().cancelTask(requireContext(), task.taskId) { success, msg ->
                if (!isAdded) return@cancelTask
                requireActivity().runOnUiThread {
                    btn.isEnabled = true
                    if (success) {
                        Toast.makeText(requireContext(), "任务已取消", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Log.d(TAG,msg?:"取消任务失败")
                        Toast.makeText(requireContext(), msg ?: "取消任务失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun parseTaskFromArgs(): Task? {
        val args = arguments ?: return null
        if (!args.containsKey(ARG_TASK_TITLE)) return null

        return Task(
            taskId = args.getLong(ARG_TASK_ID),
            title = args.getString(ARG_TASK_TITLE).orEmpty(),
            description = args.getString(ARG_TASK_DESC).orEmpty(),
            type = args.getString(ARG_TASK_TYPE).orEmpty(),
            publisherId = args.getLong(ARG_TASK_PUBLISHER_ID),
            publisherName = args.getString(ARG_TASK_PUBLISHER_NAME).orEmpty(),
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