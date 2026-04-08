package com.example.campustask

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class TaskDetailFragment : Fragment(R.layout.fragment_task_detail) {

    companion object {
        fun newInstance(task: Task): TaskDetailFragment {
            val fragment = TaskDetailFragment()
            val bundle = Bundle()

            bundle.putString("title", task.title)
            bundle.putString("people", task.people)
            bundle.putString("score", task.score)
            bundle.putString("time", task.time)

            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ===== 获取数据 =====
        val title = arguments?.getString("title") ?: ""
        val people = arguments?.getString("people") ?: ""
        val score = arguments?.getString("score") ?: ""
        val time = arguments?.getString("time") ?: ""

        // ===== 标题 =====
        view.findViewById<TextView>(R.id.tv_title).text = title

        // ===== 描述（你可以后续动态传）=====
        view.findViewById<TextView>(R.id.tv_desc).text =
            "需要两个队友一起打排位，最好是铂金以上段位，晚上8点开始"

        // ===== 绑定4个信息卡片 =====
        bindCard(view, R.id.card_people, "需要人数", people)
        bindCard(view, R.id.card_score, "积分奖励", "+$score")
        bindCard(view, R.id.card_time, "发布时间", time)
        bindCard(view, R.id.card_deadline, "截止时间", "2026-03-11 20:00")

        // ===== 返回按钮 =====
        view.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * 👉 通用卡片绑定方法（核心！）
     */
    private fun bindCard(view: View, cardId: Int, label: String, value: String) {
        val card = view.findViewById<View>(cardId)

        val tvLabel = card.findViewById<TextView>(R.id.tv_label)
        val tvValue = card.findViewById<TextView>(R.id.tv_value)

        tvLabel.text = label
        tvValue.text = value
    }
}