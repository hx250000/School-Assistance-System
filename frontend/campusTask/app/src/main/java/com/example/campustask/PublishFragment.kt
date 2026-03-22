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

        val btnLife = view.findViewById<Button>(R.id.btn_life)
        val btnGame = view.findViewById<Button>(R.id.btn_game)
        val btnStudy = view.findViewById<Button>(R.id.btn_study)

        // 👉 类型选择逻辑
        val buttons = listOf(btnLife, btnGame, btnStudy)

        fun select(btn: Button, type: String) {
            selectedType = type
            buttons.forEach { it.setBackgroundResource(android.R.drawable.btn_default) }
            btn.setBackgroundResource(R.drawable.bg_type_selected)
        }

        btnLife.setOnClickListener { select(btnLife, "生活") }
        btnGame.setOnClickListener { select(btnGame, "游戏") }
        btnStudy.setOnClickListener { select(btnStudy, "学习") }

        // 👉 发布按钮
        btnPublish.setOnClickListener {
            val t = title.text.toString()
            val d = desc.text.toString()

            if (t.isEmpty() || d.isEmpty()) {
                Toast.makeText(context, "请填写完整信息", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "发布成功：$selectedType - $t",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }
}