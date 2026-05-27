package com.example.campustask.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.campustask.R
import com.example.campustask.repository.UserRepository
import com.example.campustask.utils.AuthTokenStore
import com.example.campustask.utils.FileUrlResolver

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val userRepo = UserRepository()

    private val avatarPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uploadAvatar(uri)
        } else {
            Toast.makeText(requireContext(), "未选择头像", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUserInfo(view)
        initClick(view)
        initQuickEntry(view)
    }

    /**
     * 用户信息
     */
    private fun initUserInfo(view: View) {

        val imgAvatar = view.findViewById<ImageView>(R.id.img_avatar)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvId = view.findViewById<TextView>(R.id.tv_id)
        val tvPoint = view.findViewById<TextView>(R.id.tv_point)
        val tvCredit = view.findViewById<TextView>(R.id.tv_credit)

        userRepo.getMyInfo(requireContext()) { success, userInfo, error ->

            if (success && userInfo != null) {

                tvName.text = userInfo.username
                tvId.text = "ID: ${userInfo.id}"
                tvPoint.text = userInfo.points.toString()
                tvCredit.text = userInfo.creditScore.toString()

                val imageSource = FileUrlResolver.resolve(userInfo.avatarUrl, defaultType = "avatar")
                Glide.with(this)
                    .load(imageSource)
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .circleCrop()
                    .into(imgAvatar)

            } else {

                Toast.makeText(
                    context,
                    "加载个人信息失败: $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * 我的服务 + 设置
     */
    private fun initClick(view: View) {

        val itemMsg = view.findViewById<LinearLayout>(R.id.item_msg)
        val itemAchievement = view.findViewById<LinearLayout>(R.id.item_achievement)

        val itemTheme = view.findViewById<LinearLayout>(R.id.item_theme)

        val btnLogout = view.findViewById<TextView>(R.id.btn_logout)

        /**
         * 消息通知
         */
        itemMsg.setOnClickListener {

            Toast.makeText(
                context,
                "点击了消息通知",
                Toast.LENGTH_SHORT
            ).show()
        }

        /**
         * 我的成就
         */
        itemAchievement.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AchievementFragment())
                .addToBackStack(null)
                .commit()
        }

        /**
         * 切换主题
         */
        itemTheme.setOnClickListener {

            toggleDarkMode()
        }

        val btnAvatarEdit = view.findViewById<ImageView>(R.id.btn_avatar_edit)
        btnAvatarEdit.setOnClickListener {
            avatarPicker.launch("image/*")
        }

        /**
         * 退出登录
         */
        btnLogout.setOnClickListener {

            // 清理 JWT
            AuthTokenStore.clearToken(requireContext())

            Toast.makeText(
                context,
                "已退出登录",
                Toast.LENGTH_SHORT
            ).show()

            // 跳转登录页
            val intent = Intent(
                requireContext(),
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            requireActivity().finish()
        }
    }

    private fun uploadAvatar(uri: Uri) {
        userRepo.uploadAvatar(requireContext(), uri) { success, response, error ->
            if (success && response != null) {
                Toast.makeText(requireContext(), "头像上传成功", Toast.LENGTH_SHORT).show()
                initUserInfo(requireView())
            } else {
                Toast.makeText(requireContext(), error ?: "头像上传失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 快捷入口
     */
    private fun initQuickEntry(view: View) {

        val btnHistory = view.findViewById<LinearLayout>(R.id.btn_history)
        val btnPoints = view.findViewById<LinearLayout>(R.id.btn_points)
        val btnAchievement = view.findViewById<LinearLayout>(R.id.btn_achievement)
        val btnDark = view.findViewById<LinearLayout>(R.id.btn_dark)

        /**
         * 任务历史
         */
        btnHistory.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    MyTaskFragment.newInstance("OPEN")
                )
                .addToBackStack(null)
                .commit()
        }

        /**
         * 积分明细
         */
        btnPoints.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PointsFragment())
                .addToBackStack(null)
                .commit()
        }

        /**
         * 我的成就
         */
        btnAchievement.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AchievementFragment())
                .addToBackStack(null)
                .commit()
        }

        /**
         * 夜间模式
         */
        btnDark.setOnClickListener {

            toggleDarkMode()
        }
    }

    /**
     * 夜间模式切换
     */
    private fun toggleDarkMode() {

        val sp = requireContext()
            .getSharedPreferences("theme", Context.MODE_PRIVATE)

        val isNight = sp.getBoolean("night", false)

        val newMode = !isNight

        // 保存状态
        sp.edit()
            .putBoolean("night", newMode)
            .apply()

        // 切换模式
        if (newMode) {

            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )

            Toast.makeText(
                context,
                "已切换为夜间模式 🌙",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )

            Toast.makeText(
                context,
                "已切换为日间模式 ☀️",
                Toast.LENGTH_SHORT
            ).show()
        }

        // 刷新页面
        requireActivity().recreate()
    }
}