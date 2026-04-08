package com.example.campustask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class PublishFragment : Fragment() {

    private var selectedType = "生活"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_publish, container, false)

        val title = view.findViewById<EditText>(R.id.et_title)
        val desc = view.findViewById<EditText>(R.id.et_desc)
        val btnPublish = view.findViewById<Button>(R.id.btn_publish)

        // 👉 类型（TextView）
        val type1 = view.findViewById<TextView>(R.id.type1)
        val type2 = view.findViewById<TextView>(R.id.type2)
        val type3 = view.findViewById<TextView>(R.id.type3)

        val types = listOf(type1, type2, type3)

        fun selectType(selected: TextView, type: String) {
            selectedType = type

            types.forEach {
                it.setBackgroundResource(R.drawable.bg_tag)
                it.setTextColor(resources.getColor(android.R.color.black))
            }

            selected.setBackgroundResource(R.drawable.bg_tag_selected)
            selected.setTextColor(resources.getColor(android.R.color.white))
        }

        type1.setOnClickListener { selectType(type1, "生活") }
        type2.setOnClickListener { selectType(type2, "学习") }
        type3.setOnClickListener { selectType(type3, "游戏") }

        // 👉 默认选中
        selectType(type1, "生活")

        // 👉 人数选择
        val etPeople = view.findViewById<EditText>(R.id.et_people)
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

        // 👉 发布按钮
        btnPublish.setOnClickListener {
            val t = title.text.toString()
            val d = desc.text.toString()
            val people = etPeople.text.toString()

            if (t.isEmpty() || d.isEmpty()) {
                Toast.makeText(context, "请填写完整信息", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "发布成功：$selectedType - $t（$people 人）",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }
}