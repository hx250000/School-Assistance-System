package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.entity.Achievement;
import org.example.back.entity.User;
import org.example.back.entity.UserAchievement;
import org.example.back.repository.AchievementRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {
    @Autowired
    private AchievementService achievementService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my")
    public ApiResponse<UserAchievementOverview> getMyAchievement() {
        return ApiResponse.success(achievementService.getMyAchievement());
    }
    
    /**
     * 管理接口：为所有没有成就记录的用户初始化成就
     * 适用于新增成就定义后，需要为既有用户补齐记录的场景
     * 建议：在实际系统中加上权限验证，确保只有管理员可以调用
     * @return 初始化的用户数
     */
    @PostMapping("/admin/init")
    public ApiResponse<Integer> initializeAllUsers() {
        // TODO: 添加权限验证，确保只有管理员可以调用
        achievementService.initializeAllUsersAchievements();
        int userCount = userRepository.findAll().size();
        return ApiResponse.success(userCount);
    }

    @PostMapping("/admin/recalculate")
    public ApiResponse<Integer> recalculateAllUsers() {
        // TODO: 添加权限验证，确保只有管理员可以调用
        achievementService.recalculateAllUsersAchievements();
        int userCount = userRepository.findAll().size();
        return ApiResponse.success(userCount);
    }

    @PostMapping("/admin/achievement")
    public ApiResponse<Achievement> addAchievement(@RequestBody Achievement achievement) {
        return ApiResponse.success(achievementService.addAchievement(achievement));
    }

    @GetMapping("/admin/list")
    public ApiResponse<List<Achievement>> listAchievements() {
        // TODO: 添加权限验证，确保只有管理员可以调用
        return ApiResponse.success(achievementService.listAll());
    }
}
