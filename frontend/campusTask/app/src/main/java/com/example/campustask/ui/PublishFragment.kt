package com.example.campustask.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.campustask.R
import com.example.campustask.model.request.TaskCreateRequest
import com.example.campustask.repository.TaskRepository
import com.example.campustask.util.CategoryMapper
import com.google.android.material.bottomnavigation.BottomNavigationView

class PublishFragment : Fragment() {

    private var selectedType = "生活"
    private val taskRepository = TaskRepository()  // 添加Repository实例

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_publish, container, false)

        val title = view.findViewById<EditText>(R.id.et_title)
        val desc = view.findViewById<EditText>(R.id.et_desc)
        val btnPublish = view.findViewById<Button>(R.id.btn_publish)
        val etScore = view.findViewById<EditText>(R.id.et_score)
        val etPeople = view.findViewById<EditText>(R.id.et_people)
        val etDeadline = view.findViewById<EditText>(R.id.et_deadline)  // 假设你添加了这个EditText

        // 类型选择逻辑（保持不变）
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

        // 人数增减逻辑（保持不变）
        val btnAdd = view.findViewById<Button>(R.id.btn_add)
        val btnMinus = view.findViewById<Button>(R.id.btn_minus)
        btnAdd.setOnClickListener {
            val num = etPeople.text.toString().toIntOrNull() ?: 1
            etPeople.setText((num + 1).toString())
        }
        btnMinus.setOnClickListener {
            val num = etPeople.text.toString().toIntOrNull() ?: 1
            if (num > 1) {
                etPeople.setText((num - 1).toString())
            }
        }

        // 发布按钮逻辑：调用API
        btnPublish.setOnClickListener {
            val t = title.text.toString().trim()
            val d = desc.text.toString().trim()
            val people = etPeople.text.toString().toIntOrNull() ?: 1
            val score = etScore.text.toString().toIntOrNull() ?: 0
            val deadline = etDeadline.text.toString().trim()  // 获取deadline

            // 校验
            if (t.isEmpty() || d.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(context, "请填写完整信息", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (score <= 0) {
                Toast.makeText(context, "请输入有效积分", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 构建请求
            val request = TaskCreateRequest(
                title = t,
                description = d,
                type = CategoryMapper.toType(selectedType),//selectedType,
                needPeople = people,
                rewardPoints = score,
                deadline = deadline  // 传递deadline
            )

            // 调用Repository
            taskRepository.createTask(requireContext(), request) { success, taskId, error ->
                if (success) {
                    Toast.makeText(context, "发布成功！任务ID: $taskId", Toast.LENGTH_LONG).show()
                    (activity as? MainActivity)?.let {
                        it.findViewById<BottomNavigationView>(R.id.bottom_nav)?.selectedItemId = R.id.nav_home
                    }
                } else {
                    Toast.makeText(context, "发布失败: $error", Toast.LENGTH_LONG).show()
                }
            }
        }

        return view
    }
}
