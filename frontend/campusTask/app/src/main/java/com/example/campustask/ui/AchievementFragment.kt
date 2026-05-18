package com.example.campustask.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campustask.R
import com.example.campustask.adapter.AchievementAdapter
import com.example.campustask.model.Achievement
import com.example.campustask.model.AchievementOverview
import com.example.campustask.repository.AchievementRepository
import java.time.LocalDateTime
import java.time.ZoneOffset

class AchievementFragment : Fragment(R.layout.fragment_achievement) {
    private val TAG="AchievementFragment"

    private val achievementRepository= AchievementRepository()

    private var achievementList=mutableListOf<Achievement>()
    private var myAchievement= AchievementOverview(
        unlockedCount = 0,
        totalCount = 0,
        completionRate = 0.0,
        achievements = achievementList
    )

    private lateinit var achievementAdapter: AchievementAdapter
    private lateinit var tvProgress: TextView
    private lateinit var tvUnlocked: TextView

    /*
       iconUrl:Int=R.drawable.ic_launcher_foreground
        */
    @RequiresApi(Build.VERSION_CODES.O)
    private val fakeData = listOf(
        Achievement(0,"任务达人", "完成10个任务", 10, 10, "R.drawable.ic_launcher_foreground",true,
            0),
        Achievement(1,"闪电侠", "1小时完成3个任务", 3, 3, "R.drawable.ic_launcher_foreground",true,
            0),
        Achievement(2,"人气王", "100人查看", 100, 100, "R.drawable.ic_launcher_foreground",true,
            0),
        Achievement(3,"热心肠", "帮助50人", 23, 50, "R.drawable.ic_launcher_foreground",false,null),
        Achievement(4,"神枪手", "连续7天完成", 4, 7, "R.drawable.ic_launcher_foreground",false,null),
        Achievement(5,"校园之星", "获得1000积分", 650, 1000, "R.drawable.ic_launcher_foreground",false,null)
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvProgress=view.findViewById<TextView>(R.id.tvProgress)
        tvUnlocked=view.findViewById<TextView>(R.id.tvUnlocked)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        achievementAdapter= AchievementAdapter(achievementList)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = achievementAdapter

        fetchData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchData() {

        // 调用 Repository，传入 context 和回调函数
        achievementRepository.getMyAchievements(requireContext()) { success, data, errorMsg ->
            if (success && data != null) {
                // 清空旧数据并添加新数据
                myAchievement=data
                achievementList.clear()
                achievementList.addAll(myAchievement.achievements)
                Log.d(TAG,"Data: "+data.toString())
                updateStats(myAchievement.unlockedCount,
                    myAchievement.totalCount)
                // 通知适配器刷新
                achievementAdapter.notifyDataSetChanged()
            } else {
                // 提示错误
                Toast.makeText(requireContext(), errorMsg ?: "成就获取失败", Toast.LENGTH_SHORT).show()

                // 如果后端还没通，暂时保留模拟数据
                achievementList.clear()
                achievementList.addAll(fakeData)

                achievementAdapter.notifyDataSetChanged()
            }
        }
    }
    private fun updateStats(current:Int,total:Int){
        tvProgress.text="${current} / ${total}"
        tvUnlocked.text="已解锁 ${current} 个徽章，继续加油 \uD83D\uDCAA"
    }

}