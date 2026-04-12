package com.example.campustask

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.model.Achievement

class AchievementFragment : Fragment(R.layout.fragment_achievement) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val fakeData = listOf(
            Achievement("任务达人", "完成10个任务", 10, 10, R.drawable.ic_launcher_foreground),
            Achievement("闪电侠", "1小时完成3个任务", 3, 3, R.drawable.ic_launcher_foreground),
            Achievement("人气王", "100人查看", 100, 100, R.drawable.ic_launcher_foreground),
            Achievement("热心肠", "帮助50人", 23, 50, R.drawable.ic_launcher_foreground),
            Achievement("神枪手", "连续7天完成", 4, 7, R.drawable.ic_launcher_foreground),
            Achievement("校园之星", "获得1000积分", 650, 1000, R.drawable.ic_launcher_foreground)
        )

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = AchievementAdapter(fakeData)
    }
}