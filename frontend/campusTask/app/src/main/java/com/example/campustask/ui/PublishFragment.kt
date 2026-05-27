package com.example.campustask.ui

import android.R.attr.type
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.campustask.R
import com.example.campustask.model.request.AiGenerateRequest
import com.example.campustask.model.request.TaskCreateRequest
import com.example.campustask.repository.AiRepository
import com.example.campustask.repository.TaskRepository
import com.example.campustask.util.CategoryMapper
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class PublishFragment : Fragment() {

    private var selectedType = "生活"
    private val taskRepository = TaskRepository()
    private val aiRepository = AiRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_publish, container, false)

        val title = view.findViewById<EditText>(R.id.et_title)
        val desc = view.findViewById<EditText>(R.id.et_desc)
        val btnPublish = view.findViewById<Button>(R.id.btn_publish)
        val btnAiDesc = view.findViewById<TextView>(R.id.btn_ai_desc)
        val etScore = view.findViewById<EditText>(R.id.et_score)
        val etPeople = view.findViewById<EditText>(R.id.et_people)
        val etDeadline = view.findViewById<TextView>(R.id.et_deadline)

        // ===== 时间选择器 =====
        etDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()

            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->

                    TimePickerDialog(
                        requireContext(),
                        { _, hourOfDay, minute ->

                            val result = String.format(
                                "%04d-%02d-%02d %02d:%02d:00",
                                year, month + 1, dayOfMonth, hourOfDay, minute
                            )

                            etDeadline.text = result
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // ===== 类型选择 =====
        val type1 = view.findViewById<TextView>(R.id.type1)
        val type2 = view.findViewById<TextView>(R.id.type2)
        val type3 = view.findViewById<TextView>(R.id.type3)
        val types = listOf(type1, type2, type3)

        fun selectType(selected: TextView, type: String) {
            selectedType = type
            types.forEach {
                it.setBackgroundResource(R.drawable.bg_tag)
                it.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
            selected.setBackgroundResource(R.drawable.bg_tag_selected)
            selected.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        }

        type1.setOnClickListener { selectType(type1, "生活") }
        type2.setOnClickListener { selectType(type2, "学习") }
        type3.setOnClickListener { selectType(type3, "游戏") }
        selectType(type1, "生活")

        // ===== 人数 =====
        view.findViewById<Button>(R.id.btn_add).setOnClickListener {
            val num = etPeople.text.toString().toIntOrNull() ?: 1
            etPeople.setText((num + 1).toString())
        }

        view.findViewById<Button>(R.id.btn_minus).setOnClickListener {
            val num = etPeople.text.toString().toIntOrNull() ?: 1
            if (num > 1) etPeople.setText((num - 1).toString())
        }

        // ===== 发布 =====
        btnPublish.setOnClickListener {

            val t = title.text.toString().trim()
            val d = desc.text.toString().trim()
            val people = etPeople.text.toString().toIntOrNull() ?: 1
            val score = etScore.text.toString().toIntOrNull() ?: 0
            val deadline = etDeadline.text.toString()

            if (t.isEmpty() || d.isEmpty() || deadline == "请选择截止时间") {
                Toast.makeText(context, "请填写完整信息", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = TaskCreateRequest(
                title = t,
                description = d,
                type = CategoryMapper.toType(selectedType),
                needPeople = people,
                rewardPoints = score,
                deadline = deadline
            )

            taskRepository.createTask(requireContext(), request) { success, taskId, error ->
                if (success) {
                    Toast.makeText(context, "发布成功！", Toast.LENGTH_LONG).show()
                    activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
                        ?.selectedItemId = R.id.nav_home
                } else {
                    Toast.makeText(context, "失败: $error", Toast.LENGTH_LONG).show()
                }
            }
        }

        // AI
        btnAiDesc.setOnClickListener {
            val t = title.text.toString().trim()
            val aiRequest= AiGenerateRequest(t,selectedType)
            aiRepository.generateDesc(requireContext(),aiRequest){success, aiResponse, error ->
                if (success) {
                    Toast.makeText(context, "AI生成成功！", Toast.LENGTH_LONG).show()
                    desc.setText(aiResponse?.description)
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }

            }
        }

        return view
    }
}